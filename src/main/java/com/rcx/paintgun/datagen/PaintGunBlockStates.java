package com.rcx.paintgun.datagen;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;
import com.rcx.paintgun.PaintGunResources.FluidStuff;
import com.rcx.paintgun.render.SplatterModelBuilder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class PaintGunBlockStates extends BlockStateProvider {

	public PaintGunBlockStates(PackOutput gen, ExistingFileHelper exFileHelper) {
		super(gen, PaintGun.MODID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		//this is just to give them proper particles
		for (FluidStuff fluid : PaintGunResources.fluidList) {
			fluid(fluid.FLUID_BLOCK, fluid.name);
		}

		ModelFile propulsionSplatterModel = models().withExistingParent("propulsion_gel_splatter", "block")
				.customLoader(SplatterModelBuilder::begin)
				.parts(getGelSplatterPart("splatter_top_left", "propulsion_gel"),
						getGelSplatterPart("splatter_top", "propulsion_gel"),
						getGelSplatterPart("splatter_top_right", "propulsion_gel"),
						getGelSplatterPart("splatter_left", "propulsion_gel"),
						getGelSplatterPart("splatter_center", "propulsion_gel"),
						getGelSplatterPart("splatter_right", "propulsion_gel"),
						getGelSplatterPart("splatter_bottom_left", "propulsion_gel"),
						getGelSplatterPart("splatter_bottom", "propulsion_gel"),
						getGelSplatterPart("splatter_bottom_right", "propulsion_gel")).end().renderType("cutout");
		simpleBlock(PaintGunResources.PROPULSION_GEL_SPLATTER_BLOCK.get(), propulsionSplatterModel);
		ModelFile repulsionSplatterModel = models().withExistingParent("repulsion_gel_splatter", "block")
				.customLoader(SplatterModelBuilder::begin)
				.parts(getGelSplatterPart("splatter_top_left", "repulsion_gel"),
						getGelSplatterPart("splatter_top", "repulsion_gel"),
						getGelSplatterPart("splatter_top_right", "repulsion_gel"),
						getGelSplatterPart("splatter_left", "repulsion_gel"),
						getGelSplatterPart("splatter_center", "repulsion_gel"),
						getGelSplatterPart("splatter_right", "repulsion_gel"),
						getGelSplatterPart("splatter_bottom_left", "repulsion_gel"),
						getGelSplatterPart("splatter_bottom", "repulsion_gel"),
						getGelSplatterPart("splatter_bottom_right", "repulsion_gel")).end().renderType("cutout");
		simpleBlock(PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK.get(), repulsionSplatterModel);
	}

	public BlockModelBuilder getGelSplatterPart(String name, String texture) {
		return models().withExistingParent(texture + "_" + name, new ResourceLocation(PaintGun.MODID, name))
				.texture("gel", new ResourceLocation(PaintGun.MODID, "block/" + texture))
				.texture("particle", new ResourceLocation(PaintGun.MODID, "block/" + texture + "_center")).renderType("cutout");
	}

	public void blockWithItem(RegistryObject<? extends Block> registryObject) {
		//block model
		simpleBlock(registryObject.get());
		//itemblock model
		ResourceLocation id = registryObject.getId();
		ResourceLocation textureLocation = new ResourceLocation(id.getNamespace(), "block/" + id.getPath());
		itemModels().cubeAll(id.getPath(), textureLocation);
	}

	public void fluid(RegistryObject<? extends Block> fluid, String name) {
		simpleBlock(fluid.get(), models().cubeAll(name, new ResourceLocation(PaintGun.MODID, ModelProvider.BLOCK_FOLDER + "/" + name + "_fluid_still")));
	}
}
