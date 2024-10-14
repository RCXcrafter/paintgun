package com.rcx.paintgun.misc;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IMultiTankFluidHandler extends IFluidHandler {

	public int fill(int tank, FluidStack resource, FluidAction doFill);

	public FluidStack drain(int tank, int maxDrain, FluidAction action);
}
