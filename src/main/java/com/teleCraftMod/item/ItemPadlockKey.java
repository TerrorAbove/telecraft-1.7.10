package com.teleCraftMod.item;

import java.awt.Color;
import java.util.List;

import com.teleCraftMod.TeleCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

public class ItemPadlockKey extends Item
{
	public ItemPadlockKey()
	{
		super();
		
		setTextureName("telecraft:fancy_key");
		setMaxStackSize(1);
	}
	
	public int getColorFromItemStack(ItemStack stack, int b)
	{
		if(stack.hasTagCompound())
		{
			NBTTagCompound c = stack.getTagCompound();
			
			if(c.hasKey("hover_highlight_time"))
			{
				int color = Color.GREEN.getRGB();
				
				if(System.currentTimeMillis() - c.getLong("hover_highlight_time") >= 100)
					c.removeTag("hover_highlight_time");
				
				return color;
			}
		}
		return super.getColorFromItemStack(stack, b);
	}
	
	public void addInformation(ItemStack stack, EntityPlayer p, List list, boolean b)
	{
		try
		{
			if(!stack.hasTagCompound() || !stack.getTagCompound().hasKey("pin_data"))
				list.add("Blank key");
			else
			{
				ItemStack matchingLock = null;
				
				NBTBase base = stack.getTagCompound().getTag("pin_data");
				if(base instanceof NBTTagIntArray)
				{
					int[] arr = ((NBTTagIntArray)base).func_150302_c();

					for(int i = 0; i < p.inventory.getSizeInventory(); i++)
					{
						ItemStack curr = p.inventory.getStackInSlot(i);

						if(curr != null && curr.getItem() == TeleCraft.padlock && curr.getTagCompound() != null)
						{
							NBTBase base2 = curr.getTagCompound().getTag("pin_data");
							if(base2 instanceof NBTTagIntArray)
							{
								int[] arr2 = ((NBTTagIntArray)base2).func_150302_c();

								boolean equal = arr.length == arr2.length;

								if(equal)//same number of pins
								{
									for(int j = 0; j < arr.length; j++)
									{
										if(arr[j] != arr2[j])
										{
											equal = false;
											break;
										}
									}
								}

								if(equal)//found a matching key
								{
									matchingLock = curr;
									break;
								}
							}
						}
					}
				}
				
				if(matchingLock != null)
				{
					list.add("\u00A7aMatching lock found.");
					NBTTagCompound c = matchingLock.getTagCompound();
					if(c != null)
					{
						c.setLong("hover_highlight_time", System.currentTimeMillis());
						matchingLock.setTagCompound(c);
					}
				}
				else
				{
					list.add("\u00A74No matching lock found.");
				}
			}
		}
		catch(Exception ex) {}
	}
}
