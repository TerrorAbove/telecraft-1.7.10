package com.teleCraftMod.proxy;

import net.minecraft.entity.player.EntityPlayer;

public interface Handleable {
	public void handlePreInit();
	public void handleInit();
	public void handlePostInit();
	public void handleLoad();
	public int addArmor(String name);
}
