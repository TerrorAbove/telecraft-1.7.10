package com.teleCraftMod.packet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.BladeOfTeleportation;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class BladeOfTeleportationPacket implements IMessage
{
	private int entityId;
	private long timeSent;
	
	public BladeOfTeleportationPacket(int id, long time)
	{
		entityId = id;
		timeSent = time;
	}
	
	public BladeOfTeleportationPacket(){}
	
	public static class Handler implements IMessageHandler<BladeOfTeleportationPacket, IMessage>
	{
		public IMessage onMessage(BladeOfTeleportationPacket message, MessageContext ctx)
		{
			try
			{
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				Entity target = player.worldObj.getEntityByID(message.entityId);
				
				if(target instanceof EntityPlayer && !player.canAttackPlayer((EntityPlayer)target))
					return null;
				
				ItemStack held = player.getHeldItem();
				
				if(held == null || held.getItem() != TeleCraft.bladeOfTeleportation)
					return null;
				
				if(target == null || System.currentTimeMillis() - message.timeSent > 3000)
					return null;
				
				Vec3 vec = Vec3.createVectorHelper((int)(target.posX - player.posX), (int)(target.posY - player.posY), (int)(target.posZ - player.posZ));
				
				//possible TODO: check for weapon reach enchantment?
				if(vec.lengthVector() < 6)
					return null;
				
				//adjust for direction looking
				if(vec.xCoord > 0)
					vec.xCoord--;
				else if(vec.xCoord < 0)
					vec.xCoord++;
				
				if(vec.zCoord > 0)
					vec.zCoord--;
				else if(vec.zCoord < 0)
					vec.zCoord++;
				
				final double DIST = vec.lengthVector();
				
				double x = player.posX + vec.xCoord;
				double y = player.posY + vec.yCoord;
				double z = player.posZ + vec.zCoord;
				
				int cooldown = BladeOfTeleportation.getCooldownForStack(held);
				
				if(cooldown < 8 && DIST <= (8 - cooldown) * 8)
				{
					World world = player.worldObj;
					
					for(int i = 0; i < 3; i++)
					{
						Block lower = world.getBlock((int)x, (int)y-2, (int)z);
						Block middle = world.getBlock((int)x, (int)y-1, (int)z);
						Block upper = world.getBlock((int)x, (int)y, (int)z);
						
						if((lower.getMaterial().isSolid() || lower.getMaterial() == Material.water)
							&& (!middle.getMaterial().blocksMovement() && middle.getMaterial() != Material.lava)
							&&  upper.getMaterial() == Material.air)
						{
							
							player.setPositionAndUpdate(x, y, z);
							
							//short damage boost, if we're fully charged
							if(cooldown == 0)
								player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), 33, 0, false));
							
							if(held.getTagCompound() == null)
								held.setTagCompound(new NBTTagCompound());
							
							if(!player.capabilities.isCreativeMode)
							{
								held.getTagCompound().setLong("last_off_teleport", System.currentTimeMillis());
								held.getTagCompound().setInteger("last_target_id", target.getEntityId());
								held.damageItem((int)(DIST / 2), player);
							}
							
							break;
						}
						
						y++;
					}
				}
				
				/*
				//will force tag compound update
				if(held.hasTagCompound())
					held.getTagCompound().setLong("last_off_teleport", held.getTagCompound().getLong("last_off_teleport")+1);
				*/
			}
			catch(Exception ex){}
			return null;
		}
	}

	public void fromBytes(ByteBuf buf) {
		entityId = buf.readInt();
		timeSent = buf.readLong();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeLong(timeSent);
	}
}
