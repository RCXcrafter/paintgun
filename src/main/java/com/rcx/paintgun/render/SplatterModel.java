package com.rcx.paintgun.render;

import java.util.HashSet;
import java.util.function.Function;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public class SplatterModel implements IUnbakedGeometry<SplatterModel> {

	BlockModel[] models;

	public SplatterModel(BlockModel[] models) {
		this.models = models;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
		BakedModel[][][][][] bakedModels = new BakedModel[9][3][3][3][6];

		for (int m = 0; m < 9; m++) {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					for (int z = 0; z < 3; z++) {
						if (m == 4 && (x != 1 || y != 1 || z != 1)) //centers
							continue;
						if ((m == 1 || m == 3 || m == 5 || m == 7) && (x != 1 && y != 1 && z != 1)) //edges
							continue;
						if ((m == 0 || m == 2 || m == 6 || m == 8) && (x == 1 && y == 1 && z == 1)) //corners
							continue;

						HashSet<Integer> skip = new HashSet<Integer>();
						if (y == 0)
							skip.add(0);
						else if (y == 2)
							skip.add(1);
						if (z == 0)
							skip.add(2);
						else if (z == 2)
							skip.add(3);
						if (x == 0)
							skip.add(4);
						else if (x == 2)
							skip.add(5);

						bakedModels[m][x][y][z] = getRotatedModels(context, baker, spriteGetter, modelLocation, models[m], new Vector3f(x - 1, y - 1, z - 1), skip);
					}
				}
			}
		}

		return new SplatterBakedModel(bakedModels);
	}

	public static BlockModel offsetModel(float x, float y, float z, BlockModel model) {
		for (BlockElement element : model.getElements()) {
			element.to.add(x, y, z);
			element.from.add(x, y, z);
		}
		return model;
	}

	public static Quaternionf[] rotations = {
			new Quaternionf().rotateYXZ((float)(-0) * ((float)Math.PI / 180F), (float)(-90) * ((float)Math.PI / 180F), 0.0F), //X90 Y0
			new Quaternionf().rotateYXZ((float)(-0) * ((float)Math.PI / 180F), (float)(-270) * ((float)Math.PI / 180F), 0.0F), //X270 Y0
			new Quaternionf().rotateYXZ((float)(-0) * ((float)Math.PI / 180F), (float)(-0) * ((float)Math.PI / 180F), 0.0F), //X0 Y0
			new Quaternionf().rotateYXZ((float)(-180) * ((float)Math.PI / 180F), (float)(-0) * ((float)Math.PI / 180F), 0.0F), //X0 Y180
			new Quaternionf().rotateYXZ((float)(-270) * ((float)Math.PI / 180F), (float)(-0) * ((float)Math.PI / 180F), 0.0F), //X0 Y270
			new Quaternionf().rotateYXZ((float)(-90) * ((float)Math.PI / 180F), (float)(-0) * ((float)Math.PI / 180F), 0.0F) //X0 Y90
	};

	public static BakedModel[] getRotatedModels(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ResourceLocation modelLocation, BlockModel model, Vector3f offset, HashSet<Integer> skip) {
		BakedModel[] models = new BakedModel[6];

		for (int i = 0; i < rotations.length; i++) {
			if (!skip.contains(i))
				models[i] = model.bake(baker, model, spriteGetter, new SimpleModelState(new Transformation(offset, rotations[i], null, null)), modelLocation, context.useBlockLight());
		}
		return models;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		for (BlockModel model : models) {
			model.resolveParents(modelGetter);
		}
	}

	public static final class Loader implements IGeometryLoader<SplatterModel> {

		public static final Loader INSTANCE = new Loader();

		@Override
		public SplatterModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
			BlockModel[] models = new BlockModel[9];
			models[0] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "top_left"), BlockModel.class);
			models[1] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "top"), BlockModel.class);
			models[2] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "top_right"), BlockModel.class);
			models[3] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "left"), BlockModel.class);
			models[4] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "center"), BlockModel.class);
			models[5] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "right"), BlockModel.class);
			models[6] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "bottom_left"), BlockModel.class);
			models[7] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "bottom"), BlockModel.class);
			models[8] = deserializationContext.deserialize(GsonHelper.getAsJsonObject(jsonObject, "bottom_right"), BlockModel.class);

			return new SplatterModel(models);
		}
	}
}
