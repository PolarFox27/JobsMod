package net.polarfox27.jobs.gui.buttons;

import net.polarfox27.jobs.data.ClientInfos;
import net.polarfox27.jobs.util.Constants;
import net.polarfox27.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import java.awt.Color;

public class ButtonJob extends GuiButton {

    private final ResourceLocation texture = new ResourceLocation(Reference.MOD_ID + ":textures/gui/jobs_icons.png");
    private final int xTexStart;
    private final int yTexStart;
    private final String title;
    private final Constants.Job job;

    public ButtonJob(int index, int posX, int posY, Constants.Job j)
    {
        super(index, posX, posY, 200, 30, "");
        this.xTexStart = 40 * j.index;
        this.yTexStart = 0;
        this.title = I18n.translateToLocal("jobs." + j.name);
        this.job = j;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(this.texture);
            drawIcon();
            drawGradient();
            drawName();

        }
    }

    private void drawIcon()
    {
        int i = this.xTexStart;
        int j = this.yTexStart;
        drawScaledCustomSizeModalRect(this.x, this.y, i, j, 40, 40, 30, 30, 256, 256);
        drawScaledCustomSizeModalRect(this.x, this.y, 0, 40, 40, 40, 30, 30, 256, 256);
    }

    private void drawGradient()
    {
        long xp = ClientInfos.job.getXPByJob(this.job);
        int lvl = ClientInfos.job.getLevelByJob(this.job);
        if(lvl < 25)
        {
            long total = Constants.XP_BY_LEVEL[lvl+1];
            int size = (int)(150*((double)xp / (double)total));
            this.drawTexturedModalRect(this.x + 45, this.y + 15, 0, 80, 150, 12); //background
            this.drawTexturedModalRect(this.x + 45, this.y + 15, 0, 92, size, 12);

            String info = xp + "/" + total;
            int widthInfo = Minecraft.getMinecraft().fontRenderer.getStringWidth(info);
            Minecraft.getMinecraft().fontRenderer.drawString(info, this.x + 120 - widthInfo/2, this.y + 18,
                    Color.white.getRGB());
        }
        else
        {
            int size = 150;
            this.drawTexturedModalRect(this.x + 45, this.y + 15, 0, 80, 150, 12); //background
            this.drawTexturedModalRect(this.x + 45, this.y + 15, 0, 104, size, 12);

            String info = I18n.translateToLocal("text.level.max");
            int widthInfo = Minecraft.getMinecraft().fontRenderer.getStringWidth(info);
            Minecraft.getMinecraft().fontRenderer.drawString(info, this.x + 120 - widthInfo/2, this.y + 18,
                    Color.white.getRGB());
        }
    }

    private void drawName()
    {
        int lvl = ClientInfos.job.getLevelByJob(this.job);
        String name = this.title + " (" + I18n.translateToLocal("text.level") + " " + lvl + ")";
        int x = 120 - Minecraft.getMinecraft().fontRenderer.getStringWidth(name)/2;
        int y = 2;
        Minecraft.getMinecraft().fontRenderer.drawString(name, this.x + x, this.y + y, Color.black.getRGB());
    }
}
