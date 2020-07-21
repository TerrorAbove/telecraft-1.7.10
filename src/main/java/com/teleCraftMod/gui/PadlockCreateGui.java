package com.teleCraftMod.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.packet.SetPinsPacket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.ResourceLocation;

public class PadlockCreateGui extends GuiScreen
{
	public static final ResourceLocation PADLOCK_CREATION_SCREEN = new ResourceLocation("telecraft:textures/gui/padlock_create_screen.png");
	public static final ResourceLocation PADLOCK_CREATION_PINS = new ResourceLocation("telecraft:textures/gui/vertical_pin.png");
	public static final ResourceLocation PADLOCK_CREATION_CYLINDER = new ResourceLocation("telecraft:textures/gui/cylinder.png");
	public static final int GUI_ID = 0x31;
	
	public static final int NUMBER_OF_PINS = 16;
	public static final int PIN_MAX = 6;//0 - 6 (inclusive)
	
	private int xSize;
	private int ySize;
	private int guiLeft;
	private int guiTop;
	
	private int[] pin_data;
	
	public PadlockCreateGui()
	{
		super();
		
		xSize = 248;
		ySize = 166;
		
		pin_data = new int[NUMBER_OF_PINS];
		
		for(int i = 0; i < pin_data.length; i++)
			pin_data[i] = PIN_MAX;
	}
	
	public void initGui()
	{
		super.initGui();
		
		this.guiLeft = (width - xSize) / 2;
		this.guiTop = (height - ySize) / 2;
		
		for(int i = 0; i < NUMBER_OF_PINS; i++)//draw + buttons
			if(i%2 == 1)
				buttonList.add(new GuiButton(i, 7*i + guiLeft+58, guiTop+104, 10, 8, "+"));//upper
			else
				buttonList.add(new GuiButton(i, 7*i + guiLeft+57, guiTop+147, 10, 8, "+"));//lower
		
		for(int i = 0; i < NUMBER_OF_PINS; i++)//draw - buttons
			if(i%2 == 1)
				buttonList.add(new GuiButton(NUMBER_OF_PINS+i, 7*i + guiLeft+58, guiTop+112, 10, 8, "-"));
			else
				buttonList.add(new GuiButton(NUMBER_OF_PINS+i, 7*i + guiLeft+57, guiTop+155, 10, 8, "-"));
		
		buttonList.add(new GuiButton(NUMBER_OF_PINS*2, guiLeft+64, guiTop+70, 54, 14, "Randomize"));
		buttonList.add(new GuiButton(1 + NUMBER_OF_PINS*2, guiLeft+130, guiTop+70, 28, 14, "Done"));
	}
	
	public void drawScreen(int x, int y, float partialTicks)
	{
		drawDefaultBackground();
		
		mc.getTextureManager().bindTexture(PADLOCK_CREATION_SCREEN);
		
		this.guiLeft = (width - xSize) / 2;
		this.guiTop = (height - ySize) / 2;
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		mc.getTextureManager().bindTexture(PADLOCK_CREATION_CYLINDER);
		
		drawTexturedModalRect(guiLeft+30, guiTop+60, 0, 0, 165, 87);
		
		mc.getTextureManager().bindTexture(PADLOCK_CREATION_PINS);
		
		for(int i = 0; i < NUMBER_OF_PINS; i++)
		{
			int currPinHeight = (int)(26 * (1+(double)pin_data[i]) / (1+PIN_MAX));
			drawTexturedModalRect(i*7 + guiLeft + 61, (26 - currPinHeight) + guiTop + 120, 0, 0, 3, currPinHeight);
		}
		
		super.drawScreen(x, y, partialTicks);
		
		drawCenteredString(fontRendererObj, "Set the pins for this lock: ", guiLeft + xSize/2, guiTop + 20, Color.white.hashCode());
	}
	
	@Override
	public void actionPerformed(GuiButton button)
	{
		if(button.id < NUMBER_OF_PINS)//try to increase
		{
			pin_data[button.id] = Math.min(pin_data[button.id]+1, PIN_MAX);
		}
		else if(button.id < 2*NUMBER_OF_PINS)//try to decrease
		{
			int i = button.id - NUMBER_OF_PINS;
			pin_data[i] = Math.max(pin_data[i]-1, 0);
		}
		else if(button.id == 2*NUMBER_OF_PINS)//randomize
		{
			randomizePins();
		}
		else//done
		{
			String paramString = "";
			for(int i = 0; i < pin_data.length; i++)
			{
				paramString += pin_data[i];
				
				if(i < pin_data.length-1)
					paramString += ",";
			}
			
			TeleCraft.wrapper.sendToServer(new SetPinsPacket(paramString));
			mc.thePlayer.closeScreen();
		}
	}
	
	private void randomizePins()
	{
		for(int i = 0; i < pin_data.length; i++)
		{
			pin_data[i] = (int)(Math.random() * (1+PIN_MAX));
		}
	}
}
