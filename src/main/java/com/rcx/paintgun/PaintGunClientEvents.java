package com.rcx.paintgun;

import com.rcx.paintgun.datagen.PaintGunItemTags;

import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PaintGunClientEvents {

	public static void movementInput(MovementInputUpdateEvent event) {
		if (event.getEntity().isUsingItem() && !event.getEntity().isPassenger() && event.getEntity().getItemInHand(event.getEntity().getUsedItemHand()).is(PaintGunItemTags.FLUID_GUN)) {
			event.getInput().forwardImpulse /= 0.2f;
			event.getInput().leftImpulse /= 0.2f;
			if (event.getEntity().isSprinting())
				event.getEntity().setSprinting(false);
		}
	}

	/*public static void leftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
		if (event.getEntity().getMainHandItem().is(PaintGunItemTags.FLUID_GUN)) {
			event.setCanceled(true);
		}
	}*/

	public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getEntity().getMainHandItem().is(PaintGunItemTags.FLUID_GUN) || event.getEntity().getOffhandItem().is(PaintGunItemTags.FLUID_GUN)) {
			event.setCanceled(true);
		}
	}
}
