package com.rcx.paintgun.misc;

import com.rcx.paintgun.GelUtils;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RepulsionMobEffect extends MobEffect {

	public static double multiplier = 1.04;

	public RepulsionMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		if (!GelUtils.touchingFloor(livingEntity)) {
			Vec3 movement = livingEntity.getDeltaMovement();
			double multiplier = 1.04;
			livingEntity.setDeltaMovement(new Vec3(movement.x * multiplier, movement.y, movement.z * multiplier));
		}
	}

	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
