package com.rcx.paintgun.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.joml.Vector3f;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;
import com.rcx.paintgun.block.GelSplatterBlock;
import com.rcx.paintgun.datagen.PaintGunBlockTags;
import com.rcx.paintgun.particle.GelDropParticleOptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class GelProjectile extends Projectile {

	private static final EntityDataAccessor<String> DATA_FLUID = SynchedEntityData.defineId(GelProjectile.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(GelProjectile.class, EntityDataSerializers.INT);

	public GelProjectile(EntityType<? extends Projectile> entityType, Level level) {
		super(entityType, level);
	}

	public GelProjectile(Level level, LivingEntity player, double x, double y, double z, Fluid fluid, int size) {
		this(PaintGunResources.GEL_PROJECTILE.get(), player.level());
		this.setOwner(player);
		this.setPos(x, y, z);
		this.getEntityData().set(DATA_FLUID, BuiltInRegistries.FLUID.getKey(fluid).toString());
		this.getEntityData().set(DATA_SIZE, size);
		this.refreshDimensions();
	}

	@Override
	public void defineSynchedData() {
		this.getEntityData().define(DATA_FLUID, PaintGunResources.PROPULSION_GEL.FLUID.getId().toString());
		this.getEntityData().define(DATA_SIZE, 1);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
		if (DATA_SIZE.equals(pKey)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(pKey);
	}

	@Override
	public EntityDimensions getDimensions(Pose pPose) {
		return super.getDimensions(pPose).scale(this.getSize() * 2.0F + 2.0F);
	}

	public Fluid getFluid() {
		Optional<? extends Holder<Fluid>> optional = this.level().holderLookup(Registries.FLUID).get(ResourceKey.create(Registries.FLUID, new ResourceLocation(getEntityData().get(DATA_FLUID))));
		if (!optional.isEmpty())
			return optional.get().value();
		return PaintGunResources.PROPULSION_GEL.FLUID.get();
	}

	public int getSize() {
		return getEntityData().get(DATA_SIZE);
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == 3) {
			level().playLocalSound(position().x, position().y, position().z, SoundEvents.HONEY_BLOCK_BREAK, getSoundSource(), 0.4f, 0.6f + level().getRandom().nextFloat() * 0.2f, false);
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		super.onHitBlock(result);
		if (result.getType() != HitResult.Type.BLOCK)
			return;
		Direction face = result.getDirection();
		BlockPos pos = result.getBlockPos().relative(face);
		BlockState state = level().getBlockState(pos);
		Block block = PaintGun.gelMap.get(getFluid());
		int size = getSize() - 1;
		boolean spread = false;

		if (state.is(block)) {
			if (state.getValue(MultifaceBlock.getFaceProperty(face.getOpposite()))) {
				size += 1;
				spread = true;
				level().setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_4, 0));
			} else if (GelSplatterBlock.canAttach(level(), face.getOpposite(), result.getBlockPos(), level().getBlockState(result.getBlockPos()))) {
				level().setBlockAndUpdate(pos, state.setValue(BlockStateProperties.AGE_4, 0).setValue(MultifaceBlock.getFaceProperty(face.getOpposite()), true));
				spread = true;
			}
		} else if (state.canBeReplaced() && state.getFluidState().isEmpty() && GelSplatterBlock.canAttach(level(), face.getOpposite(), result.getBlockPos(), level().getBlockState(result.getBlockPos()))) {
			level().setBlockAndUpdate(pos, block.defaultBlockState().setValue(MultifaceBlock.getFaceProperty(face.getOpposite()), true));
			state = level().getBlockState(pos);
			spread = true;
		}

		if (spread && size > 0)
			this.spreadFrom(pos, face, state, getNearests(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z), size);
	}

	public static List<Direction> getNearests(double pX, double pY, double pZ) {
		ArrayList<Direction> output = new ArrayList<Direction>();
		for (Direction direction : Direction.values())
			output.add(direction);

		output.sort(new Comparator<Direction>() {
			@Override
			public int compare(Direction lhs, Direction rhs) {
				double l = pX * (float)lhs.getNormal().getX() + pY * (float)lhs.getNormal().getY() + pZ * (float)lhs.getNormal().getZ();
				double r = pX * (float)rhs.getNormal().getX() + pY * (float)rhs.getNormal().getY() + pZ * (float)rhs.getNormal().getZ();
				return l > r ? -1 : (l < r) ? 1 : 0;
			}
		});

		return output;
	}

	public void spreadFrom(BlockPos pos, Direction face, BlockState state, List<Direction> spreadDirections) {
		this.spreadFrom(pos, face, state, spreadDirections, 1);
	}

	public void spreadFrom(BlockPos pos, Direction face, BlockState state, List<Direction> spreadDirections, int recursion) {
		MultifaceSpreader spreader = ((GelSplatterBlock) state.getBlock()).getSpreader();

		for (int i = 0; i < recursion; ++i) {
			spreadDirections.stream().map((p_221677_) -> {
				return spreader.spreadFromFaceTowardDirection(state, level(), pos, face.getOpposite(), p_221677_, false);
			}).filter(Optional::isPresent).findFirst().orElse(Optional.empty());
		}
	}

	@SuppressWarnings("resource")
	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!this.level().isClientSide) {
			this.level().broadcastEntityEvent(this, (byte)3);
			this.discard();
		} else {
			Vec3 pos = result.getLocation().subtract(getDeltaMovement().normalize().scale(0.22));
			Vector3f direction = getDeltaMovement().toVector3f().mul(-0.12f);
			Vector3f color = IClientFluidTypeExtensions.of(getFluid().getFluidType()).modifyFogColor(Minecraft.getInstance().gameRenderer.getMainCamera(), 0, (ClientLevel) this.level(), 6, 0, new Vector3f(1, 1, 1));
			ParticleOptions drop = new GelDropParticleOptions(color);
			for (int i = 0; i < 8; ++i) {
				float xv = random.nextFloat() * 0.3f - 0.15f;
				float yv = random.nextFloat() * 0.3f - 0.15f;
				float zv = random.nextFloat() * 0.3f - 0.15f;
				level().addParticle(drop, pos.x, pos.y, pos.z, direction.x + xv, direction.y + yv, direction.z + zv);
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		boolean flag = false;
		if (hitresult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
			BlockState blockstate = this.level().getBlockState(blockpos);
			if (blockstate.is(Blocks.NETHER_PORTAL)) {
				this.handleInsidePortal(blockpos);
				flag = true;
			} else if (blockstate.is(Blocks.END_GATEWAY)) {
				BlockEntity blockentity = this.level().getBlockEntity(blockpos);
				if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
					TheEndGatewayBlockEntity.teleportEntity(this.level(), blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
				}
				flag = true;
			} else if (blockstate.is(PaintGunBlockTags.GRATING)) {
				flag = true;
			}
		}

		if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
			this.onHit(hitresult);
		}

		this.checkInsideBlocks();
		Vec3 vec3 = this.getDeltaMovement();
		double d2 = this.getX() + vec3.x;
		double d0 = this.getY() + vec3.y;
		double d1 = this.getZ() + vec3.z;

		this.updateRotation();
		float f;
		if (this.isInWater()) {
			for(int i = 0; i < 4; ++i) {
				float f1 = 0.25F;
				this.level().addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * f1, d0 - vec3.y * f1, d1 - vec3.z * f1, vec3.x, vec3.y, vec3.z);
			}

			f = 0.8F;
		} else {
			f = 0.99F;
		}

		this.setDeltaMovement(vec3.scale((double)f));
		if (!this.isNoGravity()) {
			Vec3 vec31 = this.getDeltaMovement();
			this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
		}

		this.setPos(d2, d0, d1);
	}

	public float getGravity() {
		return 0.14F;
	}
}
