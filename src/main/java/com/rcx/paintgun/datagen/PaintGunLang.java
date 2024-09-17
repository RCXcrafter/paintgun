package com.rcx.paintgun.datagen;

import com.rcx.paintgun.PaintGun;
import com.rcx.paintgun.PaintGunResources;
import com.rcx.paintgun.PaintGunResources.FluidStuff;

import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;

public class PaintGunLang extends LanguageProvider {

	public PaintGunLang(PackOutput gen) {
		super(gen, PaintGun.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		for (FluidStuff fluid : PaintGunResources.fluidList) {
			addFluid(fluid.name, fluid.localizedName);
			addItem(fluid.FLUID_BUCKET, fluid.localizedName + " Bucket");
		}

		addBlock(PaintGunResources.PROPULSION_GEL_SPLATTER_BLOCK, "Propulsion Gel");
		addBlock(PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK, "Repulsion Gel");

		addItem(PaintGunResources.PAINT_GUN, "Paint Gun");

		addEntityType(PaintGunResources.GEL_PROJECTILE, "Gel Projectile");

		add("item_group." + PaintGun.MODID, "Paint Gun");

		add("effect." + PaintGun.MODID + ".propulsion", "Propulsion");
		add("effect." + PaintGun.MODID + ".repulsion", "Repulsion");

		addSubtitle(PaintGunSounds.GEL_SHOOT, "Gel is shot");
	}

	public void addAdvancement(String id, String title, String description) {
		add("advancements." + PaintGun.MODID + "." + id + ".title", title);
		add("advancements." + PaintGun.MODID + "." + id + ".description", description);
	}

	public void addFluid(String name, String localizedName) {
		add("fluid." + PaintGun.MODID + "." + name, localizedName);
		add("fluid_type." + PaintGun.MODID + "." + name, localizedName);
	}

	public void addSubtitle(RegistryObject<SoundEvent> soundEvent, String subtitle) {
		add("subtitles." + PaintGun.MODID + "." + soundEvent.getId().getPath(), subtitle);
	}
}
