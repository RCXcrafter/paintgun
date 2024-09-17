package com.rcx.paintgun.datagen;

import com.rcx.paintgun.PaintGun;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;

public class PaintGunLootModifiers extends GlobalLootModifierProvider {

	public PaintGunLootModifiers(PackOutput output) {
		super(output, PaintGun.MODID);
	}

	@Override
	protected void start() {
		
	}
}
