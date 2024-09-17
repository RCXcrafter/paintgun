package com.rcx.paintgun.datagen;

import java.util.function.Consumer;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

public class PaintGunRecipes extends RecipeProvider implements IConditionBuilder {

	public PaintGunRecipes(PackOutput gen) {
		super(gen);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, PaintGunResources.PAINT_GUN.get())
		.define('I', Tags.Items.INGOTS_IRON)
		.define('G', Tags.Items.GLASS_SILICA)
		.define('N', Tags.Items.INGOTS_NETHERITE)
		.define('L', Tags.Items.GEMS_LAPIS)
		.define('S', Tags.Items.SLIMEBALLS)
		.define('B', Items.BLAZE_POWDER)
		.pattern("IGI")
		.pattern("LSB")
		.pattern("NNN")
		.unlockedBy("has_netherite", has(Tags.Items.INGOTS_NETHERITE))
		.save(consumer);
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(PaintGun.MODID, name);
	}
}
