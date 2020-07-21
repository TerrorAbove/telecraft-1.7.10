package teleCraftMod.event;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import teleCraftMod.TeleCraft;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerHurtHandler extends CustomArmorEventHandler
{
	private static final double FORTITUDE_FACTOR = 0.85; //damage reduced by 15% per mob
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void onLivingHurtEvent(LivingHurtEvent event)
	{
		if(event.entityLiving instanceof EntityPlayer && event.source.getSourceOfDamage() instanceof EntityMob)
		{
			EntityPlayer player = (EntityPlayer)event.entityLiving;
			
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
		}
	}
}
