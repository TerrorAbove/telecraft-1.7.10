package com.teleCraftMod.event;

import java.util.Iterator;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHealth;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import sun.net.www.content.audio.wav;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.BladeOfTeleportation;
import com.teleCraftMod.item.NecromancerStaff;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class LivingHurtHandler extends CustomArmorEventHandler
{
	private static final double FORTITUDE_FACTOR = 0.85; //damage reduced by 15% per mob
	
	@SubscribeEvent(priority=EventPriority.HIGH)
	public void onLivingHurtEvent(LivingHurtEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
			/*if(event.source.getSourceOfDamage() instanceof EntityMob)
			{
				if(isWearingEquipment(player, TeleCraft.armorOfFortitude, CHEST) && event.ammount > 0)
				{
					double damage = event.ammount;
					
					double x = player.posX;
					double y = player.posY;
					double z = player.posZ;
					
					List list = player.worldObj.getEntitiesWithinAABB(EntityMob.class, AxisAlignedBB.getBoundingBox(x-5, y-1, z-5, x+5, y+1, z+5));
					
					for(int i = 1; i < list.size(); i++)//start at i = 1 so that reduction applies only to groups
					{
						damage *= FORTITUDE_FACTOR;
					}
					
					if(player instanceof EntityPlayerMP && damage < event.ammount)
					{
						double ratio = damage / (double)event.ammount;
						double percentBlocked = 100 * (1.0 - ratio);
						
						EntityPlayerMP p = (EntityPlayerMP)player;
						p.addChatMessage(new ChatComponentText("Your armor glimmers as it nullifies "+(int)(percentBlocked + 0.5)+"% of an incoming hit."));
					}
					
					event.ammount = (float)damage;
				}
			}*/
//			if(BladeOfTeleportation.canTeleportAttacker(player))
//			{
//				Entity attacker = event.source.getEntity();
//				if(attacker != null && attacker instanceof EntityLivingBase)
//				{
//					EntityLivingBase elb = (EntityLivingBase)attacker;
//					
//					boolean foundLocation = false;
//					
//					double newX = 0, newY = 0, newZ = 0;
//					double DIST = 0;
//					
//					int tries = 0;
//					
//					do
//					{
//						double angle = Math.random() * Math.PI * 2;
//						double distance = 25 + Math.random()*20;
//						Vec3 offset = Vec3.createVectorHelper(distance * Math.cos(angle), -5, distance * Math.sin(angle));
//						
//						newX = player.posX + offset.xCoord;
//						newY = player.posY + offset.yCoord;
//						newZ = player.posZ + offset.zCoord;
//						
//						int entityHeight = elb instanceof EntityEnderman ? 3 : 2;
//						
//						Block block0 = attacker.worldObj.getBlock((int)newX, (int)newY, (int)newZ);
//						
//						int max = 15;
//						while(max-- > 0 && !foundLocation)
//						{
//							if(block0.getMaterial().isSolid() || block0.getMaterial() == Material.water
//									|| (attacker.worldObj.provider.isHellWorld && block0.getMaterial() == Material.lava) && !(attacker instanceof EntityPlayer))
//							{
//								boolean isAir = true;
//								for(int i = 0; i < entityHeight; i++)
//								{
//									Material mat = attacker.worldObj.getBlock((int)newX, (int)newY+i+1, (int)newZ).getMaterial();
//									isAir = isAir && (i == entityHeight-1 ? mat == Material.air : !mat.isSolid());
//								}
//								if(isAir)
//								{
//									foundLocation = true;
//								}
//							}
//							block0 = attacker.worldObj.getBlock((int)newX, (int)++newY, (int)newZ);
//						}
//						
//						DIST = Math.sqrt(Math.pow(newX - elb.posX, 2) + Math.pow(newY - elb.posY, 2) + Math.pow(newZ - elb.posZ, 2));
//						
//						if(foundLocation)
//						{
//							elb.setPositionAndUpdate(newX, newY, newZ);
//							
//							if(player.getHeldItem() != null)
//							{
//								player.heal(1 + (int)(Math.random()*3));
//								player.getHeldItem().damageItem((int)(-DIST / 4), elb);
//							}
//							
//							/*if(player instanceof EntityPlayerMP)
//							{
//								//TODO this will be removed in a future update
//								((EntityPlayerMP)player).addChatMessage(new ChatComponentText("Your blade's defensive magic teleports your attacker "+(int)(DIST+0.5)+" blocks away."));
//							}*/
//						}
//						
//						tries++;
//					}
//					while(!foundLocation && tries < 10);
//				}
//			}
		}
		else
		{
			EntityLivingBase elb = event.entityLiving;
			if(event.source.getSourceOfDamage() instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)event.source.getSourceOfDamage();
				if(player.getHeldItem() != null)
				{
					ItemStack stack = player.getHeldItem();
					if(stack.getItem() instanceof BladeOfTeleportation)
					{
						NBTTagCompound comp = stack.getTagCompound();
						if(comp != null && !event.isCanceled())
						{
							if(System.currentTimeMillis() - comp.getLong("last_off_teleport") < 2500)
							{
								if(elb.getEntityId() == comp.getInteger("last_target_id"))
								{
									if(event.ammount >= elb.getHealth())
									{
										//give back most of charges... sets cooldown to 1
										BladeOfTeleportation.setCooldownForStack(stack, 1);

										//EXPERIMENTAL

//										int max_dist = BladeOfTeleportation.getMaxOffDistForStack(stack);
//
//										AxisAlignedBB box = AxisAlignedBB.getBoundingBox(player.posX - max_dist, player.posY - max_dist, player.posZ - max_dist, player.posX + max_dist, player.posY + max_dist, player.posZ + max_dist);
//										List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
//
//										Iterator<Object> iter = list.iterator();
//
//										while(iter.hasNext())
//										{
//											Object o = iter.next();
//
//											if(o instanceof EntityPlayer)
//											{
//												if(!player.canAttackPlayer((EntityPlayer)o))
//												{
//													iter.remove();
//													continue;
//												}
//											}
//											if(o instanceof EntityLivingBase)
//											{
//												EntityLivingBase elb2 = (EntityLivingBase)o;
//
//												if(player.getDistanceToEntity(elb2) > max_dist)
//												{
//													iter.remove();
//												}
//												else if(!player.canEntityBeSeen(elb2))
//												{
//													iter.remove();
//												}
//												else if(elb instanceof EntityPlayer && !(elb2 instanceof EntityPlayer))
//												{
//													iter.remove();
//												}
//												else if(elb instanceof EntityMob && !(elb2 instanceof EntityMob))
//												{
//													iter.remove();
//												}
//											}
//											else
//											{
//												iter.remove();
//											}
//										}
//
//										Object o = list.get((int)(Math.random() * list.size()));
//
//										if(o instanceof EntityLivingBase)//mostly for null-check
//										{
//											EntityLivingBase randomEntity = (EntityLivingBase)o;
//
//											Vec3 direction = Vec3.createVectorHelper(player.posX - randomEntity.posX, player.posY - randomEntity.posY, player.posZ - randomEntity.posZ);
//											direction = direction.normalize();
//
//											float yaw = 180F * (float)((direction.xCoord != 0 ? Math.atan(direction.zCoord / direction.xCoord) : 0) / Math.PI);
//											float pitch = (float)direction.yCoord * 90F;
//
//											//TODO fix
//
//											player.setLocationAndAngles(player.posX, player.posY, player.posZ, yaw, pitch);
//										}
									}
								}
							}
							else if(BladeOfTeleportation.getCooldownForStack(stack) > 0)
							{
								//50% chance for reducing cooldown by 1 second upon dealing damage
								if(Math.random() < 0.5 && event.ammount > 0)
								{
									comp.setLong("last_off_teleport", comp.getLong("last_off_teleport") - 1000);
									stack.setTagCompound(comp);
								}
							}
						}
					}
					/*if(stack.getItem() instanceof NecromancerStaff)
					{
						if(elb instanceof EntityAnimal)
						{
							if(stack.getTagCompound() == null)
								stack.setTagCompound(new NBTTagCompound());
							
							elb.setFire(1000);
							elb.attackEntityFrom(DamageSource.generic, elb.getMaxHealth());
							
							if(!player.capabilities.isCreativeMode)
								stack.getTagCompound().setInteger("charge", Math.min(stack.getTagCompound().getInteger("charge")+5, NecromancerStaff.getMaxCharges(stack)));
						}
					}*/
				}
			}
		}
	}
}
