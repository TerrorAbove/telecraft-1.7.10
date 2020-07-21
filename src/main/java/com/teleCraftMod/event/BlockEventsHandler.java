package com.teleCraftMod.event;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.block.BlockChestRigged;
import com.teleCraftMod.item.EnderBucket;
import com.teleCraftMod.tileentity.TileEntityRiggedChest;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;

public class BlockEventsHandler
{
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onBlockBreakEvent(BlockEvent.BreakEvent breakEvent)
	{
		EntityPlayer player = breakEvent.getPlayer();
		Block b = breakEvent.block;
		
		if(player != null && !breakEvent.isCanceled() && b instanceof BlockChestRigged)
		{
			//TODO: may need to send a packet to the server...
			//TODO: message?
			
			TileEntity te = breakEvent.world.getTileEntity(breakEvent.x, breakEvent.y, breakEvent.z);
			
			if(te instanceof TileEntityRiggedChest)
			{
				TileEntityRiggedChest rc = (TileEntityRiggedChest)te;
				
				boolean allowed = false;
				for(String s : TeleCraft.chest_destroy_allowed)
				{
					if(player.getCommandSenderName().equalsIgnoreCase(s))
					{
						allowed = true;
						break;
					}
				}
				
				if(!allowed && (!rc.isUseableByPlayer(player) && (!player.capabilities.isCreativeMode || !TeleCraft.creative_destroy)))
				{
					breakEvent.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.LOW)
	public void onBlockPlacedEvent(BlockEvent.PlaceEvent placeEvent)
	{
		if(!placeEvent.isCanceled() && placeEvent.block instanceof BlockChestRigged)
		{
			EntityPlayer p = placeEvent.player;
			BlockChestRigged b = (BlockChestRigged)placeEvent.block;
			World w = placeEvent.world;
			int x = placeEvent.x;
			int y = placeEvent.y;
			int z = placeEvent.z;
			int flag = placeEvent.blockSnapshot.flag;
			
			TileEntity te = w.getTileEntity(x, y, z);
			
			if(te instanceof TileEntityRiggedChest)
			{
				Block block = w.getBlock(x, y, z - 1);
		        Block block1 = w.getBlock(x, y, z + 1);
		        Block block2 = w.getBlock(x - 1, y, z);
		        Block block3 = w.getBlock(x + 1, y, z);
		        
		        boolean checkB = block instanceof BlockChest;
		        boolean checkB1 = block1 instanceof BlockChest;
		        boolean checkB2 = block2 instanceof BlockChest;
		        boolean checkB3 = block3 instanceof BlockChest;
		        
		        if(checkB)
		        {
		        	TileEntity teB = w.getTileEntity(x, y, z - 1);
		        	if(teB instanceof TileEntityRiggedChest)
		        	{
		        		TileEntityRiggedChest rc0 = (TileEntityRiggedChest)teB;
		        		int old_meta = rc0.getOriginalMeta();
		        		if(!p.getCommandSenderName().equalsIgnoreCase(rc0.getOwner()))
		        		{
		        			placeEvent.setCanceled(true);
		        			w.setBlockMetadataWithNotify(x, y, z-1, old_meta, flag);
		        			return;
		        		}
		        	}
		        	else if(teB instanceof TileEntityChest)
		        	{
		        		placeEvent.setCanceled(true);
	        			//cant fix rotation here sadly
	        			return;
		        	}
		        }
		        if(checkB1)
		        {
		        	TileEntity teB1 = w.getTileEntity(x, y, z + 1);
		        	if(teB1 instanceof TileEntityRiggedChest)
		        	{
		        		TileEntityRiggedChest rc1 = (TileEntityRiggedChest)teB1;
		        		int old_meta = rc1.getOriginalMeta();
		        		if(!p.getCommandSenderName().equalsIgnoreCase(rc1.getOwner()))
		        		{
		        			placeEvent.setCanceled(true);
		        			w.setBlockMetadataWithNotify(x, y, z+1, old_meta, flag);
		        			return;
		        		}
		        	}
		        	else if(teB1 instanceof TileEntityChest)
		        	{
		        		placeEvent.setCanceled(true);
		        		//cant fix rotation here sadly
	        			return;
		        	}
		        }
		        if(checkB2)
		        {
		        	TileEntity teB2 = w.getTileEntity(x - 1, y, z);
		        	if(teB2 instanceof TileEntityRiggedChest)
		        	{
		        		TileEntityRiggedChest rc2 = (TileEntityRiggedChest)teB2;
		        		int old_meta = rc2.getOriginalMeta();
		        		if(!p.getCommandSenderName().equalsIgnoreCase(rc2.getOwner()))
		        		{
		        			placeEvent.setCanceled(true);
		        			w.setBlockMetadataWithNotify(x-1, y, z, old_meta, flag);
		        			return;
		        		}
		        	}
		        	else if(teB2 instanceof TileEntityChest)
		        	{
		        		placeEvent.setCanceled(true);
		        		//cant fix rotation here sadly
	        			return;
		        	}
		        }
		        if(checkB3)
		        {
		        	TileEntity teB3 = w.getTileEntity(x + 1, y, z);
		        	if(teB3 instanceof TileEntityRiggedChest)
		        	{
		        		TileEntityRiggedChest rc3 = (TileEntityRiggedChest)teB3;
		        		int old_meta = rc3.getOriginalMeta();
		        		if(!p.getCommandSenderName().equalsIgnoreCase(rc3.getOwner()))
		        		{
		        			placeEvent.setCanceled(true);
		        			w.setBlockMetadataWithNotify(x+1, y, z, old_meta, flag);
		        			return;
		        		}
		        	}
		        	else if(teB3 instanceof TileEntityChest)
		        	{
		        		placeEvent.setCanceled(true);
		        		//cant fix rotation here sadly
	        			return;
		        	}
		        }
		        
		        //time to check permissions...
		        
		        boolean whitelisted = false;
		        if(TeleCraft.pChest_place_whitelist)
		        {
		        	for(String s : TeleCraft.chest_place_allowed)
		        	{
		        		if(p.getCommandSenderName().equalsIgnoreCase(s))
		        		{
		        			whitelisted = true;
		        			break;
		        		}
		        	}
		        	if(!whitelisted)
		        	{
		        		placeEvent.setCanceled(true);
			        	return;
		        	}
		        }
		        
		        for(String s : TeleCraft.chest_place_banned)
		        {
		        	if(p.getCommandSenderName().equalsIgnoreCase(s))
		        	{
		        		placeEvent.setCanceled(true);
		        		return;
		        	}
		        }

				TileEntityRiggedChest rc = (TileEntityRiggedChest)te;
				rc.setOwner(p.getCommandSenderName());
				rc.setOriginalMeta((byte) w.getBlockMetadata(x, y, z));
			}
		}
	}
}
