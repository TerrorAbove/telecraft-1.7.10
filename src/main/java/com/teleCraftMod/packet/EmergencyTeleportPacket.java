package com.teleCraftMod.packet;

import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.ZombieMinion;
import com.teleCraftMod.gui.EnterTextScreen;
import com.teleCraftMod.item.ItemReadyEmergencyTeleport;
import com.teleCraftMod.item.ItemStackableEmergencyTeleport;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class EmergencyTeleportPacket implements IMessage
{
	private boolean doTeleport;
	private String identifier;
	
	public EmergencyTeleportPacket(boolean doTeleport)
	{
		this(doTeleport, "");
	}
	
	public EmergencyTeleportPacket(boolean doTeleport, String identifier)
	{
		this.doTeleport = doTeleport;
		this.identifier = identifier;
	}
	
	public EmergencyTeleportPacket(){}
	
	public static class Handler implements IMessageHandler<EmergencyTeleportPacket, IMessage>
	{
		public IMessage onMessage(EmergencyTeleportPacket message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			boolean doTP = message.doTeleport;
			
			if(player != null)
			{
				World world = player.worldObj;
				if(world != null)
				{
					ItemStack stack = player.getHeldItem();
					if(stack != null)
					{
						if(stack.getTagCompound() == null)
							stack.setTagCompound(new NBTTagCompound());

						NBTTagCompound c = stack.getTagCompound();
						
						if(doTP)
						{
							if(stack.getItem() instanceof ItemReadyEmergencyTeleport)
							{
								if(stack.isItemDamaged() && --stack.stackSize == 0)
									return null;

								if(c.hasKey("TeleportLocation") && c.hasKey("TeleportWorldName"))
								{
									String[] coords = c.getString("TeleportLocation").split(",");
									String worldName = c.getString("TeleportWorldName");

									double X = 0, Y = 0, Z = 0;

									try
									{
										X = Double.parseDouble(coords[0]);
										Y = Double.parseDouble(coords[1]);
										Z = Double.parseDouble(coords[2]);
									}
									catch(NumberFormatException nfe)
									{
										nfe.printStackTrace();
										return null;
									}

									if(world.provider.getDimensionName().equalsIgnoreCase(worldName))
									{
										if(!player.capabilities.isCreativeMode)
											player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
										
										player.setPositionAndUpdate(X, Y, Z);

										final ItemStack S = stack;
										final EntityPlayer P = player;
										final World W = world;

										if(!player.capabilities.isCreativeMode)
										{
											player.capabilities.disableDamage = true;
											//2 sec later, disable invulnerability and damage item
											new Timer().schedule(new TimerTask()
											{
												@Override
												public void run()
												{
													if(P != null && !P.capabilities.isCreativeMode)
													{
														P.capabilities.disableDamage = false;
													}
												}
											}, 2000);
										}
									}
									else
									{
										player.addChatMessage(new ChatComponentText("You can only use this in the same world you were in before!"));
									}
								}
								else
								{
									System.err.println("Player attempted to use a ready emergency teleport which had no location set!");
								}
							}
						}
						else
						{
							if(stack.getItem() instanceof ItemStackableEmergencyTeleport && stack.stackSize > 0)
							{
								//player.openGui(TeleCraft.instance, EnterTextScreen.GUI_ID, world, (int)player.posX, (int)player.posY + 1, (int)player.posZ);
								
								ItemStack toAdd = new ItemStack(TeleCraft.emergencyReadyTeleport);
								
								//below is a rounding trick to round to the nearest quarter
								double x = (int)(4 * player.posX) / 4.0;
								double y = player.posY;
								double z = (int)(4 * player.posZ) / 4.0;
								
								c.setString("TeleportLocation", x+","+y+","+z);
								c.setString("TeleportWorldName", world.provider.getDimensionName());
								c.setString("TeleportIdentifier", message.identifier);
								
								toAdd.setTagCompound(c);
								
								if(!player.capabilities.isCreativeMode)
								{
									if(stack.stackSize > 1)
										stack.stackSize--;
									else
										player.inventory.setInventorySlotContents(player.inventory.currentItem, null);//TODO may need to change this
								}
								
								if(!player.inventory.addItemStackToInventory(toAdd))
								{
									ItemStack currItem = player.inventory.getStackInSlot(player.inventory.currentItem);
									
									if(currItem != null && currItem.getItem() instanceof ItemStackableEmergencyTeleport)
									{
										currItem.stackSize++;//give the player back his/her item since we couldn't complete the operation
									}
								}
								else
								{
									ItemReadyEmergencyTeleport.storeRandomColor(toAdd);
								}
								
								if(player.capabilities.isCreativeMode)
									player.addChatMessage(new ChatComponentText("Location set."));
								else
									player.addChatMessage(new ChatComponentText("Location set. This tablet will break upon use."));
							}
						}
					}
				}
			}			
			return null;
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.doTeleport = buf.readBoolean();
		this.identifier = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(doTeleport);
		ByteBufUtils.writeUTF8String(buf, identifier);
	}
}
