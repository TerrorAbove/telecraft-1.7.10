package com.teleCraftMod.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityTelePlate extends TileEntity
{
	private int teleDistance;
	
	public TileEntityTelePlate() {}
	
	public TileEntityTelePlate(int teleDist)
	{
		super();
		teleDistance = teleDist;
	}
	
	public int getTeleDistance()
	{
		return teleDistance;
	}
	
	public void setTeleDistance(int dist)
	{
		teleDistance = dist;
	}
	
	@Override
	public Packet getDescriptionPacket()//server side
	{
	    NBTTagCompound tag = new NBTTagCompound();
	    writeToNBT(tag);
	    
	    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)//received client side
	{
	    readFromNBT(pkt.func_148857_g());
	}
	
	@Override
	public void writeToNBT(NBTTagCompound c)
	{
		super.writeToNBT(c);
		
		c.setInteger("teleDistance", teleDistance);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound c)
	{
		super.readFromNBT(c);
		
		teleDistance = c.getInteger("teleDistance");
	}
	
	public void markChunkModified()
	{
		if(worldObj != null)
		{
			worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
		}
	}
}
