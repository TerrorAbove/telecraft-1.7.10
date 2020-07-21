package com.teleCraftMod.item;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.gui.EnterTextScreen;
import com.teleCraftMod.packet.EmergencyTeleportPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemStackableEmergencyTeleport extends AbstractEmergencyTeleport
{
	public ItemStackableEmergencyTeleport()
	{
		super();
		this.bFull3D = true;
		//they are stackable when NOT set to a location
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		if(p != Minecraft.getMinecraft().thePlayer)
			return stack;
		
		if(p != null && w != null && stack != null && stack.getItem() instanceof ItemStackableEmergencyTeleport)
		{
			p.openGui(TeleCraft.instance, EnterTextScreen.GUI_ID, w, (int)p.posX, (int)p.posY + 1, (int)p.posZ);
		}
		
		return stack;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int a, float f0, float f1, float f2)
	{
		onItemRightClick(stack, w, p);
		return true;
	}
}
