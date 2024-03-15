package com.neep.meatweapons;

import com.neep.meatlib.item.BaseCraftingItem;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.item.*;
import com.neep.meatweapons.item.meatgun.MeatgunModules;
import net.minecraft.item.Item;

public class MWItems
{
    public static Item BALLISTIC_CARTRIDGE = new BaseCraftingItem("ballistic_cartridge", 1, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item ENGINE = new BaseCraftingItem("engine", 0, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));
    public static Item FUSION_CANNON = new FusionCannonItem();
    public static Item HAND_CANNON = new HandCannonItem();
    public static Item MACHINE_PISTOL = new MachinePistolItem();
    public static Item LMG = new LMGItem();

    public static Item MA75 = new MA75Item();

    public static Item MEATGUN = ItemRegistry.queue(new MeatgunItem("meatgun", TooltipSupplier.blank(), new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

//    public static Item BLASTER = new BlasterItem();
    public static Item HEAVY_CANNON = new HeavyCannonItem();
    public static Item AIRTRUCK_ITEM = new AirtruckItem("airtruck", TooltipSupplier.hidden(2), new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static Item ASSAULT_DRILL = new AssaultDrillItem("assault_drill", 1000, new MeatlibItemSettings().group(MeatWeapons.WEAPONS));

    public static MeatgunModuleItem PISTOL = ItemRegistry.queue("pistol", new MeatgunModuleItem(MeatgunModules.PISTOL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem BOSHER = ItemRegistry.queue("bosher", new MeatgunModuleItem(MeatgunModules.BOSHER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem CHUGGER = ItemRegistry.queue("chugger", new MeatgunModuleItem(MeatgunModules.CHUGGER, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem LONG_BOI = ItemRegistry.queue("long_boi", new MeatgunModuleItem(MeatgunModules.LONG_BOI, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem UNDERBARREL = ItemRegistry.queue("underbarrel", new MeatgunModuleItem(MeatgunModules.UNDERBARREL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem DOUBLE_CAROUSEL = ItemRegistry.queue("double_carousel", new MeatgunModuleItem(MeatgunModules.DOUBLE_CAROUSEL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));
    public static MeatgunModuleItem TRIPLE_CAROUSEL = ItemRegistry.queue("triple_carousel", new MeatgunModuleItem(MeatgunModules.TRIPLE_CAROUSEL, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

    public static MeatgunModuleItem BATTERY = ItemRegistry.queue("battery", new MeatgunModuleItem(MeatgunModules.BATTERY, new MeatlibItemSettings().group(MeatWeapons.WEAPONS)));

    public static void init()
    {

    }
}
