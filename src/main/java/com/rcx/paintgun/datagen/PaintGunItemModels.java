package com.rcx.paintgun.datagen;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;
import com.rcx.paintgun.PaintGunResources.FluidStuff;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class PaintGunItemModels extends ItemModelProvider {

	public PaintGunItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, PaintGun.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		for (FluidStuff fluid : PaintGunResources.fluidList) {
			itemWithModel(fluid.FLUID_BUCKET, "item/generated");
		}
	}

	public void layeredItem(RegistryObject<? extends Item> registryObject, String model, ResourceLocation... textures) {
		ResourceLocation id = registryObject.getId();

		ModelBuilder<?> builder = withExistingParent(id.getPath(), new ResourceLocation(model));
		for (int i = 0; i < textures.length; i ++) {
			builder.texture("layer" + i, textures[i]);
		}
	}

	public void itemWithModel(RegistryObject<? extends Item> registryObject, String model) {
		ResourceLocation id = registryObject.getId();
		ResourceLocation textureLocation = new ResourceLocation(id.getNamespace(), "item/" + id.getPath());
		singleTexture(id.getPath(), new ResourceLocation(model), "layer0", textureLocation);
	}
}
