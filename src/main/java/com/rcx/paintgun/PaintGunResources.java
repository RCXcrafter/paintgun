package com.rcx.paintgun;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.rcx.paintgun.block.PropulsionGelSplatterBlock;
import com.rcx.paintgun.block.RepulsionGelSplatterBlock;
import com.rcx.paintgun.entity.GelProjectile;
import com.rcx.paintgun.fluid.GelFluidType;
import com.rcx.paintgun.fluid.GelFluidType.FluidInfo;
import com.rcx.paintgun.item.PaintGunItem;
import com.rcx.paintgun.misc.PropulsionMobEffect;
import com.rcx.paintgun.misc.RepulsionMobEffect;
import com.rcx.paintgun.particle.GelDropParticleOptions;
import com.rcx.paintgun.particle.GelSplashParticleOptions;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PaintGunResources {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PaintGun.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PaintGun.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PaintGun.MODID);
	public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, PaintGun.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, PaintGun.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PaintGun.MODID);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, PaintGun.MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PaintGun.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, PaintGun.MODID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PaintGun.MODID);
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PaintGun.MODID);

	public static List<FluidStuff> fluidList = new ArrayList<FluidStuff>();

	public static FluidStuff addFluid(String localizedName, String name, Block.Properties properties, BiFunction<FluidType.Properties, String, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Source> source, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Flowing> flowing, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		FluidStuff fluid = new FluidStuff(name, localizedName, type.apply(prop, name), block, fluidProperties, source, flowing, properties);
		fluidList.add(fluid);
		return fluid;
	}

	public static FluidStuff addFluid(String localizedName, String name, Block.Properties properties, BiFunction<FluidType.Properties, String, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		return addFluid(localizedName, name, properties, type, block, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, fluidProperties, prop);
	}

	public static FluidStuff addFluid(String localizedName, FluidInfo info, Block.Properties properties, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Source> source, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Flowing> flowing, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		FluidStuff fluid = new FluidStuff(info.name, localizedName, type.apply(prop, info), block, fluidProperties, source, flowing, properties);
		fluidList.add(fluid);
		return fluid;
	}

	public static FluidStuff addFluid(String localizedName, FluidInfo info, Block.Properties properties, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		return addFluid(localizedName, info, properties, type, block, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, fluidProperties, prop);
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
		return ENTITY_TYPES.register(name, () -> builder.build(PaintGun.MODID + ":" + name));
	}

	public static <T extends ParticleOptions> RegistryObject<ParticleType<T>> registerParticle(String name, boolean overrideLimiter, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec) {
		return PARTICLE_TYPES.register(name, () -> new ParticleType<T>(overrideLimiter, deserializer) {
			public Codec<T> codec() {
				return codec;
			}
		});
	}

	public static final RegistryObject<Block> PROPULSION_GEL_SPLATTER_BLOCK = BLOCKS.register("propulsion_gel_splatter", () -> new PropulsionGelSplatterBlock(Properties.of().replaceable().noCollission().sound(SoundType.HONEY_BLOCK).instabreak().pushReaction(PushReaction.DESTROY)));
	public static final RegistryObject<Block> REPULSION_GEL_SPLATTER_BLOCK = BLOCKS.register("repulsion_gel_splatter", () -> new RepulsionGelSplatterBlock(Properties.of().replaceable().noCollission().sound(SoundType.HONEY_BLOCK).instabreak().pushReaction(PushReaction.DESTROY)));

	//public static final RegistryObject<Block> INKLOGGED_BLOCK = BLOCKS.register("inklogged_block", () -> new InkLoggedBlock(Properties.of().replaceable().noCollission().sound(SoundType.HONEY_BLOCK)));

	public static final FluidStuff PROPULSION_GEL = addFluid("Propulsion Gel", new FluidInfo("propulsion_gel", 0xFF6A00, 0.1F, 1.5F), Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).noCollission().replaceable().liquid().pushReaction(PushReaction.DESTROY).noLootTable(), GelFluidType::new, LiquidBlock::new,
			prop -> prop.explosionResistance(1000F),
			FluidType.Properties.create()
			.canExtinguish(true)
			.supportsBoating(true)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL));

	public static final FluidStuff REPULSION_GEL = addFluid("Repulsion Gel", new FluidInfo("repulsion_gel", 0x007BFF, 0.1F, 1.5F), Block.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().replaceable().liquid().pushReaction(PushReaction.DESTROY).noLootTable(), GelFluidType::new, LiquidBlock::new,
			prop -> prop.explosionResistance(1000F),
			FluidType.Properties.create()
			.canExtinguish(true)
			.supportsBoating(true)
			.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
			.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL));


	public static final RegistryObject<Item> PAINT_GUN = ITEMS.register("paint_gun", () -> new PaintGunItem(new Item.Properties().stacksTo(1), PROPULSION_GEL, REPULSION_GEL));


	public static final RegistryObject<EntityType<GelProjectile>> GEL_PROJECTILE = registerEntity("gel_projectile", EntityType.Builder.<GelProjectile>of(GelProjectile::new, MobCategory.MISC).sized(0.0625F, 0.0625F).fireImmune().clientTrackingRange(3).updateInterval(1));


	public static final RegistryObject<MobEffect> PROPULSION_EFFECT = MOB_EFFECTS.register("propulsion", () -> new PropulsionMobEffect(MobEffectCategory.NEUTRAL, 0xFF6A00));
	public static final RegistryObject<MobEffect> REPULSION_EFFECT = MOB_EFFECTS.register("repulsion", () -> new RepulsionMobEffect(MobEffectCategory.NEUTRAL, 0x007BFF));


	public static final RegistryObject<ParticleType<GelSplashParticleOptions>> GEL_SPLASH_PARTICLE = registerParticle("gel_splash", false, GelSplashParticleOptions.DESERIALIZER, GelSplashParticleOptions.CODEC);
	public static final RegistryObject<ParticleType<GelDropParticleOptions>> GEL_DROP_PARTICLE = registerParticle("gel_drop", false, GelDropParticleOptions.DESERIALIZER, GelDropParticleOptions.CODEC);


	public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_TABS.register("main_tab", () -> CreativeModeTab.builder()
			.title(Component.translatable("item_group." + PaintGun.MODID))
			.icon(() -> new ItemStack(PAINT_GUN.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.displayItems((params, output) -> {
				output.accept(PAINT_GUN.get());
				output.accept(PROPULSION_GEL.FLUID_BUCKET.get());
				output.accept(REPULSION_GEL.FLUID_BUCKET.get());
			})
			.build());


	public static class FluidStuff {

		public final ForgeFlowingFluid.Properties PROPERTIES;

		public final RegistryObject<ForgeFlowingFluid.Source> FLUID;
		public final RegistryObject<ForgeFlowingFluid.Flowing> FLUID_FLOW;
		public final RegistryObject<FluidType> TYPE;

		public final RegistryObject<LiquidBlock> FLUID_BLOCK;

		public final RegistryObject<BucketItem> FLUID_BUCKET;

		public final String name;
		public final String localizedName;

		public FluidStuff(String name, String localizedName, FluidType type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Source> source, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Flowing> flowing, Block.Properties properties) {
			this.name = name;
			this.localizedName = localizedName;

			FLUID = FLUIDS.register(name, () -> source.apply(getFluidProperties()));
			FLUID_FLOW = FLUIDS.register("flowing_" + name, () -> flowing.apply(getFluidProperties()));
			TYPE = FLUIDTYPES.register(name, () -> type);

			PROPERTIES = new ForgeFlowingFluid.Properties(TYPE, FLUID, FLUID_FLOW);
			if (fluidProperties != null)
				fluidProperties.accept(PROPERTIES);

			FLUID_BLOCK = BLOCKS.register(name + "_block", () -> block.apply(FLUID, properties.lightLevel((state) -> { return type.getLightLevel(); }).randomTicks().strength(100.0F).noLootTable()));
			FLUID_BUCKET = ITEMS.register(name + "_bucket", () -> new BucketItem(FLUID, new BucketItem.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

			PROPERTIES.bucket(FLUID_BUCKET).block(FLUID_BLOCK);
		}

		public ForgeFlowingFluid.Properties getFluidProperties() {
			return PROPERTIES;       
		}
	}
}
