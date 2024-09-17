package com.rcx.paintgun.network;

import java.util.function.Supplier;

import com.rcx.paintgun.datagen.PaintGunItemTags;
import com.rcx.paintgun.item.PaintGunItem;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class MessageGunFire {

	public boolean[] tanks;

	public MessageGunFire(boolean... tanks) {
		this.tanks = tanks;
	}

	public static void encode(MessageGunFire msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.tanks.length);
		for (int i = 0; i < msg.tanks.length; ++i) {
			buf.writeBoolean(msg.tanks[i]);
		}
	}

	public static MessageGunFire decode(FriendlyByteBuf buf) {
		boolean[] tanks = new boolean[buf.readInt()];
		for (int i = 0; i < tanks.length; ++i) {
			tanks[i] = buf.readBoolean();
		}
		return new MessageGunFire(tanks);
	}

	public static void handle(MessageGunFire msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isServer()) {
			ctx.get().enqueueWork(() -> {
				ItemStack main = ctx.get().getSender().getMainHandItem();
				ItemStack off = ctx.get().getSender().getOffhandItem();
				addFireTags(main, msg);
				addFireTags(off, msg);
			});
		}
		ctx.get().setPacketHandled(true);
	}

	public static void addFireTags(ItemStack stack, MessageGunFire msg) {
		if (stack.is(PaintGunItemTags.FLUID_GUN)) {
			stack.getOrCreateTag().putBoolean(PaintGunItem.PRIMARY_FIRE, msg.tanks[0]);
			stack.getOrCreateTag().putBoolean(PaintGunItem.SECONDARY_FIRE, msg.tanks[1]);
		}
	}
}
