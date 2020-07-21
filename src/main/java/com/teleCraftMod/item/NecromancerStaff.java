package com.teleCraftMod.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.teleCraftMod.packet.ZombieSpawnPacket;
import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class NecromancerStaff extends Item
{
	public NecromancerStaff()
	{
		super();
		this.setUnlocalizedName("necromancerStaff");
		this.setTextureName("telecraft:necromancer_staff");
		this.setCreativeTab(CreativeTabs.tabCombat);
		this.setMaxStackSize(1);
	}
	
	public int getMaxDamage()
	{
		return 2000;
	}
	
	/*		
	 		\u00A70   = Black
			\u00A71   = Dark Blue
			\u00A72   = Dark Green
			\u00A73   = Dark Cyan
			\u00A74   = Dark Red
			\u00A75   = Purple
			\u00A76   = Orange
			\u00A77   = Light Grey
			\u00A78   = Dark Grey
			\u00A7a   = Light Green
			\u00A7b   = Light Cyan
			\u00A7c   = Light Red
			\u00A7d   = Pink
			\u00A7e   = Yellow
			\u00A7f   = White
	*/
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		String text = "\u00A72" + "Zombie minions stored: ";
		try
		{
			if(player.capabilities.isCreativeMode)
				text += "Infinite";
			else
				text += stack.getTagCompound().getInteger("charge") + "/" + getMaxCharges(stack);
		}
		catch(Exception e)
		{
			text += "0/" + getMaxCharges(stack);
		}
		list.add(text);
	}
	
	public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_)
    {
        return TeleCraft.getColorForRGB(128, 32, 32);
    }
	
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		boolean shift = GuiScreen.isShiftKeyDown();
		
		MovingObjectPosition targMop = TeleCraft.getTarget(1.0F, 128.0);
		
		EntityLivingBase target = targMop != null ? (EntityLivingBase)targMop.entityHit : null;
		
		if((shift || (target instanceof EntityLiving && !(target instanceof ZombieMinion))) && (canSummonWith(stack) || player.capabilities.isCreativeMode))
		{
			if(stack.getTagCompound() == null)
				stack.setTagCompound(new NBTTagCompound());
			
			if(player != Minecraft.getMinecraft().thePlayer)
			{
				if(!player.capabilities.isCreativeMode)
				{
					stack.getTagCompound().setInteger("charge", Math.max(0, stack.getTagCompound().getInteger("charge")-1));
					stack.damageItem(5, player);
				}
				return stack;
			}
			
			try
			{
				stack.getTagCompound().setDouble("x", target.posX);
				stack.getTagCompound().setDouble("y", target.posY);
				stack.getTagCompound().setDouble("z", target.posZ);
			}
			catch(Exception ex)
			{
				stack.getTagCompound().setDouble("x", Integer.MIN_VALUE / 2);
				stack.getTagCompound().setDouble("y", Integer.MIN_VALUE / 2);
				stack.getTagCompound().setDouble("z", Integer.MIN_VALUE / 2);
			}
			
			stack.getTagCompound().setBoolean("shift", shift);
			
			stack.getTagCompound().setLong("last used", System.currentTimeMillis());
			
			TeleCraft.wrapper.sendToServer(new ZombieSpawnPacket(stack));
		}
        return stack;
	}
	
	private boolean canSummonWith(ItemStack stack)
	{
		return stack != null && stack.getTagCompound() != null && stack.getTagCompound().getInteger("charge") > 0 && stack.getTagCompound().getLong("last used") + 2000 <= System.currentTimeMillis();
	}

	public boolean isFull3D()
	{
		return true;
	}
	
	public static int getMaxCharges(ItemStack stack)
	{
		return (int)(100.0 * (1.0 - ((double)(stack.getItemDamage()) / stack.getMaxDamage())));
	}
}
