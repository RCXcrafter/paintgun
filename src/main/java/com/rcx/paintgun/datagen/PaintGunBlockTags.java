package com.rcx.paintgun.datagen;

import java.util.concurrent.CompletableFuture;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PaintGunBlockTags extends BlockTagsProvider {

	public static final TagKey<Block> GRATING = BlockTags.create(new ResourceLocation(PaintGun.MODID, "grating"));
	public static final TagKey<Block> SQUID_GRATING = BlockTags.create(new ResourceLocation("squidgames", "grating"));
	public static final TagKey<Block> GEL_BLACKLIST = BlockTags.create(new ResourceLocation(PaintGun.MODID, "gel_blacklist"));
	public static final TagKey<Block> GEL = BlockTags.create(new ResourceLocation(PaintGun.MODID, "gel"));

	public PaintGunBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, PaintGun.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(GRATING)
		.addTags(Tags.Blocks.FENCES, Tags.Blocks.FENCE_GATES, BlockTags.LEAVES, BlockTags.CANDLES)
		.add(Blocks.IRON_BARS, Blocks.CHAIN, Blocks.SCAFFOLDING, Blocks.MANGROVE_ROOTS, Blocks.POINTED_DRIPSTONE, Blocks.SEA_PICKLE, Blocks.BAMBOO, Blocks.AZALEA, Blocks.FLOWERING_AZALEA, Blocks.ACACIA_DOOR)
		.add(Blocks.OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.WARPED_TRAPDOOR, Blocks.CRIMSON_TRAPDOOR, Blocks.CHERRY_TRAPDOOR, Blocks.MANGROVE_TRAPDOOR)
		.add(Blocks.SMALL_AMETHYST_BUD, Blocks.MEDIUM_AMETHYST_BUD, Blocks.LARGE_AMETHYST_BUD, Blocks.AMETHYST_CLUSTER)
		.addOptionalTag(SQUID_GRATING.location());
		tag(GEL_BLACKLIST).addTags(GRATING, Tags.Blocks.GLASS, BlockTags.ICE)
		.add(Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
		tag(GEL).add(PaintGunResources.PROPULSION_GEL_SPLATTER_BLOCK.get(), PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK.get());
	}
}
