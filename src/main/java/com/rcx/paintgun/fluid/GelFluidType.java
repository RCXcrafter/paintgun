package com.rcx.paintgun.fluid;

import java.util.function.Consumer;

import org.joml.Vector3f;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.rcx.paintgun.GelUtils;
import com.rcx.paintgun.PaintGun;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

public class GelFluidType extends FluidType {

	public final ResourceLocation RENDER_OVERLAY;
	public final ResourceLocation TEXTURE_STILL;
	public final ResourceLocation TEXTURE_FLOW;
	public final ResourceLocation TEXTURE_OVERLAY;
	public final Vector3f FOG_COLOR;
	public final float fogStart;
	public final float fogEnd;

	public GelFluidType(Properties properties, FluidInfo info) {
		super(properties);
		RENDER_OVERLAY = new ResourceLocation(PaintGun.MODID, "textures/overlay/" + info.name + ".png");
		TEXTURE_STILL = new ResourceLocation(PaintGun.MODID, "block/" + info.name + "_fluid_still");
		TEXTURE_FLOW = new ResourceLocation(PaintGun.MODID, "block/" + info.name + "_fluid_flow");
		TEXTURE_OVERLAY = TEXTURE_FLOW;
		//TEXTURE_OVERLAY = new ResourceLocation(PaintGun.MODID, "block/" + info.name + "_overlay");
		FOG_COLOR = new Vector3f(GelUtils.getRed(info.color)/255F, GelUtils.getGreen(info.color)/255F, GelUtils.getBlue(info.color)/255F);
		fogStart = info.fogStart;
		fogEnd = info.fogEnd;
	}

	public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
		consumer.accept(new IClientFluidTypeExtensions() {
			@Override
			public ResourceLocation getStillTexture() {
				return TEXTURE_STILL;
			}

			@Override
			public ResourceLocation getFlowingTexture() {
				return TEXTURE_FLOW;
			}

			/*@Override
			public ResourceLocation getOverlayTexture() {
				return TEXTURE_OVERLAY;
			}

			@Override
			public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
				return RENDER_OVERLAY;
			}*/

			@Override
			public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
				return FOG_COLOR;
			}

			@Override
			public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
				RenderSystem.setShaderFogStart(fogStart);
				RenderSystem.setShaderFogEnd(fogEnd);
			}
		});
	}

	public static class FluidInfo {

		public String name;
		public int color;
		public float fogStart;
		public float fogEnd;

		public FluidInfo(String name, int color, float fogStart, float fogEnd) {
			this.name = name;
			this.color = color;
			this.fogStart = fogStart;
			this.fogEnd = fogEnd;
		}
	}
}
