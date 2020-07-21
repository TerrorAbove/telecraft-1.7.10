package com.teleCraftMod.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

public class TileEntityRiggedChest extends TileEntityChest
{
	public static final int DEFAULT_META = 0;
	
	private String ownerName;
	private byte originalMeta;
	
	public TileEntityRiggedChest()
	{
		super();
		ownerName = null;
		originalMeta = DEFAULT_META;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound comp)
	{
		super.readFromNBT(comp);
		
		if(comp.hasKey("Owner"))
		{
			ownerName = comp.getString("Owner");
		}
		
		if(comp.hasKey("OriginalMeta"))
			originalMeta = comp.getByte("OriginalMeta");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound comp)
	{
		super.writeToNBT(comp);
		
		if(ownerName != null)
		{
			comp.setString("Owner", ownerName);
		}
		
		if(originalMeta >= 2 && originalMeta <= 5)
			comp.setByte("OriginalMeta", originalMeta);
	}
	
	public void setOwner(String owner)
	{
		this.ownerName = owner;
	}
	
	public String getOwner()
	{
		return this.ownerName;
	}
	
	public void setOriginalMeta(byte oMeta)
	{
		this.originalMeta = oMeta;
	}
	
	public int getOriginalMeta()
	{
		return this.originalMeta;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer p)
    {
		if(p != null && p.getCommandSenderName().equalsIgnoreCase(ownerName))
			return super.isUseableByPlayer(p);
		
		return false;
	}
}
