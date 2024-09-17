package com.rcx.paintgun;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.rcx.paintgun.datagen.PaintGunAdvancements;
import com.rcx.paintgun.datagen.PaintGunBlockStates;
import com.rcx.paintgun.datagen.PaintGunBlockTags;
import com.rcx.paintgun.datagen.PaintGunFluidTags;
import com.rcx.paintgun.datagen.PaintGunItemModels;
import com.rcx.paintgun.datagen.PaintGunItemTags;
import com.rcx.paintgun.datagen.PaintGunLang;
import com.rcx.paintgun.datagen.PaintGunLootModifiers;
import com.rcx.paintgun.datagen.PaintGunLootTables;
import com.rcx.paintgun.datagen.PaintGunRecipes;
import com.rcx.paintgun.datagen.PaintGunSounds;
import com.rcx.paintgun.network.PacketHandler;
import com.rcx.paintgun.particle.GelDropParticle;
import com.rcx.paintgun.particle.GelSplashParticle;
import com.rcx.paintgun.render.GelProjectileRenderer;
import com.rcx.paintgun.render.SplatterModel;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PaintGun.MODID)
public class PaintGun {

	public static final String MODID = "paintgun";

	//private static final Logger LOGGER = LogUtils.getLogger();

	public static HashMap<Fluid, Block> gelMap = new HashMap<Fluid, Block>();

	public PaintGun() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);
		modEventBus.addListener(this::gatherData);

		PaintGunResources.BLOCKS.register(modEventBus);
		PaintGunResources.ITEMS.register(modEventBus);
		PaintGunResources.BLOCK_ENTITY_TYPES.register(modEventBus);
		PaintGunResources.FLUIDTYPES.register(modEventBus);
		PaintGunResources.FLUIDS.register(modEventBus);
		PaintGunResources.ENTITY_TYPES.register(modEventBus);
		PaintGunResources.MOB_EFFECTS.register(modEventBus);
		PaintGunResources.CREATIVE_TABS.register(modEventBus);
		PaintGunResources.PARTICLE_TYPES.register(modEventBus);
		PaintGunResources.SOUND_EVENTS.register(modEventBus);
		PaintGunResources.LOOT_MODIFIERS.register(modEventBus);
		PaintGunSounds.init();

		//ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

		MinecraftForge.EVENT_BUS.addListener(PaintGunEventManager::onLivingFall);
	}

	public void commonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
		gelMap.put(PaintGunResources.PROPULSION_GEL.FLUID.get(), PaintGunResources.PROPULSION_GEL_SPLATTER_BLOCK.get());
		gelMap.put(PaintGunResources.REPULSION_GEL.FLUID.get(), PaintGunResources.REPULSION_GEL_SPLATTER_BLOCK.get());
	}

	public void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput output = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeClient()) {
			gen.addProvider(true, new PaintGunLang(output));
			ItemModelProvider itemModels = new PaintGunItemModels(output, existingFileHelper);
			gen.addProvider(true, itemModels);
			gen.addProvider(true, new PaintGunBlockStates(output, existingFileHelper));
			gen.addProvider(true, new PaintGunSounds(output, existingFileHelper));
		} if (event.includeServer()) {
			gen.addProvider(true, new PaintGunLootTables(output));
			gen.addProvider(true, new PaintGunRecipes(output));
			CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
			BlockTagsProvider blockTags = new PaintGunBlockTags(output, lookupProvider, existingFileHelper);
			gen.addProvider(true, blockTags);
			gen.addProvider(true, new PaintGunItemTags(output, lookupProvider, blockTags.contentsGetter(), existingFileHelper));
			gen.addProvider(true, new PaintGunFluidTags(output, lookupProvider, existingFileHelper));
			gen.addProvider(true, new PaintGunLootModifiers(output));
			gen.addProvider(true, new ForgeAdvancementProvider(output, event.getLookupProvider(), event.getExistingFileHelper(), List.of(new PaintGunAdvancements())));
		}
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			MinecraftForge.EVENT_BUS.addListener(PaintGunClientEvents::movementInput);
			//MinecraftForge.EVENT_BUS.addListener(PaintGunClientEvents::leftClickAir);
			MinecraftForge.EVENT_BUS.addListener(PaintGunClientEvents::leftClickBlock);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
			event.register("splatter", SplatterModel.Loader.INSTANCE);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void addParticleProvider(RegisterParticleProvidersEvent event) {
			event.registerSpriteSet(PaintGunResources.GEL_SPLASH_PARTICLE.get(), GelSplashParticle.Provider::new);
			event.registerSpriteSet(PaintGunResources.GEL_DROP_PARTICLE.get(), GelDropParticle.Provider::new);
		}

		@OnlyIn(Dist.CLIENT)
		@SubscribeEvent
		static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(PaintGunResources.GEL_PROJECTILE.get(), GelProjectileRenderer::new);
		}
	}
}
