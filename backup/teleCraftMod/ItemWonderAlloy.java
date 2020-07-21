package teleCraftMod;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemWonderAlloy extends Item
{
	private boolean isForged;
	
	public ItemWonderAlloy(String textureName, boolean forged)
	{
		super();
		this.setTextureName(textureName);
		this.isForged = forged;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		if(!forged)
			this.setUnlocalizedName("unforgedWonderAlloy");
		else
			this.setUnlocalizedName("wonderIngot");
	}
	
	public boolean isForged()
	{
		return isForged;
	}
}
