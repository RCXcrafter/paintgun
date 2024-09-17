package com.rcx.paintgun.datagen;

import java.util.concurrent.CompletableFuture;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PaintGunItemTags extends ItemTagsProvider {

	public static final TagKey<Item> FLUID_GUN = ItemTags.create(new ResourceLocation(PaintGun.MODID, "fluid_gun"));
	public static final TagKey<Item> INK_CLEANER = ItemTags.create(new ResourceLocation(PaintGun.MODID, "ink_cleaner"));

	public PaintGunItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagProvider, PaintGun.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(FLUID_GUN).add(PaintGunResources.PAINT_GUN.get());

		tag(INK_CLEANER).add(Items.WET_SPONGE);
	}
}
