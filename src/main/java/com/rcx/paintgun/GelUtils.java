package com.rcx.paintgun;

import com.rcx.paintgun.misc.GelCollisions;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GelUtils {

	public static String ITEM_COOLDOWN_KEY = PaintGun.MODID + ":cooldown_time";

	public static Direction touchingWall(Entity player) {
		AABB aabb = player.getBoundingBox().inflate(0.0, -0.1, 0.0);
		for (Direction face : Plane.HORIZONTAL) {
			if (player.level().getBlockCollisions(player, aabb.expandTowards(face.getNormal().getX() * 0.05, 0.0, face.getNormal().getZ() * 0.05)).iterator().hasNext()) {
				return face;
			}
		}
		return Direction.UP;
	}

	public static boolean leavingWall(Entity player) {
		return !player.level().getBlockCollisions(player, player.getBoundingBox().inflate(0.001, -0.1, 0.001)).iterator().hasNext();
	}

	public static boolean touchingFloor(Entity player) {
		return player.level().getBlockCollisions(player, player.getBoundingBox().inflate(-0.07, 0.0, -0.07).expandTowards(0.0, -0.01, 0.0)).iterator().hasNext();
	}

	public static Direction onTheEdge(Entity player) {
		AABB aabb = player.getBoundingBox().expandTowards(0.0, -0.5, 0.0);
		for (Direction face : Plane.HORIZONTAL) {
			if (!player.level().getBlockCollisions(player, aabb.contract(face.getNormal().getX() * 0.2, 0.0, face.getNormal().getZ() * 0.2)).iterator().hasNext()) {
				return face;
			}
		}
		return Direction.UP;
	}

	public static boolean touchingFloorGel(Entity player, Block block) {
		return getGelCollisions(player.level(), player, player.getBoundingBox().inflate(-0.07, 0.0, -0.07), block).iterator().hasNext();
	}

	public static Direction touchingWallGel(Entity player, Block block) {
		AABB aabb = player.getBoundingBox().inflate(-0.07, -0.1, -0.07);
		for (Direction face : Plane.HORIZONTAL) {
			if (getGelCollisions(player.level(), player, aabb.expandTowards(face.getNormal().getX() * 0.05, 0.0, face.getNormal().getZ() * 0.05), block).iterator().hasNext()) {
				return face;
			}
		}
		return Direction.UP;
	}

	public static Iterable<VoxelShape> getGelCollisions(CollisionGetter collisionGetter, Entity pEntity, AABB pCollisionBox, Block block) {
		return () -> {
			return new GelCollisions<>(collisionGetter, pEntity, pCollisionBox, false, block, (pos, shape) -> {
				return shape.move(-pos.getX(), -pos.getY(), -pos.getZ());
			});
		};
	}

	public static boolean hasItemCooledDown(ItemStack stack, long time, int cooldown) {
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getTag();
			if (nbt.contains(GelUtils.ITEM_COOLDOWN_KEY)) {
				return (nbt.getLong(GelUtils.ITEM_COOLDOWN_KEY) + cooldown) < time;
			}
		}
		return true;
	}

	public static ItemStack setItemCooldown(ItemStack stack, long time) {
		stack.getOrCreateTag().putLong(GelUtils.ITEM_COOLDOWN_KEY, time);
		return stack;
	}

	public static int getRed(int color) {
		return (0xFF0000 & color) >> 16;
	}

	public static int getGreen(int color) {
		return (0x00FF00 & color) >> 8;
	}

	public static int getBlue(int color) {
		return 0x0000FF & color;
	}

	public static float[] RGBFromInt(int color) {
		return new float[] {
				((0xFF0000 & color) >> 16) / 255.0F,
				((0x00FF00 & color) >> 8) / 255.0F,
				(0x0000FF & color) / 255.0F
		};
	}

	public static int addColors(int color, int color2) {
		return RGBToInt(((0xFF0000 & color) >> 16) + ((0xFF0000 & color2) >> 16),
				((0x00FF00 & color) >> 8) + ((0x00FF00 & color2) >> 8),
				(0x0000FF & color) + (0x0000FF & color2));
	}

	public static int getAdditiveOvershoot(int color, int color2) {
		int r = ((0xFF0000 & color) >> 16) + ((0xFF0000 & color2) >> 16);
		int g = ((0x00FF00 & color) >> 8) + ((0x00FF00 & color2) >> 8);
		int b = (0x0000FF & color) + (0x0000FF & color2);

		if (r > g && r > b)
			return r - 0xFF;
		if (g > r && g > b)
			return g - 0xFF;
		return b - 0xFF;
	}

	public static int RGBToInt(int red, int green, int blue) {
		return (Mth.clamp(red, 0, 255) << 16) | (Mth.clamp(green, 0, 255) << 8) | Mth.clamp(blue, 0, 255);
	}

	public static int getGray(int value) {
		return RGBToInt(value, value, value);
	}
}
