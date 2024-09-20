package com.rcx.paintgun.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.rcx.paintgun.datagen.PaintGunBlockTags;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class SplatterBakedModel implements BakedModel {

	public static BooleanProperty[] properties = {
			BlockStateProperties.DOWN,
			BlockStateProperties.UP,
			BlockStateProperties.NORTH,
			BlockStateProperties.SOUTH,
			BlockStateProperties.WEST,
			BlockStateProperties.EAST,
	};
	public static final List<BakedQuad> EMPTY = new ArrayList<BakedQuad>();
	public static final ModelProperty<int[][][][]> DATA_TYPE = new ModelProperty<int[][][][]>();
	public static final ModelProperty<BakedModel> LOGGED_BLOCK_MODEL = new ModelProperty<BakedModel>();
	public static final ModelProperty<BlockState> LOGGED_BLOCK = new ModelProperty<BlockState>();
	public static final int OPEN = 0;
	public static final int SOLID = 1;
	public static final int INK = 2;
	public static final int BLOCKING = 3;
	public static Vec3i center = new Vec3i(1, 1, 1);

	public final HashMap<int[][][][], List<BakedQuad>> QUAD_CACHE = new HashMap<int[][][][], List<BakedQuad>>();
	private final BakedModel[][][][][] models;

	public SplatterBakedModel(BakedModel[][][][][] models) {
		this.models = models;
	}

	public static int getCacheIndex(int[] data) {
		return (((((data[0] * 3 + data[1]) * 3 + data[2]) * 3 + data[3]) * 3 + data[4]) * 3) + data[5];
	}

	@Override
	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
		if (!data.has(LOGGED_BLOCK_MODEL))
			return ChunkRenderTypeSet.of(RenderType.cutout());

		ArrayList<RenderType> types = new ArrayList<RenderType>();
		types.addAll(data.get(LOGGED_BLOCK_MODEL).getRenderTypes(data.get(LOGGED_BLOCK), rand, data).asList());
		if (!types.contains(RenderType.cutout()))
			types.add(RenderType.cutout());
		return ChunkRenderTypeSet.of(types);
	}

	@Override
	public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
		int[][][][] area = new int[3][3][3][6];

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				for (int z = 0; z < 3; z++) {
					BlockPos blockPos = pos.offset(x - 1, y - 1, z - 1);
					BlockState blockState = level.getBlockState(blockPos);
					for (int d = 0; d < 6; d++) {
						if (!blockState.is(PaintGunBlockTags.GRATING) && blockState.isFaceSturdy(level, blockPos, Direction.from3DDataValue(d))) {
							if (blockState.is(PaintGunBlockTags.GEL_BLACKLIST))
								area[x][y][z][d] = BLOCKING;
							else
								area[x][y][z][d] = SOLID;
						} else if (blockState.is(PaintGunBlockTags.GEL) && blockState.getValue(properties[d])) {
							area[x][y][z][d] = INK;
						} else {
							area[x][y][z][d] = OPEN;
						}
					}
				}
			}
		}
		return modelData.derive().with(DATA_TYPE, area).build();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (renderType != RenderType.cutout())
			return addLoggedQuads(EMPTY, state, side, rand, data, renderType);
		if (side != null)
			return EMPTY;

		int[][][][] sides = data.get(DATA_TYPE);

		if (sides != null) {
			List<BakedQuad> quads = QUAD_CACHE.getOrDefault(sides, null);
			if (quads != null)
				return addLoggedQuads(quads, state, side, rand, data, renderType);

			quads = new ArrayList<BakedQuad>();
			for (int i = 0; i < 6; i++) {
				if (sides[1][1][1][i] == INK) {
					quads.addAll(models[4][1][1][1][i].getQuads(state, side, rand, data, renderType));
				}
			}
			if (quads.isEmpty())
				return quads;

			for (Direction down : Direction.values()) {
				Direction up = relative(down, Direction.UP);
				Direction north = relative(down, Direction.NORTH);
				Direction south = relative(down, Direction.SOUTH);
				Direction west = relative(down, Direction.WEST);
				Direction east = relative(down, Direction.EAST);
				Direction[] horizontals = {
						north,
						east,
						south,
						west
				};

				if (getPos(sides, center)[down.get3DDataValue()] == INK) {
					for (int d = 0; d < horizontals.length; d++) {
						Direction direction = horizontals[d];
						Direction next = horizontals[(d + 1) % horizontals.length];
						Direction previous = next.getOpposite();

						//edges
						if (getPos(sides, center)[direction.get3DDataValue()] != INK) {
							int face1 = getPos(sides, center.relative(direction))[direction.getOpposite().get3DDataValue()];
							if (face1 == SOLID) {
								quads.addAll(getPos(models[getEdge(direction, up)], center)[direction.get3DDataValue()].getQuads(state, side, rand, data, renderType));
							} else if (face1 != BLOCKING && getPos(sides, center.relative(direction))[down.get3DDataValue()] != INK) {
								int face2 = getPos(sides, center.relative(direction).relative(down))[up.get3DDataValue()];
								if (face2 == SOLID) {
									quads.addAll(getPos(models[getEdge(down, direction)], center.relative(direction))[down.get3DDataValue()].getQuads(state, side, rand, data, renderType));
								} else if (face2 != BLOCKING && getPos(sides, center.relative(direction).relative(down))[direction.getOpposite().get3DDataValue()] != INK) {
									if (getPos(sides, center.relative(down))[direction.get3DDataValue()] == SOLID) {
										quads.addAll(getPos(models[getEdge(direction.getOpposite(), down)], center.relative(direction).relative(down))[direction.getOpposite().get3DDataValue()].getQuads(state, side, rand, data, renderType));
									}
								}
							}
						}
						//corners
						boolean corner = true;
						if (getPos(sides, center)[direction.get3DDataValue()] != INK) {
							if (getPos(sides, center.relative(direction))[direction.getOpposite().get3DDataValue()] == OPEN) {
								if (getPos(sides, center.relative(direction).relative(next))[next.getOpposite().get3DDataValue()] == SOLID) {
									corner = false;
									quads.addAll(getPos(models[getCorner(next, direction, up)], center.relative(direction))[next.get3DDataValue()].getQuads(state, side, rand, data, renderType));
								}
								if (getPos(sides, center.relative(direction).relative(down))[up.get3DDataValue()] == OPEN) {
									if (getPos(sides, center.relative(direction).relative(down).relative(next))[previous.get3DDataValue()] == SOLID) {
										quads.addAll(getPos(models[getCorner(next, direction, down)], center.relative(direction).relative(down))[next.get3DDataValue()].getQuads(state, side, rand, data, renderType));
									}
								}
							}
							if (getPos(sides, center.relative(direction).relative(next))[direction.getOpposite().get3DDataValue()] == SOLID) {
								corner = false;
								if (getPos(sides, center.relative(next))[next.getOpposite().get3DDataValue()] == OPEN) {
									quads.addAll(getPos(models[getCorner(direction, up, next)], center.relative(next))[direction.get3DDataValue()].getQuads(state, side, rand, data, renderType));
								}
							}
							if (getPos(sides, center.relative(next))[down.get3DDataValue()] != INK && getPos(sides, center.relative(direction).relative(down).relative(next))[direction.getOpposite().get3DDataValue()] == OPEN && getPos(sides, center.relative(down).relative(next))[direction.get3DDataValue()] == SOLID) {
								corner = false;
								quads.addAll(getPos(models[getCorner(direction.getOpposite(), next, down)], center.relative(direction).relative(next).relative(down))[direction.getOpposite().get3DDataValue()].getQuads(state, side, rand, data, renderType));
							}
							if (getPos(sides, center.relative(direction))[down.get3DDataValue()] != INK && getPos(sides, center.relative(direction).relative(down).relative(next))[next.getOpposite().get3DDataValue()] == OPEN && getPos(sides, center.relative(down).relative(direction))[next.get3DDataValue()] == SOLID) {
								corner = false;
								quads.addAll(getPos(models[getCorner(next.getOpposite(), direction, down)], center.relative(direction).relative(next).relative(down))[next.getOpposite().get3DDataValue()].getQuads(state, side, rand, data, renderType));
							}

							if (getPos(sides, center)[next.get3DDataValue()] != INK) {
								if (getPos(sides, center.relative(direction))[direction.getOpposite().get3DDataValue()] == OPEN || getPos(sides, center.relative(next))[previous.get3DDataValue()] == OPEN) {
									if (getPos(sides, center.relative(direction).relative(next))[next.getOpposite().get3DDataValue()] == OPEN && getPos(sides, center.relative(direction))[next.get3DDataValue()] == SOLID) {
										quads.addAll(getPos(models[getCorner(previous, direction, up)], center.relative(direction).relative(next))[previous.get3DDataValue()].getQuads(state, side, rand, data, renderType));
									}
									if (getPos(sides, center.relative(direction).relative(next))[direction.getOpposite().get3DDataValue()] == OPEN && getPos(sides, center.relative(next))[direction.get3DDataValue()] == SOLID) {
										quads.addAll(getPos(models[getCorner(direction.getOpposite(), next, up)], center.relative(direction).relative(next))[direction.getOpposite().get3DDataValue()].getQuads(state, side, rand, data, renderType));
									}
								} else {
									corner = false;
								}
							}
						}
						if (getPos(sides, center.relative(down).relative(next))[direction.get3DDataValue()] == OPEN && getPos(sides, center.relative(direction).relative(down).relative(next))[direction.getOpposite().get3DDataValue()] == SOLID) {
							quads.addAll(getPos(models[getCorner(direction, down, next)], center.relative(down).relative(next))[direction.get3DDataValue()].getQuads(state, side, rand, data, renderType));
						}
						if (corner && getPos(sides, center.relative(down).relative(direction).relative(next))[down.getOpposite().get3DDataValue()] == SOLID) {
							if (getPos(sides, center.relative(direction))[down.get3DDataValue()] != INK && getPos(sides, center.relative(next))[down.get3DDataValue()] != INK && getPos(sides, center.relative(direction).relative(next))[down.get3DDataValue()] != INK) {
								quads.addAll(getPos(models[getCorner(down, direction, next)], center.relative(direction).relative(next))[down.get3DDataValue()].getQuads(state, side, rand, data, renderType));
							}
						}
					}
				}
			}

			if (!quads.isEmpty())
				QUAD_CACHE.put(sides, quads);
			return addLoggedQuads(quads, state, side, rand, data, renderType);
		}
		return addLoggedQuads(getQuads(state, side, rand), state, side, rand, data, renderType);
	}

	public List<BakedQuad> addLoggedQuads(List<BakedQuad> quads, BlockState state, Direction side, RandomSource rand, ModelData data, RenderType renderType) {
		if (!data.has(LOGGED_BLOCK_MODEL))
			return quads;

		List<BakedQuad> newQuads = new ArrayList<BakedQuad>();
		newQuads.addAll(data.get(LOGGED_BLOCK_MODEL).getQuads(data.get(LOGGED_BLOCK), side, rand, data, renderType));
		newQuads.addAll(quads);
		return newQuads;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		for (int i = 0; i < properties.length; i++) {
			if (state.getValue(properties[i])) {
				quads.addAll(models[4][1][1][1][i].getQuads(state, side, rand));
			}
		}
		return quads;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return models[4][1][1][1][2].useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return models[4][1][1][1][2].isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return models[4][1][1][1][2].usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return models[4][1][1][1][2].isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return models[4][1][1][1][2].getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}

	public static <T extends Object> T getPos(T[][][] array, Vec3i pos) {
		return array[pos.getX()][pos.getY()][pos.getZ()];
	}

	public static boolean compareIntPair(int left1, int right1, int left2, int right2) {
		return (left1 == left2 && right1 == right2) || (left1 == right2 && right1 == left2);
	}

	public static int getCorner(Direction base, Direction direction1, Direction direction2) {
		int edge1 = getEdge(base, direction1);
		int edge2 = getEdge(base, direction2);

		if (compareIntPair(edge1, edge2, 1, 3))
			return 0;
		if (compareIntPair(edge1, edge2, 1, 5))
			return 2;
		if (compareIntPair(edge1, edge2, 7, 5))
			return 8;
		if (compareIntPair(edge1, edge2, 7, 3))
			return 6;
		return 4;
	}

	public static int getEdge(Direction base, Direction direction) {
		switch (direction) {
		case DOWN:
			return 7;
		case UP:
			return 1;
		case NORTH:
			switch (base) {
			case DOWN:
				return 1;
			case EAST:
				return 3;
			case WEST:
				return 5;
			default:
				return 7;
			}
		case SOUTH:
			switch (base) {
			case DOWN:
				return 7;
			case EAST:
				return 5;
			case WEST:
				return 3;
			default:
				return 1;
			}
		case WEST:
			switch (base) {
			case SOUTH:
				return 5;
			default:
				return 3;
			}
		case EAST:
			switch (base) {
			case SOUTH:
				return 3;
			default:
				return 5;
			}
		}
		return 4;
	}

	public static Direction relative(Direction anchor, Direction direction) {
		switch (anchor) {
		case DOWN:
		default:
			return direction;
		case UP:
			switch (direction) {
			case DOWN:
				return Direction.UP;
			case UP:
				return Direction.DOWN;
			case NORTH:
				return Direction.SOUTH;
			case SOUTH:
				return Direction.NORTH;
			case WEST:
				return Direction.WEST;
			case EAST:
				return Direction.EAST;
			}
		case NORTH:
			switch (direction) {
			case DOWN:
				return Direction.NORTH;
			case UP:
				return Direction.SOUTH;
			case NORTH:
				return Direction.UP;
			case SOUTH:
				return Direction.DOWN;
			case WEST:
				return Direction.WEST;
			case EAST:
				return Direction.EAST;
			}
		case SOUTH:
			switch (direction) {
			case DOWN:
				return Direction.SOUTH;
			case UP:
				return Direction.NORTH;
			case NORTH:
				return Direction.DOWN;
			case SOUTH:
				return Direction.UP;
			case WEST:
				return Direction.WEST;
			case EAST:
				return Direction.EAST;
			}
		case WEST:
			switch (direction) {
			case DOWN:
				return Direction.WEST;
			case UP:
				return Direction.EAST;
			case NORTH:
				return Direction.NORTH;
			case SOUTH:
				return Direction.SOUTH;
			case WEST:
				return Direction.UP;
			case EAST:
				return Direction.DOWN;
			}
		case EAST:
			switch (direction) {
			case DOWN:
				return Direction.EAST;
			case UP:
				return Direction.WEST;
			case NORTH:
				return Direction.NORTH;
			case SOUTH:
				return Direction.SOUTH;
			case WEST:
				return Direction.DOWN;
			case EAST:
				return Direction.UP;
			}
		}
		return direction;
	}
}
