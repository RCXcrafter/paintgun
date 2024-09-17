package com.rcx.paintgun.datagen;

import java.util.function.Consumer;

import com.rcx.paintgun.PaintGun;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider.AdvancementGenerator;

public class PaintGunAdvancements implements AdvancementGenerator {

	@Override
	public void generate(HolderLookup.Provider registries, Consumer<Advancement> saver, ExistingFileHelper existingFileHelper) {
		
	}

	public Component getTitle(String id) {
		return Component.translatable("advancements." + PaintGun.MODID + "." + id + ".title");
	}

	public Component getDesc(String id) {
		return Component.translatable("advancements." + PaintGun.MODID + "." + id + ".description");
	}

	public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> pTag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(pTag).build());
	}

	public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... pPredicates) {
		return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, pPredicates);
	}
}
