package com.teleCraftMod.rendering;

import org.lwjgl.opengl.GL11;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.entity.*;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderZombieMinion extends RenderLiving
{
    protected ResourceLocation serpentTexture;

    public RenderZombieMinion(ModelBase par1ModelBase, float parShadowSize)
    {
        super(par1ModelBase, parShadowSize);
        setEntityTexture();        
    }
 
    protected void preRenderCallback(EntityLivingBase elb, float f)
    {
    	if(elb instanceof ZombieMinion)
    	{
    		ZombieMinion zm = (ZombieMinion)elb;
    		float scale = zm.getScaleFactor();
    		if(scale < 0.875)
    			GL11.glScalef(0.75F, 0.75F, 0.75F);
    		else if(scale < 1.125)
    			GL11.glScalef(1.0F, 1.0F, 1.0F);
    		else
    			GL11.glScalef(1.25F, 1.25F, 1.25F);
    	}
    }

    protected void setEntityTexture()
    {
        serpentTexture = new ResourceLocation(TeleCraft.MODID+":textures/entity/zombie_minion/zombie.png");
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return serpentTexture;
    }
}