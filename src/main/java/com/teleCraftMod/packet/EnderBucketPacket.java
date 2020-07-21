package com.teleCraftMod.packet;

import com.mojang.realmsclient.dto.PlayerInfo;
import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.BladeOfTeleportation;
import com.teleCraftMod.item.EnderBucket;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class EnderBucketPacket implements IMessage
{
	private ItemStack stack;
	
	public EnderBucketPacket(ItemStack stack)
	{
		this.stack = stack;
	}
	
	public EnderBucketPacket(){}
	
	public static class Handler implements IMessageHandler<EnderBucketPacket, EnderBucketPacket>
	{
		public EnderBucketPacket onMessage(EnderBucketPacket message, MessageContext ctx)
		{
			try
			{
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				World world = player.worldObj;
				ItemStack someStack = message.stack;
				Item theItem = someStack.getItem();
				
				if(someStack.getTagCompound().getBoolean("cow"))
				{
					InventoryPlayer inv = player.inventory;
					
					if (someStack.stackSize-- == 1)
		            {
		                inv.setInventorySlotContents(inv.currentItem, new ItemStack(TeleCraft.enderBucketMilk));
		            }
		            else if (!inv.addItemStackToInventory(new ItemStack(TeleCraft.enderBucketMilk)))
		            {
		                player.dropPlayerItemWithRandomChoice(new ItemStack(TeleCraft.enderBucketMilk, 1, 0), false);
		            }
                	inv.markDirty();
					someStack.getTagCompound().setBoolean("cow", false);
					return null;
				}
				
				int x = someStack.getTagCompound().getInteger("x");
				int y = someStack.getTagCompound().getInteger("y");
				int z = someStack.getTagCompound().getInteger("z");
				
				float fx = someStack.getTagCompound().getFloat("fx");
				float fy = someStack.getTagCompound().getFloat("fy");
				float fz = someStack.getTagCompound().getFloat("fz");
				
				Block selectedBlock = world.getBlock(x, y, z);
				int meta = world.getBlockMetadata(x, y, z);
				
				if(someStack.getItem() == TeleCraft.enderBucketEmpty)
					player.setCurrentItemOrArmor(0, new ItemStack(Items.bucket));
				else if(someStack.getItem() == TeleCraft.enderBucketMilk)
					player.setCurrentItemOrArmor(0, new ItemStack(Items.milk_bucket));
				else if(someStack.getItem() == TeleCraft.enderBucketWater)
					player.setCurrentItemOrArmor(0, new ItemStack(Items.water_bucket));
				else if(someStack.getItem() == TeleCraft.enderBucketLava)
					player.setCurrentItemOrArmor(0, new ItemStack(Items.lava_bucket));

				int max_dist = player.capabilities.isCreativeMode ? 8 : 6;
				
				//if the selected block does something special on activation...
				if(player.getDistance(x, y, z) <= max_dist && selectedBlock.onBlockActivated(world, x, y, z, player, meta, fx, fy, fz))//last 3 floats are block-relative coords where you clicked on the block
				{
					ItemStack curr = player.getHeldItem();
					
					if(curr != null)
					{
						ItemStack toGive = null;
						
						if(curr.getItem() == Items.bucket)
							toGive = new ItemStack(TeleCraft.enderBucketEmpty);
						else if(curr.getItem() == Items.milk_bucket)
							toGive = new ItemStack(TeleCraft.enderBucketMilk);
						else if(curr.getItem() == Items.water_bucket)
							toGive = new ItemStack(TeleCraft.enderBucketWater);
						else if(curr.getItem() == Items.lava_bucket)
							toGive = new ItemStack(TeleCraft.enderBucketLava);
						
						if(toGive != null)
						{
							InventoryPlayer inv = player.inventory;
							
							inv.setInventorySlotContents(inv.currentItem, someStack);
							
							if (someStack.stackSize-- == 1)
				            {
				                inv.setInventorySlotContents(inv.currentItem, toGive);
				            }
				            else if (!inv.addItemStackToInventory(toGive))
				            {
				                player.dropPlayerItemWithRandomChoice(toGive, false);
				            }
							
		                	inv.markDirty();
						}
					}
					
					return null;
				}
				
				player.setCurrentItemOrArmor(0, someStack);
				
				int sideHit = someStack.getTagCompound().getInteger("side");

				if (theItem instanceof EnderBucket)
				{
					if (sideHit == 0)
                	{
                    	--y;
                	}

                	if (sideHit == 1)
                	{
                    	++y;
                	}

                	if (sideHit == 2)
                	{
                    	--z;
                	}

                	if (sideHit == 3)
                	{
                		++z;
                	}
                
                	if (sideHit == 4)
                	{
                		--x;
                	}

                	if (sideHit == 5)
                	{
                		++x;
                	}
                	
					EnderBucket bucket = (EnderBucket)theItem;
					InventoryPlayer inv = player.inventory;
					if (bucket.getFluid() == Blocks.air)
                	{
                    	Material mat = world.getBlock(x, y, z).getMaterial();
                    	int metadata = world.getBlockMetadata(x, y, z);

                    	if (mat == Material.water && metadata == 0)
                    	{
                        	world.setBlockToAir(x, y, z);
                        	ItemStack end = bucket.func_150910_a(someStack, player, TeleCraft.enderBucketWater);
        		            inv.setInventorySlotContents(inv.currentItem, end);
                        	inv.markDirty();
                        	return null;
                    	}
                    
                    	if (mat == Material.lava && metadata == 0)
                    	{
                        	world.setBlockToAir(x, y, z);
                        	ItemStack end = bucket.func_150910_a(someStack, player, TeleCraft.enderBucketLava);
        		            inv.setInventorySlotContents(inv.currentItem, end);
                        	inv.markDirty();
                        	return null;
                    	}
                	}
                	else
                	{
                    	if (bucket.tryPlaceContainedLiquid(world, x, y, z) && !player.capabilities.isCreativeMode)
                    	{
                    		inv.setInventorySlotContents(inv.currentItem, new ItemStack(TeleCraft.enderBucketEmpty));
                        	inv.markDirty();
                    		return null;
                    	}
                	}
				}
			}
			catch(Exception ex){}
			return null;
		}
	}

	public void fromBytes(ByteBuf buf) {
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, stack);
	}
}