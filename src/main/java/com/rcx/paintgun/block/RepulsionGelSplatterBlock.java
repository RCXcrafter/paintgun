package com.rcx.paintgun.block;

import com.rcx.paintgun.PaintGunResources;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RepulsionGelSplatterBlock extends GelSplatterBlock {

	public static Double multiplier = 1.3;
	public static Double downBounceMultiplier = 1.32;
	public static Double bounceMultiplier = 1.3;
	public static Double boostMotion = 0.75;
	public static Double minMotion = 0.65;
	public static Double maxMotion = 1.0;

	public RepulsionGelSplatterBlock(BlockBehaviour.Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof ServerPlayer player) {
			if (!entity.isSuppressingBounce() && state.getValue(BlockStateProperties.DOWN)) {
				if (Shapes.joinIsNotEmpty(DOWN_AABB.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(entity.getBoundingBox()), BooleanOp.AND)) {
					player.addEffect(new MobEffectInstance(MobEffects.JUMP, 3, 3, true, false));
					if (player.hasEffect(PaintGunResources.PROPULSION_EFFECT.get()))
						player.addEffect(new MobEffectInstance(PaintGunResources.REPULSION_EFFECT.get(), 40, 0, true, false));
				}
			}
		} else if (!entity.isSuppressingBounce() && entity instanceof LivingEntity living) {
			VoxelShape hitbox = Shapes.create(entity.getBoundingBox()).move(-pos.getX(), -pos.getY(), -pos.getZ());
			Vec3 motion = entity.getDeltaMovement();
			//Vec3 deltaPos = entity.position().subtract(entity.xo, entity.yo, entity.zo);
			//motion = deltaPos;
			//if (GelUtils.touchingFloor(entity))
			//motion = new Vec3(motion.x, 0.0, motion.z);
			hitbox = Shapes.or(hitbox, hitbox.move(motion.x / 3.0, motion.y / 3.0, motion.z / 3.0), hitbox.move(motion.x / 1.5, motion.y / 1.5, motion.z / 1.5), hitbox.move(motion.x, motion.y, motion.z));
			boolean update = false;
			for (Direction face : Direction.values()) {
				if (!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(face)))
					continue;

				boolean boost = false;

				switch (face.getAxis()) { //only bounce if the player is moving towards this face of the gel
				case X:
					if (entity.getDeltaMovement().x * face.getNormal().getX() <= 0)
						continue;
					break;
				case Z:
					if (entity.getDeltaMovement().z * face.getNormal().getZ() <= 0)
						continue;
					break;
				case Y:
				default:
					if (entity.getDeltaMovement().y * face.getNormal().getY() <= 0) {
						if (face == Direction.DOWN && Math.sqrt(entity.getDeltaMovement().x * entity.getDeltaMovement().x + entity.getDeltaMovement().z * entity.getDeltaMovement().z) > 0.8) {
							boost = true;
						} else {
							continue;
						}
					}
					break;
				}

				if (Shapes.joinIsNotEmpty(SHAPE_BY_DIRECTION.get(face), hitbox, BooleanOp.AND)) {
					switch (face.getAxis()) {
					case X:
						motion = new Vec3(Math.min(maxMotion, Math.max(minMotion, Math.abs(motion.x * bounceMultiplier))) * -Math.signum(motion.x), motion.y + 0.25, motion.z * multiplier);
						update = true;
						break;
					case Z:
						motion = new Vec3(motion.x * multiplier, motion.y + 0.25, Math.min(maxMotion, Math.max(minMotion, Math.abs(motion.z * bounceMultiplier))) * -Math.signum(motion.z));
						update = true;
						break;
					case Y:
					default:
						if (boost) {
							motion = new Vec3(motion.x, boostMotion, motion.z);
						} else {
							if (face == Direction.DOWN) {
								motion = new Vec3(motion.x * multiplier, Math.max(minMotion, Math.abs(motion.y * downBounceMultiplier)) * -Math.signum(motion.y), motion.z * multiplier);
							} else {
								motion = new Vec3(motion.x * multiplier, Math.min(maxMotion, Math.max(minMotion, Math.abs(motion.y * bounceMultiplier))) * -Math.signum(motion.y), motion.z * multiplier);
							}
						}
						update = true;
						break;
					}
				}
			}
			if (update) {
				entity.setDeltaMovement(motion);
			}
		}
	}

	/*@Override
	public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {

		System.out.println(entityIn + " is falling on gel");
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entity) {
		System.out.println(entity + " has fallen on gel");
		if (entity.isSuppressingBounce() || !(entity instanceof LivingEntity) && !(entity instanceof ItemEntity)) {
			super.updateEntityAfterFallOn(worldIn, entity);
			// this is mostly needed to prevent XP orbs from bouncing. which completely breaks the game.
			return;
		}

		Vec3 vec3d = entity.getDeltaMovement();

		if (vec3d.y < 0) {
			double speed = entity instanceof LivingEntity ? 1.0D : 0.8D;
			entity.setDeltaMovement(vec3d.x, -vec3d.y * speed, vec3d.z);
			entity.fallDistance = 0;
			if (entity instanceof ItemEntity) {
				entity.setOnGround(false);
			}
		} else {
			super.updateEntityAfterFallOn(worldIn, entity);
		}
	}*/
}
