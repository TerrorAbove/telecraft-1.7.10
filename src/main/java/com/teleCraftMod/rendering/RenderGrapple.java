package com.teleCraftMod.rendering;

import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.teleCraftMod.entity.EntityGrappleHook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderGrapple extends Render
{
	private static final ResourceLocation field_110792_a = new ResourceLocation("telecraft:textures/items/grapple_hook.png");
	
    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
	public void doRender(EntityGrappleHook hook, double x, double y, double z, float f1, float f2)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		this.bindEntityTexture(hook);
		Tessellator var10 = Tessellator.instance;
		float var13 = 0F;
		float var14 = 1F;
		float var15 = 0F;
		float var16 = 1F;
		float var17 = 1.0F;
		float var18 = 0.5F;
		float var19 = 0.5F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		var10.startDrawingQuads();
		var10.setNormal(0.0F, 1.0F, 0.0F);
		var10.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0D, (double)var13, (double)var16);
		var10.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0D, (double)var14, (double)var16);
		var10.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0D, (double)var14, (double)var15);
		var10.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0D, (double)var13, (double)var15);
		var10.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();


		if (hook.player != null)
		{
			float var20 = hook.player.getSwingProgress(f2);
			float var21 = MathHelper.sin(MathHelper.sqrt_float(var20) * (float)Math.PI);
			Vec3 var22 = Vec3.createVectorHelper(-0.4D, 0D, 0D);//TODO adjust precise start location from grapple launcher
			var22.rotateAroundX(-(hook.player.prevRotationPitch + (hook.player.rotationPitch - hook.player.prevRotationPitch) * f2) * (float)Math.PI / 180.0F);
			var22.rotateAroundY(-(hook.player.prevRotationYaw + (hook.player.rotationYaw - hook.player.prevRotationYaw) * f2) * (float)Math.PI / 180.0F);
			var22.rotateAroundY(var21 * 0.5F);
			var22.rotateAroundX(-var21 * 0.7F);
			double var23 = hook.player.prevPosX + (hook.player.posX - hook.player.prevPosX) * (double)f2 + var22.xCoord;
			double var25 = hook.player.prevPosY + (hook.player.posY - hook.player.prevPosY) * (double)f2 + var22.yCoord;
			double var27 = hook.player.prevPosZ + (hook.player.posZ - hook.player.prevPosZ) * (double)f2 + var22.zCoord;
			double var29 = hook.player == Minecraft.getMinecraft().thePlayer ? 0.0D : (double)hook.player.getEyeHeight();

			if (this.renderManager.options.thirdPersonView > 0 || hook.player != Minecraft.getMinecraft().thePlayer)
			{
				float var31 = (hook.player.prevRenderYawOffset + (hook.player.renderYawOffset - hook.player.prevRenderYawOffset) * f2) * (float)Math.PI / 180.0F;
				double var32 = (double)MathHelper.sin(var31);
				double var34 = (double)MathHelper.cos(var31);
				var23 = hook.player.prevPosX + (hook.player.posX - hook.player.prevPosX) * (double)f2 - var34 * 0.35D - var32 * 0.85D;
				var25 = hook.player.prevPosY + var29 + (hook.player.posY - hook.player.prevPosY) * (double)f2 - 0.45D;
				var27 = hook.player.prevPosZ + (hook.player.posZ - hook.player.prevPosZ) * (double)f2 - var32 * 0.35D + var34 * 0.85D;
			}

			double var46 = hook.prevPosX + (hook.posX - hook.prevPosX) * (double)f2;
			double var33 = hook.prevPosY + (hook.posY - hook.prevPosY) * (double)f2 + 0.25D;
			double var35 = hook.prevPosZ + (hook.posZ - hook.prevPosZ) * (double)f2;
			double var37 = (double)((float)(var23 - var46));
			double var39 = (double)((float)(var25 - var33));
			double var41 = (double)((float)(var27 - var35));
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			var10.startDrawing(3);
			var10.setColorOpaque_I(0);
			byte var43 = 16;

			for (int var44 = 0; var44 <= var43; ++var44)
			{
				float var45 = (float)var44 / (float)var43;
				double yVert = hook.isLaunchComplete() ? y + var39 * (double)var45 : y + var39 * (double)(var45 * var45 + var45) * 0.5;
				var10.addVertex(x + var37 * (double)var45, yVert, z + var41 * (double)var45);
			}

			var10.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityGrappleHook p_110775_1_)
    {
        return field_110792_a;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((EntityGrappleHook)p_110775_1_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityGrappleHook)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
