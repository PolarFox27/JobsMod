package com.dorian2712.jobs.gui.screens;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.gui.buttons.ButtonJob;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import java.awt.Color;
import java.io.IOException;

public class MainJobsMenu extends GuiScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/background.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/jobs_icons.png");

    @Override
    public void initGui()
    {
        this.addButton(new ButtonJob(0, this.width/2 - 100, this.height/2 - 70, Job.byIndex(0)));
        this.addButton(new ButtonJob(1, this.width/2 - 100, this.height/2 - 30, Job.byIndex(1)));
        this.addButton(new ButtonJob(2, this.width/2 - 100, this.height/2 + 10, Job.byIndex(2)));
        this.addButton(new ButtonJob(3, this.width/2 - 100, this.height/2 + 50, Job.byIndex(3)));
        super.initGui();
     }
    
    @Override
    public boolean doesGuiPauseGame() 
    {
    	return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(this.width/2 - 128, this.height/2 - 110, 0, 0, 256, 220);
        drawTitle();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTitle()
    {
        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        String title = I18n.translateToLocal("text.jobs.title");
        int x = this.width/4 - this.fontRenderer.getStringWidth(title)/4 - 5;
        int y = this.height/2 - 114;
        this.fontRenderer.drawString(title, x, y, Color.black.getRGB(), false);
        GL11.glPopMatrix();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        Job j = Job.byIndex(button.id);
        if(j != Job.NONE)
            Minecraft.getMinecraft().displayGuiScreen(new GuiJobInfos(j));
        super.actionPerformed(button);
    }
}
