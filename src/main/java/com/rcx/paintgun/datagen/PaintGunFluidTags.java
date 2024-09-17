package com.rcx.paintgun.datagen;

import java.util.concurrent.CompletableFuture;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;
import com.rcx.paintgun.PaintGunResources.FluidStuff;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;

public class PaintGunFluidTags extends FluidTagsProvider {

	public static TagKey<Fluid> GEL = FluidTags.create(new ResourceLocation(PaintGun.MODID, "gel"));

	public PaintGunFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, PaintGun.MODID, existingFileHelper);
	}

	@Override
	public void addTags(HolderLookup.Provider provider) {
		for (FluidStuff fluid : PaintGunResources.fluidList) {
			tag(FluidTags.create(new ResourceLocation(PaintGun.MODID, fluid.name))).add(fluid.FLUID.get()).add(fluid.FLUID_FLOW.get());
		}
		tag(GEL).add(PaintGunResources.PROPULSION_GEL.FLUID.get(), PaintGunResources.PROPULSION_GEL.FLUID_FLOW.get(), PaintGunResources.REPULSION_GEL.FLUID.get(), PaintGunResources.REPULSION_GEL.FLUID_FLOW.get());
	}
}
