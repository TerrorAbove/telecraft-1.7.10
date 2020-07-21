package com.teleCraftMod.util;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.ItemSuperBucket;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CraftUtil
{
	public static ItemStack handleSpecialCrafting(InventoryCrafting c, ItemStack result)
	{
		if(result != null)
		{
			if(result.getItem() == TeleCraft.padlock_key)
			{
				for(int i = 0; i < c.getSizeInventory(); i++)
				{
					ItemStack temp = c.getStackInSlot(i);
					
					if(temp != null && temp.getItem() == TeleCraft.padlock_key)
					{
						if(temp.hasTagCompound())
						{
							result = temp.copy();
							break;
						}
					}
				}
			}
			else if(result.getItem() == TeleCraft.completeGrapple)
			{
				ItemStack found0 = null;
				ItemStack found1 = null;
				
				for(int i = 0; i < c.getSizeInventory(); i++)
				{
					ItemStack temp = c.getStackInSlot(i);
					
					if(temp != null && temp.getItem() == TeleCraft.completeGrapple)
					{
						if(found0 == null)
							found0 = temp;
						else
							found1 = temp;
					}
				}
				
				if(found0 != null && found1 != null)//combining charges of two grapples
				{
					int d0 = TeleCraft.completeGrapple.getMaxDamage() - found0.getItemDamage();
					int d1 = TeleCraft.completeGrapple.getMaxDamage() - found1.getItemDamage();
					
					int n = TeleCraft.completeGrapple.getMaxDamage() - (d0 + d1);
					
					result.setItemDamage(n);//TODO test, maybe disallow combining in some situations
				}
			}
			else if(result.getItem() == TeleCraft.bladeOfTeleportation)
			{
				boolean isDiamond = false;//assumes ender pearl
				
				ItemStack oldSword = null;
				
				for(int i = 0; i < c.getSizeInventory(); i++)
				{
					ItemStack temp = c.getStackInSlot(i);
					
					if(temp != null)
					{
						if(temp.getItem() == Items.diamond)
							isDiamond = true;
						else if(temp.getItem() == TeleCraft.bladeOfTeleportation)
							oldSword = temp;
					}
				}
				
				if(oldSword != null)
				{
					result.setTagCompound(oldSword.getTagCompound());
					
					if(isDiamond)
					{
						result.setItemDamage(0);
					}
					else
					{
						int toRestore = oldSword.getMaxDamage() / 4;
						result.setItemDamage(oldSword.getItemDamage() - toRestore);
					}
				}
			}
		}
		
		return result;
	}
}
