package com.rcx.paintgun.particle;

import java.util.Locale;

import org.joml.Vector3f;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;

public class GelSplashParticleOptions implements ParticleOptions {

	protected final Vector3f color;
	protected final Vector3f direction;
	protected final float scale;

	public static final Codec<GelSplashParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), ExtraCodecs.VECTOR3F.fieldOf("direction").forGetter((p_175797_) -> {
			return p_175797_.direction;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		})).apply(p_175793_, GelSplashParticleOptions::new);
	});
	public static final ParticleOptions.Deserializer<GelSplashParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<GelSplashParticleOptions>() {
		public GelSplashParticleOptions fromCommand(ParticleType<GelSplashParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
			Vector3f vector3fColor = GelSplashParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			Vector3f vector3fDirection = GelSplashParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			float f = p_123690_.readFloat();
			return new GelSplashParticleOptions(vector3fColor, vector3fDirection, f);
		}

		public GelSplashParticleOptions fromNetwork(ParticleType<GelSplashParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
			return new GelSplashParticleOptions(GelSplashParticleOptions.readVector3f(p_123693_), GelSplashParticleOptions.readVector3f(p_123693_), p_123693_.readFloat());
		}
	};

	public GelSplashParticleOptions(Vector3f color, Vector3f direction, float scale) {
		this.color = color;
		this.direction = direction;
		this.scale = scale;
	}

	public GelSplashParticleOptions(Vector3f color, Vector3f direction) {
		this(color, direction, 1);
	}

	public static Vector3f readVector3f(StringReader pStringInput) throws CommandSyntaxException {
		pStringInput.expect(' ');
		float f = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f1 = pStringInput.readFloat();
		pStringInput.expect(' ');
		float f2 = pStringInput.readFloat();
		return new Vector3f(f, f1, f2);
	}

	public static Vector3f readVector3f(FriendlyByteBuf pBuffer) {
		return new Vector3f(pBuffer.readFloat(), pBuffer.readFloat(), pBuffer.readFloat());
	}

	public void writeToNetwork(FriendlyByteBuf pBuffer) {
		pBuffer.writeFloat(this.color.x());
		pBuffer.writeFloat(this.color.y());
		pBuffer.writeFloat(this.color.z());
		pBuffer.writeFloat(this.direction.x());
		pBuffer.writeFloat(this.direction.y());
		pBuffer.writeFloat(this.direction.z());
		pBuffer.writeFloat(this.scale);
	}

	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.direction.x(), this.direction.y(), this.direction.z(), this.scale);
	}

	public Vector3f getColor() {
		return this.color;
	}

	public Vector3f getDirection() {
		return this.direction;
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public ParticleType<?> getType() {
		return PaintGunResources.GEL_SPLASH_PARTICLE.get();
	}
}
