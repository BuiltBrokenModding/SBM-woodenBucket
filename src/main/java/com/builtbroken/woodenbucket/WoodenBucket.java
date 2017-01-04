package com.builtbroken.woodenbucket;

import com.builtbroken.woodenbucket.bucket.ItemWoodenBucket;
import com.builtbroken.woodenbucket.bucket.PamFreshWaterBucketRecipe;
import com.builtbroken.woodenbucket.bucket.PamMilkBucketRecipe;
import com.builtbroken.woodenbucket.fluid.BlockMilk;
import com.builtbroken.woodenbucket.mods.BucketHandler;
import com.builtbroken.woodenbucket.mods.agricraft.AgricraftWaterPad;
import com.builtbroken.woodenbucket.mods.agricraft.AgricraftWaterPadFilled;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;

/**
 * Created by Dark on 7/25/2015.
 */
@Mod(modid = WoodenBucket.DOMAIN, name = "Wooden Bucket", version = "@MAJOR@.@MINOR@.@REVIS@.@BUILD@")
public class WoodenBucket
{
    public static final String DOMAIN = "woodenbucket";
    public static final String PREFIX = DOMAIN + ":";

    public static Logger LOGGER;

    public static ItemWoodenBucket itemBucket;

    public static Fluid fluid_milk;

    public static Configuration config;

    public static boolean PREVENT_HOT_FLUID_USAGE = true;
    public static boolean DAMAGE_BUCKET_WITH_HOT_FLUID = true;
    public static boolean BURN_ENTITY_WITH_HOT_FLUID = true;
    public static boolean GENERATE_MILK_FLUID = true;
    public static boolean ENABLE_FLUID_LEAKING = false;
    public static boolean ALLOW_LEAK_TO_CAUSE_FIRES = true;

    public static int VISCOSITY_TO_IGNORE_LEAKING = 3000;
    public static int AMOUNT_TO_LEAK = 1;
    public static float CHANCE_TO_LEAK = 0.03f;
    public static float LEAK_FIRE_CHANCE = 0.4f;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = LogManager.getLogger("WoodenBucket");
        config = new Configuration(new File(event.getModConfigurationDirectory(), "bbm/Wooden_Bucket.cfg"));
        config.load();

        PREVENT_HOT_FLUID_USAGE = config.getBoolean("PreventHotFluidUsage", "WoodenBucketUsage", PREVENT_HOT_FLUID_USAGE, "Enables settings that attempt to prevent players from wanting to use the bucket for moving hot fluids");
        DAMAGE_BUCKET_WITH_HOT_FLUID = config.getBoolean("DamageBucketWithHotFluid", "WoodenBucketUsage", DAMAGE_BUCKET_WITH_HOT_FLUID, "Will randomly destroy the bucket if it contains hot fluid, lava in other words");
        BURN_ENTITY_WITH_HOT_FLUID = config.getBoolean("BurnPlayerWithHotFluid", "WoodenBucketUsage", BURN_ENTITY_WITH_HOT_FLUID, "Will light the player on fire if the bucket contains a hot fluid, lava in other words");
        GENERATE_MILK_FLUID = config.getBoolean("EnableMilkFluidGeneration", Configuration.CATEGORY_GENERAL, GENERATE_MILK_FLUID, "Will generate a fluid for milk allowing for the bucket to be used for gathering milk from cows");
        ENABLE_FLUID_LEAKING = config.getBoolean("Enable", "Leaking", ENABLE_FLUID_LEAKING, "Allows fluid to slowly leak out of the bucket as a nerf. Requested by Darkosto");
        VISCOSITY_TO_IGNORE_LEAKING = config.getInt("MaxViscosity", "Leaking", VISCOSITY_TO_IGNORE_LEAKING, -1, 10000, "At which point it the flow rate so slow that the leak is plugged, higher values are slower");
        AMOUNT_TO_LEAK = config.getInt("MaxLeakAmount", "Leaking", AMOUNT_TO_LEAK, 0, 10000, "How much can leak from the bucket each time a leak happens, number is max amount and is randomly ranged between 0 - #");
        CHANCE_TO_LEAK = config.getFloat("LeakChance", "Leaking", CHANCE_TO_LEAK, 0f, 1f, "What is the chance that a leak will happen, calculated each tick with high numbers being more often");
        ALLOW_LEAK_TO_CAUSE_FIRES = config.getBoolean("AllowFires", "Leaking", ALLOW_LEAK_TO_CAUSE_FIRES, "If molten fluid leaks, should there be a chance to cause fires?");
        LEAK_FIRE_CHANCE = config.getFloat("FireChance", "Leaking", LEAK_FIRE_CHANCE, 0f, 1f, "How often to cause fire from molten fluids leaking");

        itemBucket = new ItemWoodenBucket();
        GameRegistry.registerItem(itemBucket, "wbBucket", DOMAIN);
        MinecraftForge.EVENT_BUS.register(itemBucket);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (Loader.isModLoaded("AgriCraft"))
        {
            BucketHandler.addBucketHandler(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPad, new AgricraftWaterPad());
            BucketHandler.addBucketHandler(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPadFull, new AgricraftWaterPadFilled());
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (GENERATE_MILK_FLUID && FluidRegistry.getFluid("milk") == null)
        {
            fluid_milk = new Fluid("milk");
            FluidRegistry.registerFluid(fluid_milk);
            Block blockMilk = new BlockMilk(fluid_milk);
            GameRegistry.registerBlock(blockMilk, "wbBlockMilk");
        }

        //TODO add crafting recipes for milk bucket
        // TODO add proper ore shaped recipes so modded sticks and other items can be used in the recipes
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.OAK.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 0), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.SPRUCE.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 1), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.BIRCH.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 2), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.JUNGLE.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 3), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.ACACIA.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 4), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.BIG_OAK.ordinal()), " s ", "wcw", " w ", 'w', new ItemStack(Blocks.planks, 1, 5), 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
        for (ItemStack itemstack : OreDictionary.getOres("planks"))
        {
            if (itemstack != null && itemstack.getItem() != Item.getItemFromBlock(Blocks.planks))
            {
                GameRegistry.addShapedRecipe(new ItemStack(itemBucket, 1, ItemWoodenBucket.BucketTypes.OAK.ordinal()), " s ", "wcw", " w ", 'w', itemstack, 's', Items.stick, 'c', new ItemStack(Items.dye, 1, 2));
            }
        }

        //TODO add pam's harvest craft support
        if (Loader.isModLoaded("harvestcraft"))
        {
            if (config.getBoolean("EnableRegisteringMilkBucket", "PamHarvestCraftSupport", true, "Registers the milk bucket to the ore dictionary to be used in Pam's Harvest Craft recipes"))
            {
                RecipeSorter.register(PREFIX + "woodenBucketFreshMilk", PamMilkBucketRecipe.class, SHAPED, "after:minecraft:shaped");
                if (FluidRegistry.getFluid("milk") != null)
                {
                    Item itemFreshMilk = (Item) Item.itemRegistry.getObject("harvestcraft:freshmilkItem");
                    if (itemFreshMilk == null)
                    {
                        LOGGER.error("Failed to find item harvestcraft:freshmilkItem");
                    }

                    FluidStack milkFluidStack = new FluidStack(FluidRegistry.getFluid("milk"), FluidContainerRegistry.BUCKET_VOLUME);
                    for (ItemWoodenBucket.BucketTypes type : ItemWoodenBucket.BucketTypes.values())
                    {
                        ItemStack milkBucket = new ItemStack(itemBucket, 1, type.ordinal());
                        itemBucket.fill(milkBucket, milkFluidStack, true);

                        GameRegistry.addRecipe(new PamMilkBucketRecipe(milkBucket, new ItemStack(itemFreshMilk, 4, 0)));
                    }
                }
            }
            if (config.getBoolean("EnableRegisteringFreshWaterBucket", "PamHarvestCraftSupport", true, "Registers the water bucket to the ore dictionary to be used in Pam's Harvest Craft recipes"))
            {
                RecipeSorter.register(PREFIX + "woodenBucketFreshMilk", PamFreshWaterBucketRecipe.class, SHAPED, "after:minecraft:shaped");
                if (FluidRegistry.getFluid("milk") != null)
                {
                    Item itemFreshWater = (Item) Item.itemRegistry.getObject("harvestcraft:freshwaterItem");
                    if (itemFreshWater == null)
                    {
                        LOGGER.error("Failed to find item harvestcraft:freshwaterItem");
                    }

                    FluidStack waterStack = new FluidStack(FluidRegistry.WATER, FluidContainerRegistry.BUCKET_VOLUME);
                    for (ItemWoodenBucket.BucketTypes type : ItemWoodenBucket.BucketTypes.values())
                    {
                        ItemStack milkBucket = new ItemStack(itemBucket, 1, type.ordinal());
                        itemBucket.fill(milkBucket, waterStack, true);

                        GameRegistry.addRecipe(new PamFreshWaterBucketRecipe(milkBucket, new ItemStack(itemFreshWater, 1, 0)));
                    }
                }
            }
        }
        config.save();
    }
}
