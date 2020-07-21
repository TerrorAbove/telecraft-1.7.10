package com.teleCraftMod.item;

import java.util.List;

import com.teleCraftMod.TeleCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class NeptuneTrident extends Item
{
	public static final int COOLDOWN = 10;//cooldown for the earthquake in seconds
	
	public NeptuneTrident()
	{
		super();
		this.setUnlocalizedName("neptuneTrident");
		this.setTextureName("telecraft:neptune_trident");
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxStackSize(1);
	}
	
	public int getMaxDamage()
	{
		return 2000;
	}
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		try
		{
			long timeSince = System.currentTimeMillis() - stack.getTagCompound().getLong("last stopped");
			list.add("Cooldown remaining: " + Math.max(0, (getCooldownForQuake(stack) - timeSince) / 1000));
		}
		catch(Exception e)
		{
			list.add("Cooldown remaining: 0");
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int count)
	{
		if(stack != null && player != null && !world.isRemote)
		{
			//CHARGE_RATIO is the ratio representing how much left there is to go
			//i.e. 0.0 means fully charged
			final double CHARGE_RATIO = (double)(count) / getMaxItemUseDuration(stack);
			final double CHARGED = 1.0 - CHARGE_RATIO;
			
			int numHit = 0;
			
			List list = null;
			
			if(player.isAirBorne || stack.getItemDamage() == stack.getMaxDamage())
			{
				return;
			}
			
			if(player.isInsideOfMaterial(Material.water))//increased radius if in water
				list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX-(50 * CHARGED), player.posY, player.posZ-(50 * CHARGED), player.posX+(50 * CHARGED), player.posY+(50 * CHARGED), player.posZ+(50 * CHARGED)));
			else
				list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(player.posX-(10 * CHARGED), player.posY-(10 * CHARGED), player.posZ-(10 * CHARGED), player.posX+(10 * CHARGED), player.posY+(10 * CHARGED), player.posZ+(10 * CHARGED)));
			
			//this increases the minimum magnitude while keeping max the same
			//relative to how long this earthquake was charged
			int prob_booster = (int)(CHARGED * 4);
			double magnitude = 4.0 + prob_booster + Math.random() * (8 - prob_booster);
			
			double mag_sq = Math.pow(magnitude, 2);
			
			double charged_multiplier = 2.0 - CHARGE_RATIO;
			
			for(Object o : list)
			{
				if(o instanceof EntityPlayer)
				{
					EntityPlayer otherPlayer = (EntityPlayer)o;
					
					if(player != otherPlayer && player.canAttackPlayer(otherPlayer) && (!otherPlayer.isAirBorne || otherPlayer.isInsideOfMaterial(Material.water)))
					{
						double dist = Math.sqrt(Math.pow(player.posX - otherPlayer.posX, 2) + Math.pow(player.posY - otherPlayer.posY, 2) + Math.pow(player.posZ - otherPlayer.posZ, 2));
						double reductionAmount = dist + numHit + 1;
						
						final float END_DAMAGE = (float)(charged_multiplier * mag_sq / reductionAmount);
						
						otherPlayer.attackEntityFrom(DamageSource.generic, END_DAMAGE);
						otherPlayer.motionY += 0.5;
						numHit++;
					}
				}
				else if(o instanceof EntityLivingBase)
				{
					EntityLivingBase elb = (EntityLivingBase)o;
					if(!elb.isAirBorne || elb.isInsideOfMaterial(Material.water))
					{
						double dist = Math.sqrt(Math.pow(player.posX - elb.posX, 2) + Math.pow(player.posY - elb.posY, 2) + Math.pow(player.posZ - elb.posZ, 2));
						double reductionAmount = dist + numHit + 1;
						
						final float END_DAMAGE = (float)(charged_multiplier * mag_sq / reductionAmount);
						
						elb.attackEntityFrom(DamageSource.generic, END_DAMAGE);
						elb.motionY += 0.5;
						numHit++;
					}
				}
			}
			
			if(!player.capabilities.isCreativeMode)
				stack.damageItem((int)(((magnitude / 12) * numHit) + 0.5), player);
			
			player.addChatMessage(new ChatComponentText("Your trident releases a " + TeleCraft.roundToSignificantFigures(CHARGED * 100, 3) + "% charged earthquake of magnitude " + TeleCraft.roundToSignificantFigures(magnitude, 3) + "!"));
			stack.getTagCompound().setLong("last stopped", System.currentTimeMillis());
			stack.getTagCompound().setDouble("last charged", CHARGED);
		}
	}
	
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player)
	{
		player.stopUsingItem();
		return stack;
	}
	
	public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
	{
		if(count % 5 == 0)//for shake effect (4 stages)
			player.cameraYaw += count % 2 == 0 ? (getMaxItemUseDuration(stack) - count) / 25 : -((getMaxItemUseDuration(stack) - count) / 25);
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(stack.getTagCompound() == null)
		{
			NBTTagCompound comp = new NBTTagCompound();
			comp.setLong("last stopped", 0);
			stack.setTagCompound(comp);
		}
		if(System.currentTimeMillis() - stack.getTagCompound().getLong("last stopped") > getCooldownForQuake(stack))
		{
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
		}
		return stack;
	}
	
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 100;
	}
	
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.bow;
	}
	
	public boolean isFull3D()
	{
		return true;
	}
	
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if(entity != null && entity instanceof EntityLivingBase)
		{
			EntityLivingBase elb = (EntityLivingBase)entity;
			if(elb.getHealth() == elb.getMaxHealth())
				elb.attackEntityFrom(DamageSource.generic, 1);
			else
				elb.attackEntityFrom(DamageSource.drown, (elb.getMaxHealth() - elb.getHealth()));
			//player.worldObj.playSoundAtEntity(player, "liquid.splash", 1, 1);
		}
		return true;
	}
	
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		boolean success = false;
		Block block = player.worldObj.getBlock(x, y, z);
		if(block.getMaterial() == Material.ground || block.getMaterial() == Material.grass || block.getMaterial() == Material.sand)
		{
			player.worldObj.setBlock(x, y, z, Blocks.flowing_water);
			if(!player.capabilities.isCreativeMode)
				stack.damageItem(10, player);
			success = true;
		}
		return success || super.onBlockStartBreak(stack, x, y, z, player);
	}
	
	private static long getCooldownForQuake(ItemStack stack)
	{
		NBTTagCompound tag = stack.getTagCompound();
		if(tag != null)
		{
			return Math.round(tag.getDouble("last charged") * (COOLDOWN - 5) + 5) * 1000L;
		}
		return COOLDOWN * 1000L;
	}
}
