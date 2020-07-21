package com.teleCraftMod.item;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.Multimap;
import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.packet.BladeOfTeleportationPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BladeOfTeleportation extends ItemSword
{
	public static final int ATTACK_COOLDOWN = 10;//cooldown for the offensive teleport in seconds
	public static final int BLOCK_COOLDOWN = 2;//cooldown for the defensive teleport in seconds
	
	private IIcon[] icons;

	public BladeOfTeleportation()
	{
		super(ToolMaterial.EMERALD);
		this.setUnlocalizedName("bladeOfTeleportation");
	}
	
	public void registerIcons(IIconRegister reg)
	{
		icons = new IIcon[11];
		
		for(int i = 0; i < icons.length; i++)
		{
			icons[i] = reg.registerIcon("telecraft:blade_of_teleportation/blade_of_teleportation_"+i);
		}
	}
	
	public IIcon getIcon(ItemStack stack, int a)
	{
		return getIconIndex(stack);
	}
	
	public IIcon getIconIndex(ItemStack stack)
	{
		return icons[getCooldownForStack(stack)];
	}

	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		int cooldown = getCooldownForStack(stack);
		
		if(cooldown < 8)
		{
			int max_dist = (8 - cooldown) * 8;
			list.add("\u00A77Teleport up to \u00A7f" + max_dist + " \u00A77blocks to your target");
			
			if(cooldown == 0)
				list.add("\u00A7fBonus damage \u00A77on your next teleport");
		}
		else
		{
			list.add("\u00A77Recharging...");
		}
	}
	
	/*@Override
	public Multimap getAttributeModifiers(ItemStack stack)
    {
		int cooldown = getCooldownForStack(stack);
		
        Multimap multimap = super.getAttributeModifiers(stack);
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(field_111210_e, "Weapon modifier", cooldown == 0 ? 10.0 : 7.0, 0));
        return multimap;
    }*/
	
	public void onCreated(ItemStack stack, World world, EntityPlayer player)
	{
		NBTTagCompound comp = new NBTTagCompound();
		
		if(stack.hasTagCompound())
			comp = stack.getTagCompound();
		
		comp.setLong("last_off_teleport", System.currentTimeMillis());
		comp.setLong("last_def_teleport", System.currentTimeMillis());
		comp.setInteger("last_target_id", -1);
		stack.setTagCompound(comp);
	}
	
	public boolean onEntitySwing(EntityLivingBase elb, ItemStack stack)
	{
		if(elb.worldObj != null && elb.worldObj.isRemote && elb == Minecraft.getMinecraft().thePlayer)
		{
			MovingObjectPosition targMop = TeleCraft.getTarget(1.0F, 64.0);
			if(targMop != null)
			{
				EntityLivingBase target = (EntityLivingBase)targMop.entityHit;
				
				Vec3 playerPos = Vec3.createVectorHelper(elb.posX, elb.posY, elb.posZ);
				Vec3 diff = playerPos.subtract(targMop.hitVec);
				
				if(diff.lengthVector() >= 6)
				{
					//this is a hacky way to make the sword show up as "on cooldown" faster
					//it is client side, so it will be overriden by correct server data later
					
					/*
					if(stack.hasTagCompound())
						stack.getTagCompound().setLong("last_off_teleport", System.currentTimeMillis());
					*/
					
					//TODO send currentTimeMillis to server and check if not too much time has elapsed there
					TeleCraft.wrapper.sendToServer(new BladeOfTeleportationPacket(target.getEntityId(), System.currentTimeMillis()));
				}
			}
		}
		return false;
	}
	
	public static boolean canTeleportAttacker(EntityPlayer p)
	{
		if(p == null || p.getHeldItem() == null || !(p.getHeldItem().getItem() instanceof BladeOfTeleportation))
			return false;
		
		long timeOfLast = 0;
		
		try
		{
			timeOfLast = p.getHeldItem().getTagCompound().getLong("last_def_teleport");
		}
		catch(Exception ex){}
		
		if((int)((System.currentTimeMillis() - timeOfLast) / 1000) >= BLOCK_COOLDOWN && p.isBlocking())
		{
			if(!p.capabilities.isCreativeMode)
			{
				if(p.getHeldItem().getTagCompound() == null)
					p.getHeldItem().setTagCompound(new NBTTagCompound());
				p.getHeldItem().getTagCompound().setLong("last_def_teleport", System.currentTimeMillis());
			}
			
			return true;
		}
		return false;
	}
	
	public static int getCooldownForStack(ItemStack stack)
	{
		if(stack != null && stack.getTagCompound() != null)
		{
			try
			{
				long timeSince = System.currentTimeMillis() - stack.getTagCompound().getLong("last_off_teleport");
				return (int) Math.min(10, Math.max(0, ATTACK_COOLDOWN - (timeSince / 1000)));
			}
			catch(Exception e) {}
		}
		return 0;
	}
	
	public static int getMaxOffDistForStack(ItemStack stack)
	{
		int cooldown = getCooldownForStack(stack);
		
		if(cooldown < 8)
		{
			return (8 - cooldown) * 8;
		}
		
		return 0;
	}
}
