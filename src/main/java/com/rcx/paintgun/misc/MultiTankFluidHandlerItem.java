package com.rcx.paintgun.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class MultiTankFluidHandlerItem implements IFluidHandlerItem, IMultiTankFluidHandler, ICapabilityProvider {

	public static final String FLUID_NBT_KEY = "fluid";

	private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

	@NotNull
	protected ItemStack container;
	protected int tanks;
	protected int capacity;

	/**
	 * @param container  The container itemStack, data is stored on it directly as NBT.
	 * @param capacity   The maximum capacity of this fluid tank.
	 */
	public MultiTankFluidHandlerItem(@NotNull ItemStack container, int tanks, int capacity) {
		this.container = container;
		this.tanks = tanks;
		this.capacity = capacity;
	}

	public void setFluid(int tank, FluidStack fluid) {
		if (!container.getOrCreateTag().contains(FLUID_NBT_KEY)) {
			ListTag fluids = new ListTag();
			for (int i = 0; i < getTanks(); ++i) {
				CompoundTag fluidTag = new CompoundTag();
				if (i == tank) {
					fluid.writeToNBT(fluidTag);
				} else {
					FluidStack.EMPTY.writeToNBT(fluidTag);
				}
				fluids.add(fluidTag);
			}
			return;
		}

		ListTag fluids = container.getTag().getList(FLUID_NBT_KEY, Tag.TAG_COMPOUND);
		CompoundTag fluidTag = new CompoundTag();
		fluid.writeToNBT(fluidTag);
		fluids.set(tank, fluidTag);
		container.getTag().put(FLUID_NBT_KEY, fluids);
	}

	@NotNull
	@Override
	public ItemStack getContainer() {
		return container;
	}

	@Override
	public int getTanks() {
		return tanks;
	}

	@NotNull
	@Override
	public FluidStack getFluidInTank(int tank) {
		if (!container.hasTag() || !container.getTag().contains(FLUID_NBT_KEY)) {
			return FluidStack.EMPTY;
		}
		ListTag fluids = container.getTag().getList(FLUID_NBT_KEY, Tag.TAG_COMPOUND);
		return FluidStack.loadFluidStackFromNBT(fluids.getCompound(tank));
	}

	@Override
	public int getTankCapacity(int tank) {
		return capacity;
	}

	@Override
	public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
		return true;
	}

	@Override
	public int fill(FluidStack resource, FluidAction doFill) {
		if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource)) {
			return 0;
		}
		//only one tank can ever contain a certain fluid, we don't want fluid overflowing into the next tank when one is full
		for (int i = 0; i < getTanks(); ++i) {
			if (resource.isFluidEqual(getFluidInTank(i))) {
				return fill(i, resource, doFill);
			}
		}
		//if none of the tanks already contain this fluid, try filling the first available one
		for (int i = 0; i < getTanks(); ++i) {
			int filled = fill(i, resource, doFill);
			if (filled > 0)
				return filled;
		}
		return 0;
	}

	@Override
	public int fill(int tank, FluidStack resource, FluidAction doFill) {
		if (container.getCount() != 1 || resource.isEmpty() || !canFillFluidType(resource)) {
			return 0;
		}

		FluidStack contained = getFluidInTank(tank);
		if (contained.isEmpty()) {
			int fillAmount = Math.min(capacity, resource.getAmount());

			if (doFill.execute()) {
				FluidStack filled = resource.copy();
				filled.setAmount(fillAmount);
				setFluid(tank, filled);
			}
			return fillAmount;
		} else {
			if (contained.isFluidEqual(resource)) {
				int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

				if (doFill.execute() && fillAmount > 0) {
					contained.grow(fillAmount);
					setFluid(tank, contained);
				}
				return fillAmount;
			}
			return 0;
		}
	}

	@NotNull
	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (container.getCount() != 1 || resource.isEmpty()) {
			return FluidStack.EMPTY;
		}
		int toDrain = resource.getAmount();
		FluidStack drainedTotal = FluidStack.EMPTY;
		for (int i = 0; i < getTanks(); ++i) {
			if (resource.isFluidEqual(getFluidInTank(i))) {
				FluidStack drained = drain(i, toDrain, action);
				if (drainedTotal.isEmpty()) {
					drainedTotal = drained;
				} else {
					drainedTotal.grow(drained.getAmount());
				}
				toDrain -= drained.getAmount();
				if (toDrain <= 0)
					break;
			}
		}
		return drainedTotal;
	}

	@NotNull
	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		if (container.getCount() != 1 || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}
		int toDrain = maxDrain;
		FluidStack drainedTotal = FluidStack.EMPTY;
		for (int i = 0; i < getTanks(); ++i) {
			if (drainedTotal.isEmpty() || getFluidInTank(i).isFluidEqual(drainedTotal)) {
				FluidStack drained = drain(i, toDrain, action);
				if (drainedTotal.isEmpty()) {
					drainedTotal = drained;
				} else {
					drainedTotal.grow(drained.getAmount());
				}
				toDrain -= drained.getAmount();
				if (toDrain <= 0)
					break;
			}
		}
		return drainedTotal;
	}

	@NotNull
	@Override
	public FluidStack drain(int tank, int maxDrain, FluidAction action) {
		if (container.getCount() != 1 || maxDrain <= 0) {
			return FluidStack.EMPTY;
		}

		FluidStack contained = getFluidInTank(tank);
		if (contained.isEmpty() || !canDrainFluidType(contained)) {
			return FluidStack.EMPTY;
		}

		final int drainAmount = Math.min(contained.getAmount(), maxDrain);

		FluidStack drained = contained.copy();
		drained.setAmount(drainAmount);

		if (action.execute()) {
			contained.shrink(drainAmount);
			if (contained.isEmpty()) {
				setContainerToEmpty();
			} else {
				setFluid(tank, contained);
			}
		}
		return drained;
	}

	public boolean canFillFluidType(FluidStack fluid) {
		return true;
	}

	public boolean canDrainFluidType(FluidStack fluid) {
		return true;
	}

	/**
	 * Override this method for special handling.
	 * Can be used to swap out or destroy the container.
	 */
	protected void setContainerToEmpty() {
		container.removeTagKey(FLUID_NBT_KEY);
	}

	@Override
	@NotNull
	public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
		return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
	}
}
