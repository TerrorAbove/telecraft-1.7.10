package com.teleCraftMod.event;

import com.teleCraftMod.item.ItemCustomArmor;
import com.teleCraftMod.item.AbstractEmergencyTeleport;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PlayerTickHandler extends CustomArmorEventHandler
{
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		
		if(player != null)
		{
			ItemStack stack_chestplate = player.getCurrentArmor(CHEST);
			
	    	if(stack_chestplate != null && stack_chestplate.getItem() instanceof ItemCustomArmor && ((ItemCustomArmor)stack_chestplate.getItem()).floats() && player.isInWater())
	    	{
	    		player.motionY += 0.03999999910593033D * 0.5;//constant from EntityLivingBase
	    	}
	    	
	    	/*if(player.getHeldItem() != null)
	    	{
	    		ItemStack stack = player.getHeldItem();
	    		if(stack.getItem() instanceof ItemEmergencyTeleport)
	    		{
	    			ItemEmergencyTeleport iet = (ItemEmergencyTeleport)stack.getItem();
	    			
	    			if(iet.getDamage(stack) > 0)
	    			{
	    				stack.stackSize--;
	    			}
	    		}
	    	}*/
		}
	}
}
