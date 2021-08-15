package com.dorian2712.jobs.gui;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.awt.Color;

public class GuiGainXP extends Gui {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_gain_xp.png");

    public GuiGainXP(Job job, long xpAdded)
    {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        long xp = ClientInfos.job.getXPByJob(job);
        long total = Constants.XP_BY_LEVEL[ClientInfos.job.getLevelByJob(job)+1];
        int width = (int)(150 * ((double)xp /(double)total));
        String title = TextFormatting.WHITE + I18n.translateToLocal("jobs." + job.name) + " (lvl " + ClientInfos.job.getLevelByJob(job) + ") : " +
                TextFormatting.AQUA + "+" + xpAdded + TextFormatting.WHITE + " xp";
        String xpTotal = xp + "/" + total;
        int titleWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(title);
        int xpTotalWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(xpTotal);

        this.drawTexturedModalRect(res.getScaledWidth()/2 - 90, 5, 0, 0, 180, 50);//background
        this.drawTexturedModalRect(res.getScaledWidth()/2 - 75, 35, 0, 50, 150, 12);//progressbackground
        this.drawTexturedModalRect(res.getScaledWidth()/2 - 75, 35, 0, 62, width, 12);//progressbar
        Minecraft.getMinecraft().fontRenderer.drawString(title, res.getScaledWidth()/2 - titleWidth/2, 15, Color.white.getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(xpTotal, res.getScaledWidth()/2 - xpTotalWidth/2, 38, Color.black.getRGB());
        GlStateManager.popMatrix();
    }


    public static class GuiAddXpInfos{
        public long xpAdded;
        public Job job;
        public long ticks;

        public GuiAddXpInfos(Job j, long xp)
        {
            this.job = j;
            this.xpAdded = xp;
            ticks = System.currentTimeMillis() + 5000;
        }

    }

}
