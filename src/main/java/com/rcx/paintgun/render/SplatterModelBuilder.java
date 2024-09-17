package com.rcx.paintgun.render;

import java.util.ArrayList;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.rcx.paintgun.PaintGun;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SplatterModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

	ArrayList<T> models = new ArrayList<T>();

	public static <T extends ModelBuilder<T>> SplatterModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
		return new SplatterModelBuilder<>(parent, existingFileHelper);
	}

	protected SplatterModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(new ResourceLocation(PaintGun.MODID, "splatter"), parent, existingFileHelper);
	}

	public SplatterModelBuilder<T> parts(T topLeft, T top, T topRight, T left, T center, T right, T bottomLeft, T bottom, T bottomRight) {
		Preconditions.checkNotNull(topLeft, "top_left must not be null");
		Preconditions.checkNotNull(top, "top must not be null");
		Preconditions.checkNotNull(topRight, "top_right must not be null");
		Preconditions.checkNotNull(left, "left must not be null");
		Preconditions.checkNotNull(center, "center must not be null");
		Preconditions.checkNotNull(right, "right must not be null");
		Preconditions.checkNotNull(bottomLeft, "bottom_left must not be null");
		Preconditions.checkNotNull(bottom, "bottom must not be null");
		Preconditions.checkNotNull(bottomRight, "bottom_right must not be null");
		models.add(0, topLeft);
		models.add(1, top);
		models.add(2, topRight);
		models.add(3, left);
		models.add(4, center);
		models.add(5, right);
		models.add(6, bottomLeft);
		models.add(7, bottom);
		models.add(8, bottomRight);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);

		json.add("top_left", models.get(0).toJson());
		json.add("top", models.get(1).toJson());
		json.add("top_right", models.get(2).toJson());
		json.add("left", models.get(3).toJson());
		json.add("center", models.get(4).toJson());
		json.add("right", models.get(5).toJson());
		json.add("bottom_left", models.get(6).toJson());
		json.add("bottom", models.get(7).toJson());
		json.add("bottom_right", models.get(8).toJson());

		return json;
	}
}
