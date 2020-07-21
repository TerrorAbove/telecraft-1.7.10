package com.teleCraftMod.packet;

import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.EntityGrappleHook;
import com.teleCraftMod.item.ItemCompleteGrapple;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class GrapplePacket implements IMessage
{
	private double x, y, z, grappleX, grappleY, grappleZ;
	private byte type;//0=block, 1=entity
	
	public static class Handler implements IMessageHandler<GrapplePacket, GrappleResponsePacket>
	{
		@Override
		public GrappleResponsePacket onMessage(GrapplePacket message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER && ctx.getServerHandler().playerEntity != null)
			{
				EntityPlayer p = ctx.getServerHandler().playerEntity;
				World w = p.worldObj;
				
				if(message.type == 1)
				{
					Entity b = w.getEntityByID((int)message.x);//x here is the entity ID, ONLY WHEN type is 1
					
					if(b instanceof EntityLivingBase)
					{
						EntityLivingBase target = (EntityLivingBase)b;
						
						if(target instanceof EntityPlayer && !p.canAttackPlayer((EntityPlayer)target))
						{
							EntityPlayer p2 = (EntityPlayer)target;
							p.addChatComponentMessage(new ChatComponentText("You cannot attack "+p2.getDisplayName()+"!"));
							return null;
						}
						
						Vec3 diff = Vec3.createVectorHelper((int)target.posX, (int)target.posY, (int)target.posZ).subtract(Vec3.createVectorHelper((int)p.posX, (int)p.posY, (int)p.posZ));
						
						boolean neg;
						
						if(diff.yCoord < 1)
							diff.yCoord = 1;
						
						diff.yCoord = Math.sqrt(diff.yCoord);
						diff.yCoord /= 2;

						/*neg = diff.zCoord < 0;
						diff.zCoord = Math.sqrt(Math.abs(diff.zCoord));

						if(neg)
							diff.zCoord = -diff.zCoord;
						diff.zCoord /= 2;

						neg = diff.xCoord < 0;
						diff.xCoord = Math.sqrt(Math.abs(diff.xCoord));

						if(neg)
							diff.xCoord = -diff.xCoord;
						diff.xCoord /= 2;*/
						
						diff.xCoord /= 7;
						diff.zCoord /= 7;
						
						target.attackEntityFrom(new DamageSource("Grapple"), ((float)diff.lengthVector())/4.0F);
						target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 80, 3));
						target.addVelocity(diff.xCoord, diff.yCoord, diff.zCoord);
						
						if(p.getHeldItem() != null)
						{
							if(p.getHeldItem().getItem() == TeleCraft.completeGrapple)
							{
								p.getHeldItem().damageItem((int)(5.0 * diff.lengthVector()/ItemCompleteGrapple.MAX_GRAPPLE_DISTANCE + 0.5), p);
							}
						}
					}
				}
				else
				{
					Vec3 diff = Vec3.createVectorHelper(p.posX - message.grappleX, p.posY - message.grappleY, p.posZ - message.grappleZ);
					
					if(p.getHeldItem() != null)
					{
						if(p.getHeldItem().getItem() == TeleCraft.completeGrapple)
						{
							p.getHeldItem().damageItem((int)(5.0 * diff.lengthVector()/ItemCompleteGrapple.MAX_GRAPPLE_DISTANCE + 0.5), p);
							p.addPotionEffect(new PotionEffect(Potion.resistance.id, 80, 5));
						}
					}
				}
			}
			return null;
		}
	}
	
	public GrapplePacket() {}
	
	public GrapplePacket(double x, double y, double z, double grappleX, double grappleY, double grappleZ, byte type)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.grappleX = grappleX;
		this.grappleY = grappleY;
		this.grappleZ = grappleZ;
		this.type = type;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		grappleX = buf.readDouble();
		grappleY = buf.readDouble();
		grappleZ = buf.readDouble();
		type = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(grappleX);
		buf.writeDouble(grappleY);
		buf.writeDouble(grappleZ);
		buf.writeByte(type);
	}
}
