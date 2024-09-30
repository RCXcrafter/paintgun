package com.rcx.paintgun.render;

import java.util.Map;
import java.util.Random;

import org.joml.Vector3f;

import net.minecraft.core.Direction;

public class WobblyFluidCuboid extends FluidCuboid {

	protected Vector3f[] wobbledScaledVertices = new Vector3f[8];

	public WobblyFluidCuboid(Vector3f from, Vector3f to, Map<Direction, FluidFace> faces) {
		super(from, to, faces);
		for (int i = 0; i < wobbledScaledVertices.length; ++i) {
			wobbledScaledVertices[i] = new Vector3f();
		}
	}

	public WobblyFluidCuboid setWobbleTime(float time, float amplitude, Random rand) {
		Vector3f[] scaledVertices = this.getUnwobbledScaledVertices();
		for (int i = 0; i < wobbledScaledVertices.length; ++i) {
			if (rand.nextBoolean())
				amplitude = -amplitude;
			scaledVertices[i].add(
					(float) Math.sin(time + rand.nextFloat(6.28f)) * amplitude,
					(float) Math.sin(time + rand.nextFloat(6.28f)) * amplitude,
					(float) Math.sin(time + rand.nextFloat(6.28f)) * amplitude,
					wobbledScaledVertices[i]);
		}
		return this;
	}

	public Vector3f[] getScaledVertices() {
		return wobbledScaledVertices;
	}

	public Vector3f[] getUnwobbledScaledVertices() {
		if (scaledVertices == null) {
			scaledVertices = new Vector3f[vertices.length];
			for (int i = 0; i < vertices.length; ++i) {
				scaledVertices[i] = new Vector3f(vertices[i]);
				scaledVertices[i].mul(1 / 16f);
			}
		}
		return scaledVertices;
	}
}
