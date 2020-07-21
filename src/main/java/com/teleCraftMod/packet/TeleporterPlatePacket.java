package com.teleCraftMod.packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.WorldAccessContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class TeleporterPlatePacket implements IMessage
{
	//private int teleportDistance;
	
	/*public TeleporterPlatePacket(int teleportDistance)
	{
		this.teleportDistance = teleportDistance;
	}*/
	
	public TeleporterPlatePacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
	
	public static class Handler implements IMessageHandler<TeleporterPlatePacket, IMessage>
	{
		@Override
		public IMessage onMessage(TeleporterPlatePacket packet, MessageContext ctx)
		{
			if(ctx.side == Side.SERVER)
			{
				EntityPlayer p = ctx.getServerHandler().playerEntity;
				
				if(p != null)
				{
					World world = p.worldObj;
					
					if(world != null)
					{
						
					}
				}
			}
			
			return null;
		}
	}
}
