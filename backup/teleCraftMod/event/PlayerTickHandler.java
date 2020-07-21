package teleCraftMod.event;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import teleCraftMod.BladeOfTeleportation;
import teleCraftMod.ItemCustomArmor;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class PlayerTickHandler extends CustomArmorEventHandler
{
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer p = event.player;
		ItemStack stack_chestplate = p.getCurrentArmor(CHEST);
		
    	if(stack_chestplate != null && stack_chestplate.getItem() instanceof ItemCustomArmor && ((ItemCustomArmor)stack_chestplate.getItem()).floats())
    	{
    		if(p.isInsideOfMaterial(Material.water) && p.motionY < 0)
    		{
    			p.motionY += 0.25 + Math.random() / 5;
    		}
    	}
	}
}
