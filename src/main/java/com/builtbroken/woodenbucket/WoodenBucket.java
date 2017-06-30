package com.builtbroken.woodenbucket;

import com.builtbroken.mc.fluids.api.reg.BucketMaterialRegistryEvent;
import com.builtbroken.mc.fluids.bucket.BucketMaterialHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = WoodenBucket.DOMAIN, name = "Wooden Bucket", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@", dependencies = "after:vefluids")
@Mod.EventBusSubscriber(modid = WoodenBucket.DOMAIN)
public class WoodenBucket
{
    public static final String DOMAIN = "woodenbucket";
    public static final String PREFIX = DOMAIN + ":";

    public static Logger LOGGER;

    public static Configuration config;

    public static boolean PREVENT_HOT_FLUID_USAGE = true;
    public static boolean DAMAGE_BUCKET_WITH_HOT_FLUID = true;
    public static boolean BURN_ENTITY_WITH_HOT_FLUID = true;
    public static boolean ENABLE_FLUID_LEAKING = false;
    public static boolean ALLOW_LEAK_TO_CAUSE_FIRES = true;

    public static int VISCOSITY_TO_IGNORE_LEAKING = 3000;
    public static int AMOUNT_TO_LEAK = 1;
    public static float CHANCE_TO_LEAK = 0.03f;
    public static float LEAK_FIRE_CHANCE = 0.4f;

    public WoodenBucket()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenBucket");
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Bucket.cfg"));
        config.load();
    }

    @SubscribeEvent
    public void registerBucketMaterials(BucketMaterialRegistryEvent.Pre event)
    {
        for (BucketTypes type : BucketTypes.values())
        {
            type.material = new WoodenBucketMaterial(type);
            BucketMaterialHandler.addMaterial(type.name().toLowerCase(), type.material, type.ordinal());
        }
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        //TODO add crafting recipes for milk bucket
        ResourceLocation location = new ResourceLocation(DOMAIN, "woodenbucket");
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.OAK.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 0), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.oak"));
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.SPRUCE.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 1), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.spruce"));
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.BIRCH.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 2), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.birch"));
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.JUNGLE.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 3), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.jungle"));
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.ACACIA.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 4), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.acacia"));
        event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.BIG_OAK.getBucket(), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.PLANKS, 1, 5), 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.big_oak"));
        for (ItemStack itemstack : OreDictionary.getOres("planks"))
        {
            if (itemstack != null && itemstack.getItem() != Item.getItemFromBlock(Blocks.PLANKS))
            {
                event.getRegistry().register(new ShapedOreRecipe(location, BucketTypes.OAK.getBucket(), " s ", "wcw", " w ", 'w', itemstack, 's', "stickWood", 'c', "dye").setRegistryName("bucket.wood.z" + itemstack));
            }
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        PREVENT_HOT_FLUID_USAGE = config.getBoolean("PreventHotFluidUsage", "WoodenBucketUsage", PREVENT_HOT_FLUID_USAGE, "Enables settings that attempt to prevent players from wanting to use the bucket for moving hot fluids");
        DAMAGE_BUCKET_WITH_HOT_FLUID = config.getBoolean("DamageBucketWithHotFluid", "WoodenBucketUsage", DAMAGE_BUCKET_WITH_HOT_FLUID, "Will randomly destroy the bucket if it contains hot fluid, lava in other words");
        BURN_ENTITY_WITH_HOT_FLUID = config.getBoolean("BurnPlayerWithHotFluid", "WoodenBucketUsage", BURN_ENTITY_WITH_HOT_FLUID, "Will light the player on fire if the bucket contains a hot fluid, lava in other words");
        ENABLE_FLUID_LEAKING = config.getBoolean("Enable", "Leaking", ENABLE_FLUID_LEAKING, "Allows fluid to slowly leak out of the bucket as a nerf. Requested by Darkosto");
        VISCOSITY_TO_IGNORE_LEAKING = config.getInt("MaxViscosity", "Leaking", VISCOSITY_TO_IGNORE_LEAKING, -1, 10000, "At which point it the flow rate so slow that the leak is plugged, higher values are slower");
        AMOUNT_TO_LEAK = config.getInt("MaxLeakAmount", "Leaking", AMOUNT_TO_LEAK, 0, 10000, "How much can leak from the bucket each time a leak happens, number is max amount and is randomly ranged between 0 - #");
        CHANCE_TO_LEAK = config.getFloat("LeakChance", "Leaking", CHANCE_TO_LEAK, 0f, 1f, "What is the chance that a leak will happen, calculated each tick with high numbers being more often");
        ALLOW_LEAK_TO_CAUSE_FIRES = config.getBoolean("AllowFires", "Leaking", ALLOW_LEAK_TO_CAUSE_FIRES, "If molten fluid leaks, should there be a chance to cause fires?");
        LEAK_FIRE_CHANCE = config.getFloat("FireChance", "Leaking", LEAK_FIRE_CHANCE, 0f, 1f, "How often to cause fire from molten fluids leaking");

        for (BucketTypes type : BucketTypes.values())
        {
            type.material.preventHotFluidUsage = PREVENT_HOT_FLUID_USAGE;
            type.material.damageBucketWithHotFluid = DAMAGE_BUCKET_WITH_HOT_FLUID;
            type.material.burnEntityWithHotFluid = BURN_ENTITY_WITH_HOT_FLUID;
            type.material.enableFluidLeaking = ENABLE_FLUID_LEAKING;
            type.material.viscosityToIgnoreLeaking = VISCOSITY_TO_IGNORE_LEAKING;
            type.material.amountToLeak = AMOUNT_TO_LEAK;
            type.material.chanceToLeak = CHANCE_TO_LEAK;
            type.material.allowLeakToCauseFires = ALLOW_LEAK_TO_CAUSE_FIRES;
            type.material.leakFireChance = LEAK_FIRE_CHANCE;
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {


    }
}
