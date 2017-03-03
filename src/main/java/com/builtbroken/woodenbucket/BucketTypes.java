package com.builtbroken.woodenbucket;

import com.builtbroken.mc.fluids.FluidModule;
import com.builtbroken.mc.fluids.bucket.BucketMaterial;
import net.minecraft.item.ItemStack;

public enum BucketTypes
{
    OAK,
    ACACIA,
    BIRCH,
    JUNGLE,
    SPRUCE,
    BIG_OAK,
    CHARRED;

    public BucketMaterial material;

    public ItemStack getBucket()
    {
        return new ItemStack(FluidModule.bucket, 1, material.metaValue);
    }
}