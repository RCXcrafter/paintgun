package com.rcx.paintgun.block;

import java.util.HashSet;

import com.rcx.paintgun.datagen.PaintGunBlockTags;
import com.rcx.paintgun.datagen.PaintGunItemTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GelSplatterBlock extends MultifaceBlock {

	public final MultifaceSpreader spreader = new MultifaceSpreader(new GelSpreaderConfig(this));

	public GelSplatterBlock(BlockBehaviour.Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(defaultBlockState().setValue(BlockStateProperties.AGE_4, 0));
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		return true;
	}

	public VoxelShape getGelShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return super.getShape(pState, pLevel, pPos, pContext);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = state.getValue(BlockStateProperties.AGE_4);
		if (age >= 4 || level.isRainingAt(pos)) {
			level.removeBlock(pos, false);
			return;
		}
		level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_4, age + 1));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.AGE_4);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.is(PaintGunItemTags.INK_CLEANER)) {
			if (!level.isClientSide) {
				HashSet<MultifaceSpreader.SpreadPos> positions = new HashSet<MultifaceSpreader.SpreadPos>();
				positions.add(new MultifaceSpreader.SpreadPos(pos, hit.getDirection().getOpposite()));
				HashSet<MultifaceSpreader.SpreadPos> previousPositions = positions;

				for (int i = 0; i < 4; ++i) {
					HashSet<MultifaceSpreader.SpreadPos> newPositions = new HashSet<MultifaceSpreader.SpreadPos>();
					for (MultifaceSpreader.SpreadPos position : previousPositions) {
						BlockState currentState = level.getBlockState(position.pos());

						for (Direction direction : Direction.values()) {
							if (MultifaceBlock.hasFace(currentState, position.face())) {
								for (SpreadType spreadType : SpreadType.values()) {
									MultifaceSpreader.SpreadPos spreadPos = spreadType.getSpreadPos(position.pos(), direction, position.face());
									BlockState spreadState = level.getBlockState(spreadPos.pos());

									if (spreadState.is(PaintGunBlockTags.GEL) && MultifaceBlock.hasFace(spreadState, spreadPos.face())) {
										newPositions.add(spreadPos);
									}
								}
							}
						}
					}
					newPositions.removeAll(positions);
					previousPositions = newPositions;
					positions.addAll(newPositions);
				}

				for (MultifaceSpreader.SpreadPos position : positions) {
					level.destroyBlock(position.pos(), false, player);
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public MultifaceSpreader getSpreader() {
		return spreader;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
		if (!hasAnyFace(pState)) {
			return Blocks.AIR.defaultBlockState();
		} else {
			return hasFace(pState, pDirection) && !canAttach(pLevel, pDirection, pNeighborPos, pNeighborState) ? removeFace(pState, getFaceProperty(pDirection)) : pState;
		}
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		boolean flag = false;

		for (Direction direction : DIRECTIONS) {
			if (hasFace(pState, direction)) {
				BlockPos blockpos = pPos.relative(direction);
				if (!canAttach(pLevel, direction, blockpos, pLevel.getBlockState(blockpos))) {
					return false;
				}
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean isValidStateForPlacement(BlockGetter pLevel, BlockState pState, BlockPos pPos, Direction pDirection) {
		if (this.isFaceSupported(pDirection)) {
			BlockPos blockpos = pPos.relative(pDirection);
			return canAttach(pLevel, pDirection, blockpos, pLevel.getBlockState(blockpos));
		} else {
			return false;
		}
	}

	public static boolean canAttach(BlockGetter pLevel, Direction pDirection, BlockPos pPos, BlockState pState) {
		return !pState.is(PaintGunBlockTags.GEL_BLACKLIST) && MultifaceBlock.canAttachTo(pLevel, pDirection, pPos, pState);
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	public static class GelSpreaderConfig extends MultifaceSpreader.DefaultSpreaderConfig {

		public GelSpreaderConfig(MultifaceBlock block) {
			super(block);
		}

		@Override
		public boolean stateCanBeReplaced(BlockGetter level, BlockPos pos, BlockPos spreadPos, Direction direction, BlockState state) {
			return (state.isAir() || state.is(this.block) || state.canBeReplaced()) && state.getFluidState().isEmpty();
		}

		@Override
		public boolean placeBlock(LevelAccessor level, MultifaceSpreader.SpreadPos pos, BlockState state, boolean markForPostprocessing) {
			BlockState blockstate = this.getStateForPlacement(state, level, pos.pos(), pos.face());
			if (blockstate != null) {
				if (markForPostprocessing) {
					level.getChunk(pos.pos()).markPosForPostprocessing(pos.pos());
				}
				boolean replaced = false;
				if (level.setBlock(pos.pos(), blockstate, 2)) {
					replaced = true;
				}
				return replaced;
			} else {
				return false;
			}
		}
	}

	public static enum SpreadType {
		SAME_POSITION {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos, face);
			}
		},
		SAME_PLANE {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos.relative(face), spreadDirection);
			}
		},
		WRAP_AROUND {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos.relative(face).relative(spreadDirection), face.getOpposite());
			}
		},
		DIAGONAL {
			public MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection) {
				return new MultifaceSpreader.SpreadPos(pos.relative(face).relative(face.getClockWise(spreadDirection.getAxis())), spreadDirection);
			}
		};

		public abstract MultifaceSpreader.SpreadPos getSpreadPos(BlockPos pos, Direction face, Direction spreadDirection);
	}
}
