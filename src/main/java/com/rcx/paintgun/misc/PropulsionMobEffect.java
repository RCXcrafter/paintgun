package com.rcx.paintgun.misc;

import com.rcx.paintgun.GelUtils;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class PropulsionMobEffect extends MobEffect {

	public PropulsionMobEffect(MobEffectCategory category, int color) {
		super(category, color);
	}

	public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
		Vec3 movement = livingEntity.getDeltaMovement();
		double multiplier = getMultiplier(movement.length());
		if (!GelUtils.touchingFloor(livingEntity)) {
			multiplier = 1.0 + (multiplier - 1.0) * 0.5;
		}
		livingEntity.setDeltaMovement(new Vec3(movement.x * multiplier, movement.y, movement.z * multiplier));
	}

	public static double getMultiplier(double length) {
		return 7.0 / (length + 9.0) + 1.0 - 0.01 / (length + 0.015);
	}

	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
