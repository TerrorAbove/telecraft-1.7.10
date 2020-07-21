package com.teleCraftMod.packet;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.ItemPadlock;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

public class SetPinsPacket implements IMessage
{
	public static class Handler implements IMessageHandler<SetPinsPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SetPinsPacket message, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayer p = ctx.getServerHandler().playerEntity;
				if(p != null && message.data != null && message.data.length() > 0)
				{
					try
					{
						String[] spl = message.data.split(",");
						int[] d = new int[spl.length];
						for(int i = 0; i < d.length; i++)
							d[i] = Integer.parseInt(spl[i]);

						ItemStack held = p.getHeldItem();
						if(held != null && held.getItem() instanceof ItemPadlock)
						{
							NBTTagCompound c = held.getTagCompound();
							if(c == null)
								c = new NBTTagCompound();
							
							if(!c.getBoolean("pins_set"))
							{
								ItemStack key = new ItemStack(TeleCraft.padlock_key);
								
								NBTTagCompound c2 = key.getTagCompound();
								if(c2 == null)
									c2 = new NBTTagCompound();
								
								c2.setTag("pin_data", new NBTTagIntArray(d));
								key.setTagCompound(c2);
								
								if(p.inventory.addItemStackToInventory(key))
								{
									c.setTag("pin_data", new NBTTagIntArray(d));
									c.setBoolean("pins_set", true);
									c.setBoolean("locked", true);
									
									held.setTagCompound(c);
								}
							}
						}
						//TODO other cases?
					}
					catch(NumberFormatException nfe) {}
				}
			}
			return null;
		}
	}
	
	private String data;
	
	public SetPinsPacket() {}
	
	public SetPinsPacket(String data)
	{
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		data = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, data);
	}

}
