package com.teleCraftMod.item;

import java.awt.Color;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.gui.PadlockCreateGui;
import com.teleCraftMod.packet.PadlockRightClickedPacket;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class ItemPadlock extends Item
{
	private IIcon locked;
	private IIcon unlocked;
	private IIcon unlocked_with_key;
	
	public ItemPadlock()
	{
		super();
		
		setMaxStackSize(1);
	}
	
	public void registerIcons(IIconRegister reg)
	{
		locked = reg.registerIcon("telecraft:padlock");
		unlocked = reg.registerIcon("telecraft:padlock_unlocked");
		unlocked_with_key = reg.registerIcon("telecraft:padlock_unlocked_with_key");
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World w, EntityPlayer p)
	{
		if(p != null && stack != null)
		{
			NBTTagCompound c = stack.getTagCompound();
			
			if(c == null)
				c = new NBTTagCompound();
			
			if(System.currentTimeMillis() - c.getLong("last_time_used") < 2500)
				return stack;
			
			if(c.getBoolean("locked"))//assumes pin data is set
			{
				NBTBase base = c.getTag("pin_data");
				if(base instanceof NBTTagIntArray)
				{
					int[] arr = ((NBTTagIntArray)base).func_150302_c();
					
					for(int i = 0; i < p.inventory.getSizeInventory(); i++)
					{
						ItemStack curr = p.inventory.getStackInSlot(i);
						
						if(curr != null && curr.getItem() == TeleCraft.padlock_key && curr.getTagCompound() != null)
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
									c.setBoolean("locked", false);
									break;
								}
							}
						}
					}
				}
			}
			else
			{
				if(c.getBoolean("pins_set"))
				{
					c.setBoolean("locked", true);
				}
				else
				{
					p.openGui(TeleCraft.instance, PadlockCreateGui.GUI_ID, p.worldObj, (int)p.posX, (int)p.posY, (int)p.posZ);
				}
			}
			
			c.setLong("last_time_used", System.currentTimeMillis());
			stack.setTagCompound(c);
		}
		return stack;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer p, World w, int x, int y, int z, int o, float f1, float f2, float f3)
	{
		onItemRightClick(stack, w, p);
		return false;
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
	public void addInformation(ItemStack stack, EntityPlayer p, List list, boolean b)
	{
		try
		{
			ItemStack matchingKey = null;
			
			NBTBase base = stack.getTagCompound().getTag("pin_data");
			if(base instanceof NBTTagIntArray)
			{
				int[] arr = ((NBTTagIntArray)base).func_150302_c();

				for(int i = 0; i < p.inventory.getSizeInventory(); i++)
				{
					ItemStack curr = p.inventory.getStackInSlot(i);

					if(curr != null && curr.getItem() == TeleCraft.padlock_key && curr.getTagCompound() != null)
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
								matchingKey = curr;
								break;
							}
						}
					}
				}
			}
			
			if(matchingKey != null)
			{
				list.add("\u00A7aMatching key found.");
				NBTTagCompound c = matchingKey.getTagCompound();
				if(c != null)
				{
					c.setLong("hover_highlight_time", System.currentTimeMillis());
					matchingKey.setTagCompound(c);
				}
			}
			else
			{
				list.add("\u00A74No matching key found.");
			}
		}
		catch(Exception ex) {}
	}
	
	public int getColorFromItemStack(ItemStack stack, int b)
	{
		if(stack.hasTagCompound())
		{
			final NBTTagCompound c = stack.getTagCompound();
			
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
	
	public IIcon getIcon(ItemStack stack, int a)
	{
		return getIconIndex(stack);
	}
	
	public IIcon getIconIndex(ItemStack stack)
	{
		if(stack != null)
		{
			if(stack.getTagCompound() != null)
			{
				if(stack.getTagCompound().getBoolean("locked"))
				{
					return locked;
				}
				
				if(stack.getTagCompound().getBoolean("pins_set"))
				{
					return unlocked;
				}
			}
		}
		return unlocked_with_key;
	}
	
	public boolean getShareTag()
	{
		return true;
	}
	
	public String getItemStackDisplayName(ItemStack stack)
	{
		String s = super.getItemStackDisplayName(stack);
		
		if(getIconIndex(stack) == unlocked_with_key)
		{
			s += " and key";
		}
		else if(getIconIndex(stack) == unlocked)
		{
			s += " (unlocked)";
		}
		return s;
	}
}
