package com.teleCraftMod.item;

import com.teleCraftMod.packet.EmergencyTeleportPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class AbstractEmergencyTeleport extends Item
{
	@SideOnly(Side.CLIENT)
	public abstract ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p);
	
	@SideOnly(Side.CLIENT)
	public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int a, float f0, float f1, float f2)
	{
		onItemRightClick(stack, w, p);
		return true;
	}
}
