package com.teleCraftMod.packet;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.ItemCompleteGrapple;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GrappleResponsePacket implements IMessage
{
	private int entityID;
	
	public static class Handler implements IMessageHandler<GrappleResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(GrappleResponsePacket message, MessageContext ctx)
		{
			ItemCompleteGrapple grapple = (ItemCompleteGrapple)TeleCraft.completeGrapple;

			return null;
		}
	}
	
	public GrappleResponsePacket() {}
	
	public GrappleResponsePacket(int entityID)
	{
		this.entityID = entityID;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		entityID = buf.readInt();
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityID);
	}
}
