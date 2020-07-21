package com.teleCraftMod.packet;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.*;
import com.teleCraftMod.item.BladeOfTeleportation;
import com.teleCraftMod.item.EnderBucket;
import com.teleCraftMod.item.NecromancerStaff;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ZombieSpawnPacket implements IMessage
{
	private ItemStack stack;
	
	public ZombieSpawnPacket(ItemStack stack)
	{
		this.stack = stack;
	}
	
	public ZombieSpawnPacket(){}
	
	public static class Handler implements IMessageHandler<ZombieSpawnPacket, IMessage>
	{
		public IMessage onMessage(ZombieSpawnPacket message, MessageContext ctx)
		{
			try
			{
				EntityPlayerMP player = ctx.getServerHandler().playerEntity;
				World world = player.worldObj;
				ItemStack someStack = message.stack;
				
				double x = someStack.getTagCompound().getDouble("x");
				double y = someStack.getTagCompound().getDouble("y");
				double z = someStack.getTagCompound().getDouble("z");
				boolean shift = someStack.getTagCompound().getBoolean("shift");
				
				EntityLivingBase target = TeleCraft.closestELBToPoint(world, x, y, z, 3);
				
				ZombieMinion zm = new ZombieMinion(world, player, target, !shift, shift ? System.currentTimeMillis() + 120000 : -1);
				world.spawnEntityInWorld(zm);
			}
			catch(Exception ex){}
			return null;
		}
	}

	public void fromBytes(ByteBuf buf) {
		this.stack = ByteBufUtils.readItemStack(buf);
	}

	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeItemStack(buf, stack);
	}
}