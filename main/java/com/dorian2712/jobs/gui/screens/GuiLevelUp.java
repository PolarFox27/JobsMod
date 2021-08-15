package com.dorian2712.jobs.gui.screens;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiLevelUp extends GuiScreen {

    public static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_level_up.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/jobs_icons.png");
    private final Job job;
    private final RenderItem renderer;

    public GuiLevelUp(Job job)
    {
        this.job = job;
        this.renderer = Minecraft.getMinecraft().getRenderItem();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if(this.fontRenderer == null) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.width/2 - 88, this.height/2 - 75, 0, 0, 176, 150); //background

        Minecraft.getMinecraft().getTextureManager().bindTexture(ICONS);
        int textY = ClientInfos.job.getLevelByJob(this.job) >= 25 ? 12 : 0;
        this.drawTexturedModalRect(this.width/2 - 75, this.height/2 - 30, 0, 92 + textY, 150, 12); //gradient

        drawScaledCustomSizeModalRect(this.width/2 -15, this.height/2 - 66, this.job.index*40, 0,
                40, 40, 30, 30, 256, 256);
        drawScaledCustomSizeModalRect(this.width/2 -15, this.height/2 - 66, 0, 40,
                40, 40, 30, 30, 256, 256);//icon

        String lvl = I18n.translateToLocal("text.level") + " " + ClientInfos.job.getLevelByJob(job);
        this.fontRenderer.drawString(lvl, this.width/2 - this.fontRenderer.getStringWidth(lvl)/2, this.height/2 - 27, Color.white.getRGB());
        String unlock = I18n.translateToLocal("text.unlocked");
        this.fontRenderer.drawString(unlock, this.width/2 - this.fontRenderer.getStringWidth(unlock)/2, this.height/2, Color.black.getRGB());
        this.drawUnlockedStacks(mouseX, mouseY, partialTicks);
        String reward = I18n.translateToLocal("text.rewards");
        this.fontRenderer.drawString(reward, this.width/2 - this.fontRenderer.getStringWidth(unlock)/2, this.height/2 + 34, Color.black.getRGB());
        this.drawRewardStacks(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    private void drawUnlockedStacks(int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        List<ItemStack> stacks = new ArrayList<>();
        int hovered = -1;
        for(Item i : ClientInfos.CRAFT_UNLOCK_LVL.keySet())
        {
            if(ClientInfos.CRAFT_UNLOCK_JOB.get(i) == this.job && ClientInfos.CRAFT_UNLOCK_LVL.get(i) == ClientInfos.job.getLevelByJob(this.job))
            {
                ItemStack s = new ItemStack(i);
                stacks.add(s);
            }
        }
        int y = this.height/2 + 11;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++)
        {
            this.renderer.renderItemAndEffectIntoGUI(stacks.get(i), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderToolTip(stacks.get(hovered), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    private void drawRewardStacks(int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        List<ItemStack> stacks = ClientInfos.CURRENT_REWARDS;
        int hovered = -1;

        int y = this.height/2 + 48;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++)
        {
            this.renderer.renderItemAndEffectIntoGUI(stacks.get(i).copy(), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderToolTipAndCount(stacks.get(hovered), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> tooltips = new ArrayList<>();
        FontRenderer font = stack.getItem().getFontRenderer(stack);

        tooltips.add(stack.getDisplayName());

        if(ClientInfos.CRAFT_UNLOCK_JOB.containsKey(stack.getItem()))
            tooltips.add(TextFormatting.GREEN + I18n.translateToLocal("text.unlock_craft"));

        this.drawHoveringText(tooltips, x, y, (font == null ? fontRenderer : font));
    }

    protected void renderToolTipAndCount(ItemStack stack, int x, int y)
    {
        List<String> tooltips = new ArrayList<>();
        FontRenderer font = stack.getItem().getFontRenderer(stack);

        tooltips.add(stack.getDisplayName());
        tooltips.add(TextFormatting.GREEN + "" + stack.getCount());

        this.drawHoveringText(tooltips, x, y, (font == null ? fontRenderer : font));
    }
}
