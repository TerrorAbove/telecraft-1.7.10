package teleCraftMod.proxy;

import teleCraftMod.EntityDarkFlameFX;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

public class ClientProxy extends CommonProxy implements Handleable
{
	public void handlePreInit()
	{
		//EntityRegistry.registerGlobalEntityID(EntityDarkFlameFX.class, "darkFlameParticle", 2581);
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
