package com.rcx.paintgun.datagen;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class PaintGunBlockLootTables extends BlockLootSubProvider {

	public PaintGunBlockLootTables() {
		super(Set.of(), FeatureFlags.VANILLA_SET);
	}

	@Nonnull
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ForgeRegistries.BLOCKS.getValues().stream()
				.filter((block) -> PaintGun.MODID.equals(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getNamespace()))
				.collect(Collectors.toList());
	}

	@Override
	protected void generate() {
		add(PaintGunResources.PROPULSION_GEL_SPLATTER_BLOCK.get(), noDrop());
		add(PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK.get(), noDrop());
	}
}
