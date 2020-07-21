package teleCraftMod;

import java.util.Arrays;

import teleCraftMod.event.*;
import teleCraftMod.proxy.*;
import tv.twitch.broadcast.GameInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod
(
	modid						= TeleCraft.MODID,
	name 						= TeleCraft.NAME,
	version 					= TeleCraft.VERSION,
	dependencies 				= "required-after:Forge@[10.13.1.1217,)",
	acceptedMinecraftVersions	= "[1.7.10,)",
	canBeDeactivated = false
)
public class TeleCraft
{
    public static final String MODID = "TeleCraft";
    public static final String VERSION = "1.4.2";
    public static final String NAME = "Teleport Craft";
    
    @SidedProxy(clientSide="teleCraftMod.proxy.ClientProxy", serverSide="teleCraftMod.proxy.CommonProxy")
    public static CommonProxy proxy;
    
    public static Block wooden_teleporter;
    public static Block stone_teleporter;
    public static Block iron_teleporter;
    public static Block golden_teleporter;
    public static Block diamond_teleporter;
    
    public static Block landMine;
    
    public static Block riggedChest;
    
    //public static Block darkTorch;
    
    public static ArmorMaterial WOOD = EnumHelper.addArmorMaterial("WOOD", 10, new int[]{1, 4, 3, 1}, 10);
    public static ArmorMaterial WONDERMETAL = EnumHelper.addArmorMaterial("WONDERMETAL", 20, new int[]{2, 6, 5, 2}, 15);
    
    public static ItemCustomArmor woodenLifePreserver;
    public static ItemCustomArmor armorOfFortitude;
    public static Item unforgedWonderAlloy;
    public static Item wonderIngot;
    
    public static Item bladeOfTeleportation;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent preInitEvent)
    {
    	ModMetadata meta = preInitEvent.getModMetadata();
    	meta.modId=MODID;
    	meta.name=NAME;
    	meta.version=VERSION;
    	meta.description="Teleport Craft is a simple mod adding some new blocks and functionality. The key feature is the various tiers of teleport plates. These can be crafted using an ender pearl.";
    	meta.url="";
    	meta.updateUrl="";
    	meta.authorList=Arrays.asList (new String[] { "Terror Above", "Bluesnake198" });
    	meta.credits="Programmed by Terror, textured by Bluesnake";
    	meta.logoFile="";//relative to the location of the mcmod.info file
    	
    	wooden_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_wood", Material.wood, 2, 0.25F).setBlockName("woodenTeleporter");
    	stone_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_stone", Material.rock, 4, 0.5F).setBlockName("stoneTeleporter");
    	iron_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_iron", Material.iron, 8, 1.0F).setBlockName("ironTeleporter");
    	golden_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_gold", Material.iron, 16, 2.0F).setBlockName("goldTeleporter");
    	diamond_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_diamond", Material.iron, 32, 3.0F).setBlockName("diamondTeleporter");
    	landMine = new BlockLandMine("telecraft:land_mine", Material.wood).setBlockName("landMine");
    	riggedChest = new BlockChestRigged().setBlockName("chestRigged");
    	//darkTorch = new BlockDarkTorch().setBlockName("darkTorch");
    	
    	int lp_rend = proxy.addArmor("life_preserver");
    	int aof_rend = proxy.addArmor("armor_of_fortitude");
    	
    	unforgedWonderAlloy = new ItemWonderAlloy("telecraft:unforged_wonder_alloy", false);
    	wonderIngot = new ItemWonderAlloy("telecraft:forged_wonder_alloy_ingot", true);
    	woodenLifePreserver = (ItemCustomArmor) new ItemCustomArmor(ItemArmor.ArmorMaterial.valueOf("WOOD"), lp_rend, 1, "TeleCraft:life_preserver").setUnlocalizedName("lifePreserver");
    	armorOfFortitude = (ItemCustomArmor) new ItemCustomArmor(ItemArmor.ArmorMaterial.valueOf("WONDERMETAL"), aof_rend, 1, "TeleCraft:armor_of_fortitude").setUnlocalizedName("armorOfFortitude");
    	
    	//bladeOfTeleportation = new BladeOfTeleportation(ToolMaterial.EMERALD);
    	
    	GameRegistry.registerBlock(wooden_teleporter, "woodenTeleporter");
    	GameRegistry.registerBlock(stone_teleporter, "stoneTeleporter");
    	GameRegistry.registerBlock(iron_teleporter, "ironTeleporter");
    	GameRegistry.registerBlock(golden_teleporter, "goldTeleporter");
    	GameRegistry.registerBlock(diamond_teleporter, "diamondTeleporter");
    	GameRegistry.registerBlock(landMine, "landMine");
    	GameRegistry.registerBlock(riggedChest, "chestRigged");
    	//GameRegistry.registerBlock(darkTorch, "darkTorch");
    	
    	GameRegistry.registerItem(woodenLifePreserver, "lifePreserver");
    	GameRegistry.registerItem(armorOfFortitude, "armorOfFortitude");
    	
    	GameRegistry.registerItem(unforgedWonderAlloy, "unforgedWonderAlloy");
    	GameRegistry.registerItem(wonderIngot, "wonderIngot");
    	
    	//GameRegistry.registerItem(bladeOfTeleportation, "bladeOfTeleportation");
    	
    	EntityRegistry.registerGlobalEntityID(EntityLandMinePrimed.class, "landMinePrimed", 4105);
    	
    	//Start of event handling
    	//NOTE: Adding an instance of a handler to the wrong bus will result in it being ignored!
    	MinecraftForge.EVENT_BUS.register(new PlayerHurtHandler());
    	FMLCommonHandler.instance().bus().register(new PlayerTickHandler());
    	
    	proxy.handlePreInit();
    }
    
    @EventHandler
    public void load (FMLInitializationEvent event)
    {
    	GameRegistry.addRecipe(new ItemStack(unforgedWonderAlloy, 9), new Object[] {"GDG", "DID", "GDG", 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'D', Items.diamond});
    	GameRegistry.addRecipe(new ItemStack(armorOfFortitude), new Object[] {"W W", "WWW", "WWW", 'W', wonderIngot});
    	GameRegistry.addRecipe(new ItemStack(woodenLifePreserver), new Object[] {"W W", "WWW", "WWW", 'W', Blocks.planks});
    	GameRegistry.addRecipe(new ItemStack(wooden_teleporter, 8), new Object[] {"#E#", '#', Blocks.planks, 'E', Items.ender_pearl});
        GameRegistry.addRecipe(new ItemStack(stone_teleporter, 4), new Object[] {"#E#", '#', Blocks.stone, 'E', Items.ender_pearl});
        GameRegistry.addRecipe(new ItemStack(iron_teleporter, 2), new Object[] {"#E#", '#', Items.iron_ingot, 'E', Items.ender_pearl});
        GameRegistry.addRecipe(new ItemStack(golden_teleporter, 1), new Object[] {"#E#", '#', Items.gold_ingot, 'E', Items.ender_pearl});
        GameRegistry.addRecipe(new ItemStack(diamond_teleporter, 1), new Object[] {"#E#", '#', Items.diamond, 'E', Items.ender_pearl});
        GameRegistry.addRecipe(new ItemStack(riggedChest), new Object[] {"###", "#T#", "###", '#', Blocks.planks, 'T', Items.flint_and_steel});
        //GameRegistry.addRecipe(new ItemStack(darkTorch, 8), new Object[] {"###", "#O#", "###", '#', Blocks.torch, 'O', Blocks.obsidian});
        GameRegistry.addShapelessRecipe(new ItemStack(landMine, 6), new Object[] {Blocks.wooden_pressure_plate, Blocks.tnt, Blocks.glass});
        
        GameRegistry.addSmelting(new ItemStack(unforgedWonderAlloy), new ItemStack(wonderIngot), 10.0F);
        
        proxy.handleLoad();
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	
    }
}