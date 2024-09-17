package com.rcx.paintgun.network;

import com.rcx.paintgun.PaintGun;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(PaintGun.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
			);

	static int id = 0;

	public static void init() {
		INSTANCE.registerMessage(id++, MessageGunFire.class, MessageGunFire::encode, MessageGunFire::decode, MessageGunFire::handle);
	}
}
