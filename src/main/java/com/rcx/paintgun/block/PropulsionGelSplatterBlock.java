package com.rcx.paintgun.block;

import com.rcx.paintgun.PaintGunResources;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

public class PropulsionGelSplatterBlock extends GelSplatterBlock {

	public PropulsionGelSplatterBlock(BlockBehaviour.Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity living && state.getValue(BlockStateProperties.DOWN)) {
			if (Shapes.joinIsNotEmpty(DOWN_AABB.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(entity.getBoundingBox()), BooleanOp.AND)) {
				living.addEffect(new MobEffectInstance(PaintGunResources.PROPULSION_EFFECT.get(), 3, 0, true, false));
			}
		}
	}
}
