package com.teleCraftMod.gui;

import org.lwjgl.input.Keyboard;

import com.teleCraftMod.TeleCraft;
import com.teleCraftMod.item.ItemReadyEmergencyTeleport;
import com.teleCraftMod.packet.EmergencyTeleportPacket;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class EnterTextScreen extends GuiScreen
{
	public static final int GUI_ID = 0x27;
	
	private static final String DEFAULT_INFO = "Enter a name for this location, or leave blank.";
	
	private GuiTextField text;
	
	private String displayText;
	private boolean redText;

	public void initGui()
	{
		text = new GuiTextField(this.fontRendererObj, this.width / 2 - 50, this.height / 2 + 20, 100, 20);
		text.setMaxStringLength(10);
		text.setFocused(true);
		redText = false;
		displayText = DEFAULT_INFO;
	}

	protected void keyTyped(char par1, int par2)
	{
		this.text.textboxKeyTyped(par1, par2);  
		
		if(par2 != Keyboard.KEY_E || !this.text.isFocused())
			super.keyTyped(par1, par2);
		
		if(par2 == Keyboard.KEY_RETURN)
		{
			if(text.getText().length() == 0 || ItemReadyEmergencyTeleport.isIDValid(text.getText()))
			{
				TeleCraft.wrapper.sendToServer(new EmergencyTeleportPacket(false, text.getText()));//set the tp location and identifier
				
				mc.getMinecraft().thePlayer.closeScreen();
			}
			else
			{
				displayText = "Oops! That won't work. Try something else.";
				redText = true;
			}
		}
	}

	public void updateScreen()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
	}

	public void drawScreen(int par1, int par2, float par3)
	{
		this.drawDefaultBackground();
		this.text.drawTextBox();
		
		if(displayText != null)
			this.drawCenteredString(fontRendererObj, displayText, this.width / 2, this.height / 2, redText ? TeleCraft.getColorForRGB(255, 64, 64) : TeleCraft.getColorForRGB(255, 255, 255));
		
		super.drawScreen(par1, par2, par3);
	}

	protected void mouseClicked(int x, int y, int btn)
	{
		super.mouseClicked(x, y, btn);
		this.text.mouseClicked(x, y, btn);
	}
}
