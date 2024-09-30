package com.rcx.paintgun.render;

import java.util.Random;
import java.util.function.Function;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rcx.paintgun.entity.GelProjectile;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GelProjectileRenderer extends EntityRenderer<GelProjectile> {

	public static Function<Float, WobblyFluidCuboid> CUBOID = Util.memoize(size -> {
		return new WobblyFluidCuboid(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(size, size, size), FluidCuboid.DEFAULT_FACES);
	});

	public GelProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	float growTime = 4.0f;
	float startSize = 0.3f;

	@Override
	public void render(GelProjectile entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean flag = !entity.isInvisible();
		boolean flag1 = !flag && !entity.isInvisibleTo(minecraft.player);
		boolean flag2 = minecraft.shouldEntityAppearGlowing(entity);
		RenderType rendertype = this.getRenderType(entity, flag, flag1, flag2);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			Random rand = new Random(entity.getId());
			float age = entity.tickCount + partialTicks;
			float size = entity.getSize() * 2 + 2;

			if (age < growTime) {
				size *= age * (1 - startSize) / growTime + startSize;
			}

			poseStack.pushPose();

			poseStack.translate(0, entity.getBbHeight() / 2.0, 0);
			poseStack.mulPose(new Quaternionf().rotationX(rand.nextFloat()));
			poseStack.mulPose(new Quaternionf().rotationY(rand.nextFloat()));
			poseStack.mulPose(new Quaternionf().rotationZ(rand.nextFloat()));
			poseStack.translate(-size / 32.0, -size / 32.0, -size / 32.0);

			WobblyFluidCuboid cuboid = CUBOID.apply(size);
			cuboid.setWobbleTime(age, size / (16.0f * 6.0f), rand);

			FluidRenderer.renderCuboid(poseStack, vertexconsumer, cuboid, entity.getFluid(), packedLight, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}
	}

	protected RenderType getRenderType(GelProjectile pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
		ResourceLocation resourcelocation = this.getTextureLocation(pLivingEntity);
		if (pTranslucent) {
			return RenderType.itemEntityTranslucentCull(resourcelocation);
		} else if (pBodyVisible) {
			return PaintGunRenderTypes.FLUID;
		} else {
			return pGlowing ? RenderType.outline(resourcelocation) : null;
		}
	}

	@Override
	public ResourceLocation getTextureLocation(GelProjectile pEntity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
