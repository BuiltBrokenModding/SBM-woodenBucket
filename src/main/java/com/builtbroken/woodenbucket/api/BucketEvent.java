package com.builtbroken.woodenbucket.api;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.item.ItemStack;

/**
 * Base class for all bucket events
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 9/9/2016.
 */
public class BucketEvent extends Event
{
    public final ItemStack bucketStack;

    public BucketEvent(ItemStack bucketStack)
    {
        this.bucketStack = bucketStack;
    }
}
