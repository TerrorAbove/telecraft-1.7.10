package teleCraftMod;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class BlockChestRigged extends BlockChest
{
	private static HashMap<String, ArrayList<Location>> ownedChests;
	
	public BlockChestRigged()
	{
		super(0);
		setHardness(2.5F);
		setStepSound(soundTypeWood);
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
	{
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer p = (EntityPlayer)entity;
			
			String playerName = p.getCommandSenderName();
			
			ArrayList<Location> locs = new ArrayList<Location>();
			
			if(ownedChests == null)
			{
				ownedChests = new HashMap<String, ArrayList<Location>>();
			}
			else if(ownedChests.containsKey(playerName))
			{
				locs = ownedChests.get(playerName);
				if(locs == null)
				{
					locs = new ArrayList<Location>();
				}
			}
			
			Location placedChestLocation = new Location(x, y, z, world);
			
			for(String s : ownedChests.keySet())
			{
				if(ownedChests.get(s).contains(placedChestLocation) && !s.equals(playerName))
				{
					if(p instanceof EntityPlayerMP)
                	{
                		EntityPlayerMP player = (EntityPlayerMP)p;
                		player.addChatMessage(new ChatComponentText("You cannot place a chest here... Try somewhere else."));
                		player.addChatMessage(new ChatComponentText("(another player's chest was destroyed incorrectly)"));
                	}
					return;
				}
			}
			
			locs.add(placedChestLocation);
			ownedChests.put(playerName, locs);
		}
		super.onBlockPlacedBy(world, x, y, z, entity, stack);
	}
	
	public boolean onBlockActivated(World world, int int_2, int int_3, int int_4, EntityPlayer player, int int_6, float ft_7, float ft_8, float ft_9)
	{
		final String playerName = player.getCommandSenderName();
		
		if(ownedChests == null || !ownedChests.containsKey(playerName) || !ownedChests.get(playerName).contains(new Location(int_2, int_3, int_4, world)))
		{	
			if(!player.capabilities.isCreativeMode)
			{
				player.setHealth(Math.max(1, (int)(player.getHealth() / 2)));
				player.setFire(1);
			}
			
			if(player instanceof EntityPlayerMP)
        	{
        		EntityPlayerMP playerMP = (EntityPlayerMP)player;
        		playerMP.addChatMessage(new ChatComponentText("A powerful magic prevents you from opening this chest!"));
        	}
			
			return true;
		}
		
		super.onBlockActivated(world, int_2, int_3, int_4, player, int_6, ft_7, ft_8, ft_9);
		
		return true;
	}
	
	public void breakBlock(World world, int x, int y, int z, Block block, int other)
    {
		Location loc = new Location(x, y, z, world);
		EntityPlayer owner = null;
		
		for(String s : ownedChests.keySet())
		{
			if(ownedChests.get(s).contains(loc))
			{
				owner = world.getPlayerEntityByName(s);
				break;
			}
		}
		
		if(owner != null && owner.worldObj.getWorldInfo().getWorldName().equals(world.getWorldInfo().getWorldName()) && owner.getDistance(x, y, z) < 6)
		{
			super.breakBlock(world, x, y, z, block, other);
			ownedChests.get(owner.getCommandSenderName()).remove(loc);
		}
    }
	
	private static class Location
	{
		private int x, y, z;
		private World w;
		
		private Location(int x, int y, int z, World w)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = w;
		}
		
		@Override
		public boolean equals(Object other)
		{
			Location loc = (Location)other;
			return loc.w.getWorldInfo().getWorldName().equals(this.w.getWorldInfo().getWorldName()) && loc.x == this.x && loc.y == this.y && loc.z == this.z;
		}
	}
}
