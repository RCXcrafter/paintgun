package com.rcx.paintgun.misc;

import java.util.function.BiFunction;

import com.rcx.paintgun.block.GelSplatterBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GelCollisions<T> extends BlockCollisions<T> {

	public Block block;

	public GelCollisions(CollisionGetter pCollisionGetter, Entity pEntity, AABB pBox, boolean pOnlySuffocatingBlocks, Block block, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> pResultProvider) {
		super(pCollisionGetter, pEntity, pBox, pOnlySuffocatingBlocks, pResultProvider);
		this.block = block;
	}

	protected T computeNext() {
		while (true) {
			if (this.cursor.advance()) {
				int i = this.cursor.nextX();
				int j = this.cursor.nextY();
				int k = this.cursor.nextZ();
				int l = this.cursor.getNextType();
				if (l == 3) {
					continue;
				}

				BlockGetter blockgetter = this.getChunk(i, k);
				if (blockgetter == null) {
					continue;
				}

				this.pos.set(i, j, k);
				BlockState blockstate = blockgetter.getBlockState(this.pos);

				if (!blockstate.is(block))
					continue;

				if (this.onlySuffocatingBlocks && !blockstate.isSuffocating(blockgetter, this.pos) || l == 1 && !blockstate.hasLargeCollisionShape()) {
					continue;
				}

				VoxelShape voxelshape = ((GelSplatterBlock) blockstate.getBlock()).getGelShape(blockstate, blockgetter, this.pos, this.context);
				if (voxelshape == Shapes.block()) {
					if (!this.box.intersects((double)i, (double)j, (double)k, (double)i + 1.0D, (double)j + 1.0D, (double)k + 1.0D)) {
						continue;
					}

					return this.resultProvider.apply(this.pos, voxelshape.move((double)i, (double)j, (double)k));
				}

				VoxelShape voxelshape1 = voxelshape.move((double)i, (double)j, (double)k);
				if (voxelshape1.isEmpty() || !Shapes.joinIsNotEmpty(voxelshape1, this.entityShape, BooleanOp.AND)) {
					continue;
				}

				return this.resultProvider.apply(this.pos, voxelshape1);
			}

			return this.endOfData();
		}
	}
}
