package com.rcx.paintgun.item;

import java.util.Random;
import java.util.function.Consumer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rcx.paintgun.PaintGunResources.FluidStuff;
import com.rcx.paintgun.datagen.PaintGunSounds;
import com.rcx.paintgun.entity.GelProjectile;
import com.rcx.paintgun.network.MessageGunFire;
import com.rcx.paintgun.network.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class PaintGunItem extends Item {

	public static Random rand = new Random();
	public static int SHOT_COST = 100;
	public FluidStuff primaryFluid;
	public FluidStuff secondaryFluid;

	public static final String PRIMARY_FIRE = "primary_fire";
	public static final String SECONDARY_FIRE = "secondary_fire";

	public PaintGunItem(Properties properties, FluidStuff primaryFluid, FluidStuff secondaryFluid) {
		super(properties);
		this.primaryFluid = primaryFluid;
		this.secondaryFluid = secondaryFluid;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
		boolean primaryFire = stack.getOrCreateTag().getBoolean(PRIMARY_FIRE);
		boolean secondaryFire = stack.getOrCreateTag().getBoolean(SECONDARY_FIRE);
		if (level.isClientSide()) {
			Minecraft mc = Minecraft.getInstance();
			boolean useDown = mc.options.keyUse.isDown();
			boolean attackDown = mc.options.keyAttack.isDown();
			if (primaryFire != useDown || secondaryFire != attackDown) {
				PacketHandler.INSTANCE.sendToServer(new MessageGunFire(useDown, attackDown));
			}
		} else if (entity instanceof LivingEntity living) {
			if (secondaryFire) {
				this.shootGel(level, living, stack, secondaryFluid.FLUID.get());
			} else if (primaryFire) {
				this.shootGel(level, living, stack, primaryFluid.FLUID.get());
			}
		}
	}

	public void shootGel(Level level, LivingEntity player, ItemStack stack, Fluid gel) {
		level.playSound(null, player.getX(), player.getY(), player.getZ(), PaintGunSounds.GEL_SHOOT.get(), SoundSource.PLAYERS, 0.4F, 1.4F + level.getRandom().nextFloat() * 0.2F);
		if (!level.isClientSide) {
			double handmod = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? 1.0 : -1.0;
			handmod *= player.getMainArm() == HumanoidArm.RIGHT ? 1.0 : -1.0;
			Vec3 look = player.getLookAngle().add(player.getUpVector(1.0f).scale(-0.2));
			double posX = player.getX() + look.x + handmod * (player.getBbWidth() / 2.0) * Math.sin(Math.toRadians(-player.getYHeadRot() - 90));
			double posY = player.getY() + player.getEyeHeight() + look.y;
			double posZ = player.getZ() + look.z + handmod * (player.getBbWidth() / 2.0) * Math.cos(Math.toRadians(-player.getYHeadRot() - 90));

			GelProjectile ink = new GelProjectile(level, player, posX, posY - 0.1875f, posZ, gel, rand.nextInt(4) + 2);
			ink.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.95F, 1.0F);
			level.addFreshEntity(ink);
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		return level.isClientSide() ? InteractionResultHolder.fail(stack) : InteractionResultHolder.consume(stack);
	}

	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				return HumanoidModel.ArmPose.BOW_AND_ARROW;
			}

			@Override
			public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
				int k = arm == HumanoidArm.RIGHT ? 1 : -1;
				poseStack.translate(k * 0.56F, -0.52F, -0.72F);
				return true;
			}
		});
	}
}
