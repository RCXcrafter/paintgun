package com.rcx.paintgun.datagen;

import java.util.List;
import java.util.Set;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class PaintGunLootTables extends LootTableProvider {

	public PaintGunLootTables(PackOutput output) {
		super(output, Set.of(), List.of(
				new LootTableProvider.SubProviderEntry(PaintGunBlockLootTables::new, LootContextParamSets.BLOCK)
				));
	}
}
