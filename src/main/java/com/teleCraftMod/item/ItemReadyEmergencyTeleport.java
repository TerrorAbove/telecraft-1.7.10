package com.teleCraftMod.item;

import java.util.List;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.packet.EmergencyTeleportPacket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/*
 * 
 * This is a single use emergency teleport which takes up it's own slot and HAS A LOCATION SET.
 * It's max stack size will be 1 since each item must have its own location.
 *
 */
public class ItemReadyEmergencyTeleport extends AbstractEmergencyTeleport
{
	public ItemReadyEmergencyTeleport()
	{
		super();
		setMaxStackSize(1);
		this.bFull3D = true;
	}
	
	@SideOnly(Side.CLIENT)
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		if(p != Minecraft.getMinecraft().thePlayer)
			return stack;
		
		if(p != null && w != null && stack != null && stack.getItem() instanceof ItemReadyEmergencyTeleport)
		{
			TeleCraft.wrapper.sendToServer(new EmergencyTeleportPacket(true));//tp to location
		}

		return stack;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int a, float f0, float f1, float f2)
	{
		onItemRightClick(stack, w, p);
		return true;
	}
	
	public boolean hasEffect(ItemStack stack, int pass)
	{
		return true;
	}
	
	public int getColorFromItemStack(ItemStack stack, int a)
    {
		NBTTagCompound c = stack.getTagCompound();
		if(c != null && c.hasKey("ItemRandomColor"))
			return c.getInteger("ItemRandomColor");
		
        return super.getColorFromItemStack(stack, a);
    }
	
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean b)
	{
		try
		{
			list.add("Right-click to teleport to ("+stack.getTagCompound().getString("TeleportLocation")+")");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public String getItemStackDisplayName(ItemStack stack)
	{
		String s = super.getItemStackDisplayName(stack);
		
		if(stack != null && stack.getTagCompound() != null)
		{
			NBTTagCompound c = stack.getTagCompound();
			
			String id = c.getString("TeleportIdentifier");
			
			if(isIDValid(id))
				return format(id) + " Teleport";
		}
		return s;
	}
	
	public static void storeRandomColor(ItemStack stack)
	{
		if(stack != null && stack.getItem() instanceof ItemReadyEmergencyTeleport)
		{
			NBTTagCompound c = new NBTTagCompound();
			if(stack.getTagCompound() != null)
				c = stack.getTagCompound();
			
			int randomRed, randomGreen, randomBlue;
			
			randomRed = 127+(int)(Math.random() * 3) * 64;
			randomGreen = 127+(int)(Math.random() * 3) * 64;
			randomBlue = 127+(int)(Math.random() * 3) * 64;
			
			c.setInteger("ItemRandomColor", TeleCraft.getColorForRGB(randomRed, randomGreen, randomBlue));
		}
	}
	
	public static boolean isIDValid(String s)
	{
		if(s == null || s.length() == 0)
			return false;
		
		final String allowedSpecialChars = " ~!@#$%^&*-=+,.?/";
		final String[] bannedExpressions = new String[]{"emergency", "teleport", "unnamed"};
		
		char[] a = s.toCharArray();
		for(int i = 0; i < a.length; i++)
			if(!Character.isLetterOrDigit(a[i]) && !allowedSpecialChars.contains(""+a[i]))
				return false;
		
		for(int i = 0; i < bannedExpressions.length; i++)
			if(s.toLowerCase().contains(bannedExpressions[i]))
				return false;
		
		return true;
	}
	
	private static String format(String s)
	{
		//TODO add something else maybe?
		
		String part0 = s.substring(0, 1);
		String part1 = s.substring(1);
		
		return part0.toUpperCase() + part1;
	}
}
