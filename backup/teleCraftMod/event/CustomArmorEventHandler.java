package teleCraftMod.event;

import java.util.List;

import teleCraftMod.ItemCustomArmor;
import teleCraftMod.TeleCraft;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public abstract class CustomArmorEventHandler {
	
	protected static final int HELM = 3;
	protected static final int CHEST = 2;
	protected static final int LEGS = 1;
	protected static final int BOOTS = 0;
	
	protected static boolean isWearingEquipment(EntityPlayer player, ItemCustomArmor item, int slot)
	{
		if(player != null)
		{
			ItemStack stack = player.getCurrentArmor(slot);
			if(stack != null)
			{
				Item temp_item = stack.getItem();
				if(temp_item != null)
				{
					if(temp_item instanceof ItemCustomArmor)
					{
						ItemCustomArmor armor = (ItemCustomArmor)temp_item;
						if(Item.getIdFromItem(armor) == Item.getIdFromItem(item))
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
