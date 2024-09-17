package com.rcx.paintgun.datagen;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class PaintGunSounds extends SoundDefinitionsProvider {

	//this is just here so the class loads, nothing else needs to happen here
	public static void init() {}

	//sounds
	public static final RegistryObject<SoundEvent> GEL_SHOOT = registerSound("item.gel.shoot");

	public PaintGunSounds(PackOutput generator, ExistingFileHelper helper) {
		super(generator, PaintGun.MODID, helper);
	}

	public static RegistryObject<SoundEvent> registerSound(String name) {
		return PaintGunResources.SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PaintGun.MODID, name)));
	}

	@Override
	public void registerSounds() {
		withSubtitle(GEL_SHOOT, definition().with(
				sound(resource("bubble_1")),
				sound(resource("bubble_2")),
				sound(resource("bubble_3")),
				sound(resource("bubble_4"))));
	}

	public void withSubtitle(RegistryObject<SoundEvent> soundEvent, SoundDefinition definition) {
		add(soundEvent, definition.subtitle("subtitles." + PaintGun.MODID + "." + soundEvent.getId().getPath()));
	}

	public ResourceLocation resource(String path) {
		return new ResourceLocation(PaintGun.MODID, path);
	}
}
