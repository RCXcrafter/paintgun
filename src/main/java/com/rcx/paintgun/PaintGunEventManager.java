package com.rcx.paintgun;

import com.rcx.paintgun.block.RepulsionGelSplatterBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class PaintGunEventManager {

	public static Double fallBounceMultiplier = 9.5;

	public static final VoxelShape DOWN_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);

	public static void onLivingFall(LivingFallEvent event) {
		if (event.getEntity().hasEffect(PaintGunResources.REPULSION_EFFECT.get()))
			event.getEntity().removeEffect(PaintGunResources.REPULSION_EFFECT.get());

		if (event.getEntity().isSuppressingBounce())
			return;

		Iterable<VoxelShape> collisions = GelUtils.getGelCollisions(event.getEntity().level(), event.getEntity(), event.getEntity().getBoundingBox(), PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK.get());

		for (VoxelShape shape : collisions) {
			if (Shapes.joinIsNotEmpty(DOWN_AABB, shape, BooleanOp.AND)) {
				Vec3 movement = event.getEntity().getDeltaMovement();

				event.getEntity().setDeltaMovement(new Vec3(movement.x * RepulsionGelSplatterBlock.multiplier, Math.max(RepulsionGelSplatterBlock.minMotion, 0.42 + 0.1 * (event.getEntity().fallDistance * 1.0 + 1.0)), movement.z * RepulsionGelSplatterBlock.multiplier));
				event.getEntity().hurtMarked = true;
				event.setCanceled(true);
				break;
			}
		}
	}
}
