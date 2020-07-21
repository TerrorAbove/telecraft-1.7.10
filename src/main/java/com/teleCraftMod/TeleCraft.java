package com.teleCraftMod;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.teleCraftMod.event.*;
import com.teleCraftMod.gui.GuiHandler;
import com.teleCraftMod.item.BladeOfTeleportation;
import com.teleCraftMod.item.BucketExchanger;
import com.teleCraftMod.item.EnderBucket;
import com.teleCraftMod.item.EnderBucketMilk;
import com.teleCraftMod.item.ItemBedrockDoor;
import com.teleCraftMod.item.ItemCompleteGrapple;
import com.teleCraftMod.item.ItemCustomArmor;
import com.teleCraftMod.item.ItemIronNugget;
import com.teleCraftMod.item.ItemPadlock;
import com.teleCraftMod.item.ItemPadlockCasing;
import com.teleCraftMod.item.ItemPadlockKey;
import com.teleCraftMod.item.ItemPadlockShackle;
import com.teleCraftMod.item.ItemReadyEmergencyTeleport;
import com.teleCraftMod.item.ItemStackableEmergencyTeleport;
import com.teleCraftMod.item.ItemSuperBucket;
import com.teleCraftMod.item.ItemTinyPins;
import com.teleCraftMod.item.ItemTinySprings;
import com.teleCraftMod.item.ItemWonderAlloy;
import com.teleCraftMod.item.ItemYaleLockCylinder;
import com.teleCraftMod.item.NecromancerStaff;
import com.teleCraftMod.item.NeptuneTrident;
import com.teleCraftMod.block.BlockChestRigged;
import com.teleCraftMod.block.BlockDoorBedrock;
import com.teleCraftMod.block.BlockLandMine;
import com.teleCraftMod.block.BlockPressurePlateBedrock;
import com.teleCraftMod.block.BlockPressurePlateTeleporter;
import com.teleCraftMod.entity.*;
import com.teleCraftMod.packet.BladeOfTeleportationPacket;
import com.teleCraftMod.packet.EmergencyTeleportPacket;
import com.teleCraftMod.packet.EnderBucketPacket;
import com.teleCraftMod.packet.GrapplePacket;
import com.teleCraftMod.packet.GrappleResponsePacket;
import com.teleCraftMod.packet.PadlockRightClickedPacket;
import com.teleCraftMod.packet.SetPinsPacket;
import com.teleCraftMod.packet.TeleporterPlatePacket;
import com.teleCraftMod.packet.ZombieSpawnPacket;
import com.teleCraftMod.proxy.*;
import com.teleCraftMod.rendering.RenderZombieMinion;
import com.teleCraftMod.tileentity.TileEntityRiggedChest;
import com.teleCraftMod.tileentity.TileEntityTelePlate;
import com.teleCraftMod.util.CustomShapedRecipe;
import com.teleCraftMod.util.CustomShapelessRecipe;

import tv.twitch.broadcast.GameInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

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
	public static final String VERSION = "1.5.0";
	public static final String NAME = "TeleCraft";

	public static String[] chest_destroy_allowed;
	public static String[] chest_place_banned;
	public static String[] chest_place_allowed;
	public static boolean creative_destroy;
	public static boolean pChest_place_whitelist;
	public static boolean shouldLandMineDestroyTerrain;
	public static boolean allowTelePlateItems;
	public static boolean allowTelePlateMobs;
	public static boolean allowTelePlatePassives;
	public static boolean allowTelePlatePlayers;

	@SidedProxy(clientSide="com.teleCraftMod.proxy.ClientProxy", serverSide="com.teleCraftMod.proxy.CommonProxy")
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper wrapper;

	public static Block wooden_teleporter;
	public static Block stone_teleporter;
	public static Block iron_teleporter;
	public static Block golden_teleporter;
	public static Block diamond_teleporter;

	public static Block bedrock_pressure_plate;
	public static Block bedrock_door_block;

	public static Block landMine;

	public static Block riggedChest;

	public static Block lockedDoor;

	public static ArmorMaterial WOOD = EnumHelper.addArmorMaterial("WOOD", 0, new int[]{0, 0, 0, 0}, 0);
	public static ArmorMaterial WONDERMETAL = EnumHelper.addArmorMaterial("WONDERMETAL", 20, new int[]{2, 6, 5, 2}, 15);

	public static ItemCustomArmor woodenLifePreserver;
	public static ItemCustomArmor armorOfFortitude;
	public static Item unforgedWonderAlloy;
	public static Item wonderIngot;

	public static Item bladeOfTeleportation;
	public static Item neptuneTrident;
	public static Item necroStaff;

	public static Item enderBucketEmpty;
	public static Item enderBucketWater;
	public static Item enderBucketLava;
	public static Item enderBucketMilk;

	public static Item emergencyStackableTeleport;
	public static Item emergencyReadyTeleport;

	public static Item bedrock_door_item;

	public static Item tiny_springs;
	public static Item tiny_pins;
	public static Item yale_lock_cylinder;
	public static Item padlock;
	public static Item padlock_key;
	public static Item padlock_casing;
	public static Item padlock_shackle;
	public static Item iron_nugget;
	
	public static Item superBucketEmpty;
	public static Item superBucketWater;
	public static Item superBucketLava;
	public static Item superBucketMilk;
	
	public static Item superEnderBucketEmpty;
	public static Item superEnderBucketWater;
	public static Item superEnderBucketLava;
	public static Item superEnderBucketMilk;

	public static Item completeGrapple;
	public static Item grappleHook;
	public static Item grappleLauncher;
	public static Item strongRope;
	
	public static Item waterBucketExchanger;
	public static Item lavaBucketExchanger;

	@Instance(MODID)
	public static TeleCraft instance;

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

		Configuration config = new Configuration(new File("config/TeleCraft.cfg"));

		config.load();

		chest_destroy_allowed = config.getStringList("allowedToBreakPersonalChests", Configuration.CATEGORY_GENERAL, new String[]{}, "The list of players who should be allowed to destroy (any) personal chests on any mode.");
		chest_place_banned = config.getStringList("bannedFromPlacingPersonalChests", Configuration.CATEGORY_GENERAL, new String[]{}, "The list of players who should be banned from placing personal chests on any mode.");
		creative_destroy = config.getBoolean("creativePChestDestroy", Configuration.CATEGORY_GENERAL, true, "Whether to allow any player to destroy any personal chest but only in Creative mode.");
		pChest_place_whitelist = config.getBoolean("pChestPlacementWhitelist", Configuration.CATEGORY_GENERAL, false, "Whether or not there should be a whitelist, where only specified players may place personal chests.");
		chest_place_allowed = config.getStringList("allowedToPlacePersonalChests", Configuration.CATEGORY_GENERAL, new String[]{}, "The list of players who should be allowed to place personal chests. ONLY APPLIES WHEN WHITELIST IS ENABLED.");
		shouldLandMineDestroyTerrain = config.getBoolean("landMineDestroysBlocks", Configuration.CATEGORY_GENERAL, true, "Whether a Land Mine should destroy surrounding blocks upon detonation.");
		allowTelePlateItems = config.getBoolean("allowTelePlateItems", "Teleport Plates", false, "Whether to allow items to be transported via tele plates");
		allowTelePlateMobs = config.getBoolean("allowTelePlateMobs", "Teleport Plates", false, "Whether to allow monsters to be transported via tele plates");
		allowTelePlatePassives = config.getBoolean("allowTelePlatePassives", "Teleport Plates", false, "Whether to allow passive creatures (e.g. cows) to be transported via tele plates");
		allowTelePlatePlayers = config.getBoolean("allowTelePlatePlayers", "Teleport Plates", true, "Whether to allow players to be transported via tele plates");
		
		config.save();

		wooden_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_wood", Material.wood, 4, 0.25F).setBlockName("woodenTeleporter");
		stone_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_stone", Material.rock, 8, 0.5F).setBlockName("stoneTeleporter");
		iron_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_iron", Material.iron, 16, 1.0F).setBlockName("ironTeleporter");
		golden_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_gold", Material.iron, 32, 2.0F).setBlockName("goldTeleporter");
		diamond_teleporter = new BlockPressurePlateTeleporter("TeleCraft:tele_plate_diamond", Material.iron, 64, 3.0F).setBlockName("diamondTeleporter");
		landMine = new BlockLandMine("telecraft:land_mine", Material.wood).setBlockName("landMine");
		riggedChest = new BlockChestRigged("TeleCraft:personal_chest").setBlockName("chestRigged");
		bedrock_pressure_plate = new BlockPressurePlateBedrock().setBlockName("pressurePlateBedrock");
		bedrock_door_block = new BlockDoorBedrock().setBlockName("doorBedrock").setBlockTextureName("TeleCraft:door_bedrock");
		bedrock_door_item = new ItemBedrockDoor(Material.rock).setUnlocalizedName("doorBedrockItem").setTextureName("TeleCraft:door_bedrock_item");

		int lp_rend = proxy.addArmor("life_preserver");
		int aof_rend = proxy.addArmor("armor_of_fortitude");

		unforgedWonderAlloy = new ItemWonderAlloy("telecraft:unforged_wonder_alloy", false);
		wonderIngot = new ItemWonderAlloy("telecraft:forged_wonder_alloy_ingot", true);
		woodenLifePreserver = (ItemCustomArmor) new ItemCustomArmor(ItemArmor.ArmorMaterial.valueOf("WOOD"), lp_rend, 1, "TeleCraft:life_preserver").setUnlocalizedName("lifePreserver").setCreativeTab(CreativeTabs.tabTransport);
		armorOfFortitude = (ItemCustomArmor) new ItemCustomArmor(ItemArmor.ArmorMaterial.valueOf("WONDERMETAL"), aof_rend, 1, "TeleCraft:armor_of_fortitude").setUnlocalizedName("armorOfFortitude");

		bladeOfTeleportation = new BladeOfTeleportation();
		neptuneTrident = new NeptuneTrident();
		necroStaff = new NecromancerStaff();

		enderBucketEmpty = new EnderBucket(Blocks.air).setUnlocalizedName("enderBucket").setMaxStackSize(16).setTextureName("TeleCraft:ender_bucket_empty");
		enderBucketWater = new EnderBucket(Blocks.flowing_water).setUnlocalizedName("enderBucketWater").setContainerItem(enderBucketEmpty).setTextureName("TeleCraft:ender_bucket_water");
		enderBucketLava = new EnderBucket(Blocks.flowing_lava).setUnlocalizedName("enderBucketLava").setContainerItem(enderBucketEmpty).setTextureName("TeleCraft:ender_bucket_lava");
		enderBucketMilk = new EnderBucketMilk().setUnlocalizedName("enderBucketMilk").setTextureName("TeleCraft:ender_bucket_milk");

		//TODO finish
		tiny_springs = new ItemTinySprings().setUnlocalizedName("tiny_springs");
		tiny_pins = new ItemTinyPins().setUnlocalizedName("tiny_pins");
		yale_lock_cylinder = new ItemYaleLockCylinder().setUnlocalizedName("yale_lock_cylinder");
		padlock = new ItemPadlock().setUnlocalizedName("padlock");
		padlock_key = new ItemPadlockKey().setUnlocalizedName("iron_padlock_key");
		padlock_casing = new ItemPadlockCasing().setUnlocalizedName("padlock_casing");
		padlock_shackle = new ItemPadlockShackle().setUnlocalizedName("padlock_shackle");
		
		iron_nugget = new ItemIronNugget().setUnlocalizedName("ironNugget");
		
		
		//this may be moved to its own mod later, with super ender buckets added if both mods installed
		superBucketEmpty = new ItemSuperBucket(Blocks.air, false).setMaxStackSize(16).setUnlocalizedName("superBucket");
		superBucketWater = new ItemSuperBucket(Blocks.flowing_water, false).setUnlocalizedName("superBucketWater").setContainerItem(superBucketEmpty);
		superBucketLava = new ItemSuperBucket(Blocks.flowing_lava, false).setUnlocalizedName("superBucketLava").setContainerItem(superBucketEmpty);
		//TODO superBucketMilk...?
		
		superEnderBucketEmpty = new ItemSuperBucket(Blocks.air, true).setMaxStackSize(16).setUnlocalizedName("superEnderBucket");
		superEnderBucketWater = new ItemSuperBucket(Blocks.flowing_water, true).setUnlocalizedName("superEnderBucketWater").setContainerItem(superEnderBucketEmpty);
		superEnderBucketLava = new ItemSuperBucket(Blocks.flowing_lava, true).setUnlocalizedName("superEnderBucketLava").setContainerItem(superEnderBucketEmpty);
		//TODO superEnderBucketMilk...?
		
		completeGrapple = new ItemCompleteGrapple().setUnlocalizedName("completeGrapple").setCreativeTab(CreativeTabs.tabTransport);
		grappleHook = new Item().setUnlocalizedName("grapplingHook").setTextureName("telecraft:grapple_hook").setMaxStackSize(16);
		grappleLauncher = new Item().setUnlocalizedName("grappleLauncher").setTextureName("telecraft:grapple_launcher").setMaxStackSize(16);
		strongRope = new Item().setUnlocalizedName("grapplingRope").setTextureName("telecraft:strong_rope").setMaxStackSize(16);
		
		waterBucketExchanger = new BucketExchanger((byte)0).setUnlocalizedName("superBucketExchangerWater");
		lavaBucketExchanger = new BucketExchanger((byte)1).setUnlocalizedName("superBucketExchangerLava");
		
		/*Iterator<Block> blocks = GameData.getBlockRegistry().iterator();
    	Iterator<Item> items = GameData.getItemRegistry().iterator();

    	ArrayList<ItemBucket> buckets = new ArrayList<ItemBucket>();

    	while(items.hasNext())
    	{
    		Item i = items.next();

    		if(i instanceof ItemBucket)
    		{
    			buckets.add((ItemBucket)i);
    		}
    	}

    	while(blocks.hasNext())
    	{
    		Block b = blocks.next();

    		if(b instanceof BlockDynamicLiquid && b != Blocks.flowing_water && b != Blocks.flowing_lava)
    		{
    			for(int i = 0; i < buckets.size(); i++)
    			{
    				//if(buckets.get(i).;
    			}
    			Item newBucket = new EnderBucket(b);
    			b.getLocalizedName();

    			//TODO fix. we want to add an enderbucket version of every mod-added bucket (where applicable)
    		}
    	}*/

		emergencyStackableTeleport = new ItemStackableEmergencyTeleport().setUnlocalizedName("emergencyTeleportTablet").setTextureName("TeleCraft:emergency_teleport").setCreativeTab(CreativeTabs.tabTransport);//TODO make texture change based on stack size?
		emergencyReadyTeleport = new ItemReadyEmergencyTeleport().setUnlocalizedName("emergencyReadyTeleportTablet").setTextureName("TeleCraft:emergency_teleport");

		//BIG TODO: consolidate all "non-teleport-themed" items/blocks/functionality to a new mod

		GameRegistry.registerBlock(wooden_teleporter, "woodenTeleporter");
		GameRegistry.registerBlock(stone_teleporter, "stoneTeleporter");
		GameRegistry.registerBlock(iron_teleporter, "ironTeleporter");
		GameRegistry.registerBlock(golden_teleporter, "goldTeleporter");
		GameRegistry.registerBlock(diamond_teleporter, "diamondTeleporter");
		GameRegistry.registerItem(bladeOfTeleportation, "bladeOfTeleportation");//TODO? make config boolean for whether to enable B.O.T.
		GameRegistry.registerItem(enderBucketEmpty, "enderBucketEmpty");
		GameRegistry.registerItem(enderBucketWater, "enderBucketWater");
		GameRegistry.registerItem(enderBucketLava, "enderBucketLava");
		GameRegistry.registerItem(enderBucketMilk, "enderBucketMilk");
		GameRegistry.registerItem(emergencyStackableTeleport, "emergencyTeleportTablet");
		GameRegistry.registerItem(emergencyReadyTeleport, "readyTeleportTablet");

		//Every block/item registered below this point is non-teleport

		GameRegistry.registerBlock(landMine, "landMine");
		GameRegistry.registerBlock(riggedChest, "personalChest");
		GameRegistry.registerBlock(bedrock_pressure_plate, "pressurePlateBedrock");
		GameRegistry.registerBlock(bedrock_door_block, "doorBedrock");
		GameRegistry.registerItem(bedrock_door_item, "doorBedrockItem");
		GameRegistry.registerItem(woodenLifePreserver, "lifePreserver");
		GameRegistry.registerItem(armorOfFortitude, "armorOfFortitude");
		GameRegistry.registerItem(unforgedWonderAlloy, "unforgedWonderAlloy");
		GameRegistry.registerItem(wonderIngot, "wonderIngot");
		GameRegistry.registerItem(neptuneTrident, "neptuneTrident");
		GameRegistry.registerItem(necroStaff, "necromancerStaff");
		GameRegistry.registerItem(tiny_springs, "tinySprings");
		GameRegistry.registerItem(tiny_pins, "tinyPins");
		GameRegistry.registerItem(yale_lock_cylinder, "yaleLockCylinder");
		GameRegistry.registerItem(padlock, "padlock");
		GameRegistry.registerItem(padlock_key, "padlockKey");
		GameRegistry.registerItem(padlock_casing, "padlockCasing");
		GameRegistry.registerItem(padlock_shackle, "padlockShackle");
		GameRegistry.registerItem(iron_nugget, "ironNugget");
		GameRegistry.registerItem(superBucketEmpty, "superBucketEmpty");
		GameRegistry.registerItem(superBucketWater, "superBucketWater");
		GameRegistry.registerItem(superBucketLava, "superBucketLava");
		GameRegistry.registerItem(superEnderBucketEmpty, "superEnderBucketEmpty");
		GameRegistry.registerItem(superEnderBucketWater, "superEnderBucketWater");
		GameRegistry.registerItem(superEnderBucketLava, "superEnderBucketLava");
		GameRegistry.registerItem(completeGrapple, "completeGrapple");
		GameRegistry.registerItem(grappleHook, "grapplingHook");
		GameRegistry.registerItem(grappleLauncher, "grappleLauncher");
		GameRegistry.registerItem(strongRope, "grapplingRope");
		GameRegistry.registerItem(waterBucketExchanger, "superBucketExchangerWater");
		GameRegistry.registerItem(lavaBucketExchanger, "superBucketExchangerLava");

		GameRegistry.registerTileEntity(TileEntityRiggedChest.class, "tileEntityRiggedChest");
		GameRegistry.registerTileEntity(TileEntityTelePlate.class, "tileEntityTCTeleportPlate");

		EntityRegistry.registerGlobalEntityID(EntityGrappleHook.class, "entityGrappleHook", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(EntityLandMinePrimed.class, "landMinePrimed", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerGlobalEntityID(ZombieMinion.class, "zombieMinion", EntityRegistry.findGlobalUniqueEntityId());
		
		//Start of event handling
		//NOTE: Adding an instance of a handler to the wrong bus will result in it being ignored!
		MinecraftForge.EVENT_BUS.register(new LivingHurtHandler());
		MinecraftForge.EVENT_BUS.register(new BlockEventsHandler());
		
		FMLCommonHandler.instance().bus().register(new PlayerTickHandler());

		wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("TeleCraft channel");
		wrapper.registerMessage(BladeOfTeleportationPacket.Handler.class, BladeOfTeleportationPacket.class, 0, Side.SERVER);
		wrapper.registerMessage(EnderBucketPacket.Handler.class, EnderBucketPacket.class, 1, Side.SERVER);
		wrapper.registerMessage(ZombieSpawnPacket.Handler.class, ZombieSpawnPacket.class, 2, Side.SERVER);
		//wrapper.registerMessage(TeleporterPlatePacket.Handler.class, TeleporterPlatePacket.class, 3, Side.SERVER);
		wrapper.registerMessage(EmergencyTeleportPacket.Handler.class, EmergencyTeleportPacket.class, 4, Side.SERVER);
		wrapper.registerMessage(SetPinsPacket.Handler.class, SetPinsPacket.class, 5, Side.SERVER);
		wrapper.registerMessage(PadlockRightClickedPacket.Handler.class, PadlockRightClickedPacket.class, 6, Side.SERVER);
		wrapper.registerMessage(GrapplePacket.Handler.class, GrapplePacket.class, 7, Side.SERVER);
		wrapper.registerMessage(GrappleResponsePacket.Handler.class, GrappleResponsePacket.class, 8, Side.CLIENT);
		
		proxy.handlePreInit();
	}

	@EventHandler
	public void load (FMLInitializationEvent event)
	{
		//NOTE: what appear to be "duplicates" are alternative recipes (unless they are accidently dupes lol)
		GameRegistry.addRecipe(new ItemStack(unforgedWonderAlloy, 9), new Object[] {"IDI", "DGD", "IDI", 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'D', Items.diamond});
		GameRegistry.addRecipe(new ItemStack(unforgedWonderAlloy, 9), new Object[] {"DID", "IGI", "DID", 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'D', Items.diamond});
		GameRegistry.addRecipe(new ItemStack(armorOfFortitude), new Object[] {"W W", "WWW", "WWW", 'W', wonderIngot});
		GameRegistry.addRecipe(new ItemStack(woodenLifePreserver), new Object[] {"W W", "WWW", "WWW", 'W', Blocks.planks});//TODO change recipe to include wool and/or leather?
		GameRegistry.addRecipe(new ItemStack(wooden_teleporter, 16), new Object[] {"#E#", '#', Blocks.planks, 'E', Items.ender_pearl});
		GameRegistry.addRecipe(new ItemStack(stone_teleporter, 8), new Object[] {"#E#", '#', Blocks.stone, 'E', Items.ender_pearl});
		GameRegistry.addRecipe(new ItemStack(iron_teleporter, 4), new Object[] {"#E#", '#', Items.iron_ingot, 'E', Items.ender_pearl});
		GameRegistry.addRecipe(new ItemStack(golden_teleporter, 2), new Object[] {"#E#", '#', Items.gold_ingot, 'E', Items.ender_pearl});
		GameRegistry.addRecipe(new ItemStack(diamond_teleporter, 2), new Object[] {"#E#", '#', Items.diamond, 'E', Items.ender_pearl});
		GameRegistry.addRecipe(new ItemStack(riggedChest), new Object[] {"###", "#T#", "###", '#', Blocks.planks, 'T', Items.name_tag});
		GameRegistry.addRecipe(new ItemStack(bladeOfTeleportation), new Object[] {"OPO", "PDP", "OPO", 'O', Blocks.obsidian, 'P', Items.ender_pearl, 'D', Items.diamond_sword});
		GameRegistry.addRecipe(new ItemStack(bladeOfTeleportation), new Object[] {"POP", "ODO", "POP", 'O', Blocks.obsidian, 'P', Items.ender_pearl, 'D', Items.diamond_sword});
		GameRegistry.addShapedRecipe(new ItemStack(enderBucketEmpty), new Object[] {"IEI", " I ", 'I', Items.iron_ingot, 'E', Items.ender_eye});
		GameRegistry.addShapedRecipe(new ItemStack(grappleLauncher), new Object[] {"III", "  I", 'I', Items.iron_ingot});
		GameRegistry.addShapedRecipe(new ItemStack(strongRope), new Object[] {"SS", "SS", "SS", 'S', Items.string});
		GameRegistry.addRecipe(new ItemStack(grappleHook), new Object[] {"I I", " I ", "I I", 'I', Items.iron_ingot});
		
		/*
		 * start of grapple repair
		 */
		List grappleRepairList = new ArrayList();
		grappleRepairList.add(new ItemStack(Items.iron_ingot));
		grappleRepairList.add(new ItemStack(completeGrapple, 1, 32767));//32767 means allow all damage values for input
		
		GameRegistry.addRecipe(new CustomShapelessRecipe(new ItemStack(completeGrapple), grappleRepairList, 1));
		
		grappleRepairList = new ArrayList();
		grappleRepairList.add(new ItemStack(completeGrapple, 1, 32767));
		grappleRepairList.add(new ItemStack(completeGrapple, 1, 32767));
		GameRegistry.addRecipe(new CustomShapelessRecipe(new ItemStack(completeGrapple), grappleRepairList, 1));
		
		/*
		 * start of blade of teleportation repair
		 */
		List bladeRepairList = new ArrayList();
		bladeRepairList.add(new ItemStack(Items.ender_pearl));
		bladeRepairList.add(new ItemStack(bladeOfTeleportation, 1, 32767));//32767 means allow all damage values for input
		
		//ender pearl restores 25%
		GameRegistry.addRecipe(new CustomShapelessRecipe(new ItemStack(bladeOfTeleportation), bladeRepairList, 1));
		
		bladeRepairList = new ArrayList();
		bladeRepairList.add(new ItemStack(Items.diamond));
		bladeRepairList.add(new ItemStack(bladeOfTeleportation, 1, 32767));
		
		//diamond restores 100%
		GameRegistry.addRecipe(new CustomShapelessRecipe(new ItemStack(bladeOfTeleportation), bladeRepairList, 1));
		
		
		//TODO organize sections
		
		GameRegistry.addShapedRecipe(new ItemStack(waterBucketExchanger), new Object[] {"EBE", 'E', Items.emerald, 'B', Items.water_bucket});
		GameRegistry.addShapedRecipe(new ItemStack(lavaBucketExchanger), new Object[] {"EBE", 'E', Items.emerald, 'B', Items.lava_bucket});
		
		GameRegistry.addShapelessRecipe(new ItemStack(completeGrapple), grappleHook, grappleLauncher, strongRope);
		GameRegistry.addShapelessRecipe(new ItemStack(iron_nugget, 9), Items.iron_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.bucket, 9), superBucketEmpty);
		GameRegistry.addShapelessRecipe(new ItemStack(enderBucketEmpty, 9), superEnderBucketEmpty);
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot), "###", "###", "###", '#', iron_nugget);
		GameRegistry.addRecipe(new ItemStack(superBucketEmpty), "###", "###", "###", '#', Items.bucket);
		GameRegistry.addRecipe(new ItemStack(superEnderBucketEmpty), "###", "###", "###", '#', enderBucketEmpty);
		//GameRegistry.addRecipe(CustomShapelessRecipe.getRecipeForItemDuplicates(new ItemStack(superBucketWater), Items.water_bucket, 9));
		//GameRegistry.addRecipe(CustomShapelessRecipe.getRecipeForItemDuplicates(new ItemStack(superBucketLava), Items.lava_bucket, 9));
		//GameRegistry.addRecipe(CustomShapelessRecipe.getRecipeForItemDuplicates(new ItemStack(Items.water_bucket, 9), superBucketWater, 1));
		//GameRegistry.addRecipe(CustomShapelessRecipe.getRecipeForItemDuplicates(new ItemStack(Items.lava_bucket, 9), superBucketLava, 1));
		
		//TODO recipe to combine super buckets with same liquid type
		
		
		//compatability code for certain mods' Iron Nuggets... (includes our own iron nugget)
		//railcraft, flaxbeard's steamcraft, tinker's construct, thaumcraft, thermal foundation
		final Item[] ironNuggets = new Item[] {
			GameRegistry.findItem("railcraft", "item.railcraft.nugget.iron"),
			GameRegistry.findItem("steamcraft", "item.steamcraft:nugget.2"),
			GameRegistry.findItem("tconstruct", "item.tconstruct.Materials.IronNugget"),
			GameRegistry.findItem("thaumcraft", "item.ItemNugget.0"),
			GameRegistry.findItem("thermalfoundation", "item.thermalfoundation.material.nuggetIron"),
			iron_nugget
		};

		for(int i = 0; i < ironNuggets.length; i++)
		{
			Item nugget = ironNuggets[i];
			
			if(nugget != null)
			{
				GameRegistry.addRecipe(new ItemStack(tiny_springs), "nnn", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(tiny_pins), "n", "n", "n", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(yale_lock_cylinder), " n ", "n n", " n ", 'n', nugget);//TODO cylinder graphic looks like it already would have pins and springs in it
				GameRegistry.addRecipe(new ItemStack(padlock_casing), "nsn", "npn", "ncn", 'n', nugget, 'p', tiny_pins, 's', tiny_springs, 'c', yale_lock_cylinder);
				GameRegistry.addRecipe(new ItemStack(padlock_shackle), "nnn", "n n", "  n", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(padlock_key), "nn ", " n ", "n n", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(padlock_key), " nn", " n ", "n n", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(padlock_key), "  n", "nn ", "n n", 'n', nugget);
				GameRegistry.addRecipe(new ItemStack(padlock_key), "n n", " nn", "n  ", 'n', nugget);//TODO sensible key recipes
			}
		}

		List l = new ArrayList();
		l.add(new ItemStack(Items.iron_ingot));
		l.add(new ItemStack(padlock_key));
		
		GameRegistry.addRecipe(new CustomShapelessRecipe(new ItemStack(padlock_key, 2), l));
		GameRegistry.addRecipe(new CustomShapedRecipe(1, 3, new ItemStack[] {new ItemStack(padlock_shackle), new ItemStack(padlock_casing), new ItemStack(padlock_key)}, new ItemStack(padlock)));
		
		GameRegistry.addShapelessRecipe(new ItemStack(landMine, 6), new Object[] {Blocks.wooden_pressure_plate, Blocks.tnt, Blocks.glass});
		GameRegistry.addShapelessRecipe(new ItemStack(enderBucketEmpty), new Object[] {Items.ender_eye, Items.bucket});
		GameRegistry.addShapelessRecipe(new ItemStack(enderBucketWater), new Object[] {Items.ender_eye, Items.water_bucket});
		GameRegistry.addShapelessRecipe(new ItemStack(enderBucketLava), new Object[] {Items.ender_eye, Items.lava_bucket});
		GameRegistry.addShapelessRecipe(new ItemStack(enderBucketMilk), new Object[] {Items.ender_eye, Items.milk_bucket});
		GameRegistry.addShapelessRecipe(new ItemStack(emergencyStackableTeleport, 4), new Object[] {Items.ender_pearl, Blocks.stone_button, Blocks.stone_slab});

		GameRegistry.addSmelting(new ItemStack(unforgedWonderAlloy), new ItemStack(wonderIngot), 10.0F);
		
		RecipeSorter.register("customShapedRecipe", CustomShapedRecipe.class, Category.SHAPED, "");
		RecipeSorter.register("customShapelessRecipe", CustomShapelessRecipe.class, Category.SHAPELESS, "");

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

		proxy.handleLoad();
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		//event.registerServerCommand(new ZombieSpawnCommand());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	public static double roundToSignificantFigures(double num, int n) {
		if(num == 0) {
			return 0;
		}

		final double d = Math.ceil(Math.log10(num < 0 ? -num: num));
		final int power = n - (int) d;

		final double magnitude = Math.pow(10, power);
		final long shifted = Math.round(num*magnitude);
		return shifted/magnitude;
	}

	//CLIENT only
	public static final MovingObjectPosition getTarget(float par1, double distance)
	{
		Minecraft mc = Minecraft.getMinecraft();

		Entity pointedEntity;
		double d0 = distance;
		MovingObjectPosition omo = mc.renderViewEntity.rayTrace(d0, par1);
		double d1 = d0;
		Vec3 vec3 = mc.renderViewEntity.getPosition(par1);
		Vec3 vec31 = mc.renderViewEntity.getLook(par1);
		Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
		pointedEntity = null;
		Vec3 vec33 = null;
		float f1 = 1.0F;
		List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.renderViewEntity, mc.renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
		double d2 = d1;

		for (int i = 0; i < list.size(); ++i)
		{
			Entity entity = (Entity)list.get(i);

			if (entity.canBeCollidedWith())
			{
				float f2 = entity.getCollisionBorderSize();
				AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
				MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

				if (axisalignedbb.isVecInside(vec3))
				{
					if (0.0D < d2 || d2 == 0.0D)
					{
						pointedEntity = entity;
						vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
						d2 = 0.0D;
					}
				}
				else if (movingobjectposition != null)
				{
					double d3 = vec3.distanceTo(movingobjectposition.hitVec);

					if (d3 < d2 || d2 == 0.0D)
					{
						if (entity == mc.renderViewEntity.ridingEntity && !entity.canRiderInteract())
						{
							if (d2 == 0.0D)
							{
								pointedEntity = entity;
								vec33 = movingobjectposition.hitVec;
							}
						}
						else
						{
							pointedEntity = entity;
							vec33 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}
		}
		if (pointedEntity != null && (d2 < d1 || omo == null))
		{
			omo = new MovingObjectPosition(pointedEntity, vec33);

			if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
			{
				mc.pointedEntity = pointedEntity;
			}
		}
		if (omo != null)
		{
			if (omo.typeOfHit == MovingObjectType.ENTITY)
			{
				if(omo.entityHit instanceof EntityLivingBase)
				{
					return omo;
				}
			}
		}
		return null;
	}

	public static EntityLivingBase closestELBToPoint(World world, double x, double y, double z, double offset)
	{
		List list = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(x-offset, y-offset, z-offset, x+offset, y+offset, z+offset));

		double smallDist = Double.MAX_VALUE;
		EntityLivingBase closest = null;

		for(Object object : list)
		{
			if(object instanceof EntityLivingBase)
			{
				EntityLivingBase elb = (EntityLivingBase)object;
				double dist = elb.getDistance(x, y, z);

				if(dist < smallDist)
				{
					smallDist = dist;
					closest = elb;
				}
			}
		}
		return closest;
	}

	public static int getColorForRGB(int red, int green, int blue)
	{
		return new Color(red, green, blue).getRGB();
	}
}