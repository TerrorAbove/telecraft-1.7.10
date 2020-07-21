package com.teleCraftMod.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		//if(id == EnterTextScreen.GUI_ID)
    		//return some new container...
		
		return null;
	}

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
    	if(id == EnterTextScreen.GUI_ID)
    		return new EnterTextScreen();
    	
    	if(id == PadlockCreateGui.GUI_ID)
    		return new PadlockCreateGui();
    	
    	return null;
    }
}
