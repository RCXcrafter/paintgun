package com.rcx.paintgun.particle;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GelDropParticle extends TextureSheetParticle {

	public static float fakeLifetime = 5;

	private final SpriteSet sprites;

	public GelDropParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites, GelDropParticleOptions options) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		this.lifetime = 3 + this.random.nextInt(4);
		this.gravity = 0.8f;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.quadSize = options.getScale() / 4.0f;
		this.rCol = options.getColor().x();
		this.gCol = options.getColor().y();
		this.bCol = options.getColor().z();
		this.sprites = sprites;
	}

	@Override
	public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
		Vec3 vec3 = camera.getPosition();
		float f = (float)(Mth.lerp((double)pPartialTicks, this.xo, this.x) - vec3.x());
		float f1 = (float)(Mth.lerp((double)pPartialTicks, this.yo, this.y) - vec3.y());
		float f2 = (float)(Mth.lerp((double)pPartialTicks, this.zo, this.z) - vec3.z());

		Quaternionf quaternionf = new Quaternionf();
		quaternionf.rotateX(Mth.PI / 2);
		quaternionf.lookAlong((float) (xo - x), (float) (yo - y), (float) (zo - z), f, f1, f2).conjugate();

		Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float f3 = this.getQuadSize(pPartialTicks);

		for (int i = 0; i < 4; ++i) {
			Vector3f vector3f = avector3f[i];
			vector3f.rotate(quaternionf);
			vector3f.mul(f3);
			vector3f.add(f, f1, f2);
		}
		TextureAtlasSprite sprite = this.getSprite(pPartialTicks);

		float f6 = sprite.getU0();
		float f7 = sprite.getU1();
		float f4 = sprite.getV0();
		float f5 = sprite.getV1();
		int j = this.getLightColor(pPartialTicks);
		pBuffer.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
		pBuffer.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
	}

	public TextureAtlasSprite getSprite(float partialTick) {
		return sprites.get((int) ((fakeLifetime * (age + partialTick)) / ((float) lifetime)), (int) fakeLifetime);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<GelDropParticleOptions> {
		private final SpriteSet sprites;

		public Provider(SpriteSet sprites) {
			this.sprites = sprites;
		}

		@Override
		public TextureSheetParticle createParticle(GelDropParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new GelDropParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, type);
		}
	}
}
