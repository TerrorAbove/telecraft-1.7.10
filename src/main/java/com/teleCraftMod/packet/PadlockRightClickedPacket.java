package com.teleCraftMod.packet;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.gui.PadlockCreateGui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.world.World;

public class PadlockRightClickedPacket implements IMessage
{
	public static class Handler implements IMessageHandler<PadlockRightClickedPacket, IMessage>
	{
		@Override
		public IMessage onMessage(PadlockRightClickedPacket message, MessageContext ctx)
		{
			EntityPlayer p = ctx.getServerHandler().playerEntity;
			World w = p.worldObj;
			
			return null;
		}
	}
	
	public PadlockRightClickedPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
}
