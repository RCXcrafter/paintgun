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

public class GelDropParticleOptions implements ParticleOptions {

	protected final Vector3f color;
	protected final float scale;

	public static final Codec<GelDropParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> {
		return p_175793_.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter((p_175797_) -> {
			return p_175797_.color;
		}), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> {
			return p_175795_.scale;
		})).apply(p_175793_, GelDropParticleOptions::new);
	});
	public static final ParticleOptions.Deserializer<GelDropParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<GelDropParticleOptions>() {
		public GelDropParticleOptions fromCommand(ParticleType<GelDropParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
			Vector3f vector3fColor = GelDropParticleOptions.readVector3f(p_123690_);
			p_123690_.expect(' ');
			float f = p_123690_.readFloat();
			return new GelDropParticleOptions(vector3fColor, f);
		}

		public GelDropParticleOptions fromNetwork(ParticleType<GelDropParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
			return new GelDropParticleOptions(GelDropParticleOptions.readVector3f(p_123693_), p_123693_.readFloat());
		}
	};

	public GelDropParticleOptions(Vector3f color, float scale) {
		this.color = color;
		this.scale = scale;
	}

	public GelDropParticleOptions(Vector3f color) {
		this(color, 1.0f);
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
		pBuffer.writeFloat(this.scale);
	}

	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()), this.color.x(), this.color.y(), this.color.z(), this.scale);
	}

	public Vector3f getColor() {
		return this.color;
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public ParticleType<?> getType() {
		return PaintGunResources.GEL_DROP_PARTICLE.get();
	}
}
