package com.teleCraftMod.proxy;

import java.util.HashMap;

import com.teleCraftMod.entity.*;
import com.teleCraftMod.rendering.RenderGrapple;
import com.teleCraftMod.rendering.RenderZombieMinion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ClientProxy extends CommonProxy implements Handleable
{
	public void handlePreInit()
	{
		RenderingRegistry.registerEntityRenderingHandler(ZombieMinion.class, new RenderZombieMinion(new ModelZombie(), 0.5f));
		RenderingRegistry.registerEntityRenderingHandler(EntityGrappleHook.class, new RenderGrapple());
	}
	
	public void handleInit()
	{
		
	}
	
	public void handlePostInit()
	{
		
	}
	
	public void handleLoad()
	{
		
	}
	
	public int addArmor(String name)
	{
		return RenderingRegistry.addNewArmourRendererPrefix(name);
	}
}
