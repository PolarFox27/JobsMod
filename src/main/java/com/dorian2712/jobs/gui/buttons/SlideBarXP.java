package com.dorian2712.jobs.gui.buttons;

import com.dorian2712.jobs.gui.screens.GuiHowXP;
import com.dorian2712.jobs.gui.screens.GuiJobInfos;
import com.dorian2712.jobs.util.JobsMath;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class SlideBarXP extends GuiButton {

    private final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":textures/gui/gui_unlocked_items.png");
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffText;
    private final GuiHowXP gui;

    public SlideBarXP(int index, int posX, int posY, GuiHowXP gui)
    {
        super(index, posX, posY, 12, 15, "");
        this.xTexStart = 70;
        this.yTexStart = 0;
        this.yDiffText = 15;
        this.gui = gui;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(this.texture);
            int j = this.yTexStart;

            if(this.hovered)
            {
                j += this.yDiffText;
                if(Mouse.isButtonDown(0))
                {
                    this.y = JobsMath.clamp(mouseY-7, this.gui.top + 29, this.gui.top + 29 + 91);
                    this.gui.page = (int) ((float)this.gui.pageNumber * ((float) (this.y - (this.gui.top + 29))/ (float) 91));
                }
            }
            this.drawTexturedModalRect(this.x, this.y, this.xTexStart, j, 12, 15); //Icon
        }
    }

}

