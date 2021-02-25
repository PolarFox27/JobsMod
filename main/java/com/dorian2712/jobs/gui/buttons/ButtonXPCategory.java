package com.dorian2712.jobs.gui.buttons;

import com.dorian2712.jobs.util.Constants.XPCategories;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import java.awt.*;

public class ButtonXPCategory extends GuiButton {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_job_infos.png");
    private final int xTexStart;
    private final int yTexStart;
    private final XPCategories category;

    public ButtonXPCategory(int index, int x, int y, XPCategories categories)
    {
        super(index, x, y, 80, 16, "");
        this.category = categories;
        this.xTexStart = 16 * this.category.index;
        this.yTexStart = this.category.isCategory ? 196 : 180;
    }

    public void setPosition(int xPos, int yPos)
    {
        this.x = xPos;
        this.y = yPos;
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(BACKGROUND);
            GlStateManager.disableDepth();
            int i = this.xTexStart;
            int j = this.yTexStart;

            if(hovered) GlStateManager.color(0.8F, 0.8F, 0.8F, 1.0F);
            else GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexturedModalRect(this.x, this.y, i, j, 16, 16);
            String txt = I18n.translateToLocal("category." + this.category.name());
            int txtWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(txt);
            Minecraft.getMinecraft().fontRenderer.drawString(txt, this.x + 48 - txtWidth/2, this.y + 5, Color.BLACK.getRGB());
            GlStateManager.enableDepth();
        }
    }
}
