package net.polarfox27.jobs.gui.screens;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.unlock.UnlockStack;
import net.polarfox27.jobs.util.GuiUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GuiLevelUp extends GuiScreen {

    public static final ResourceLocation TEXTURES = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_level_up.png");
    private final String job;

    /**
     * Creates a Level Up GUI for a job
     * @param job the job that leveled up
     */
    public GuiLevelUp(String job) {
        this.job = job;
    }

    /**
     * @return false, this GUI doesn't pause the game
     */
    @Override
    public boolean doesGuiPauseGame() {
    	return false;
    }

    /**
     * Renders the GUI on the screen
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(this.fontRenderer == null)
            return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.width/2 - 88, this.height/2 - 75, 0, 0, 176, 150); //background


        
        GuiUtil.drawJobIcon(job, this.width/2, this.height/2-47, 40);
        String lvl = I18n.format("text.level") + " " + ClientJobsData.playerJobs.getLevelByJob(job);
        GuiUtil.renderProgressBarWithText(this, this.width/2 - 75, this.height/2 - 25, 150, 12, 1, 1, lvl);

        String unlock = I18n.format("text.unlocked");
        GuiUtil.renderCenteredString(unlock, Color.BLACK.getRGB(), this.width/2, this.height/2, 1.0f);
        this.drawUnlockedStacks(mouseX, mouseY);
        String reward = I18n.format("text.rewards");
        GuiUtil.renderCenteredString(reward, Color.BLACK.getRGB(), this.width/2, this.height/2 + 36, 1.0f);
        this.drawRewardStacks(mouseX, mouseY);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }


    /**
     * Renders the Unlocked stacks of the screen
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawUnlockedStacks(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        List<UnlockStack> stacks = ClientJobsData.getUnlockedStacksSorted(this.job);
        stacks.removeIf(x -> x.getLevel() != ClientJobsData.playerJobs.getLevelByJob(job));

        if(stacks.isEmpty()){
            String text = I18n.format("text.no_unlock");
            GuiUtil.renderCenteredString(text, Color.RED.getRGB(), this.width/2, this.height/2 + 19, 0.75f);
            return;
        }

        int hovered = -1;
        int y = this.height/2 + 11;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++) {
            this.itemRender.renderItemIntoGUI(stacks.get(i).getStack(), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderUnlockedCraftToolTip(stacks.get(hovered), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Renders the rewards stacks on the screen
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawRewardStacks(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        List<ItemStack> stacks = ClientJobsData.CURRENT_REWARDS;

        if(stacks.isEmpty()){
            String text = I18n.format("text.no_reward");
            GuiUtil.renderCenteredString(text, Color.RED.getRGB(), this.width/2, this.height/2 + 56, 0.75f);
            return;
        }

        int hovered = -1;
        int y = this.height/2 + 48;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++) {
            this.itemRender.renderItemIntoGUI(stacks.get(i).copy(), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderToolTipAndCount(stacks.get(hovered), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }


    /**
     * Renders the tooltip for the hovered UnlockStack
     * @param stack the hovered UnlockStack
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    private void renderUnlockedCraftToolTip(UnlockStack stack, int x, int y) {
        List<String> tooltips = new ArrayList<>();
        tooltips.add(stack.getStack().getDisplayName());
        tooltips.add(TextFormatting.GREEN + I18n.format("text.unlock_" + stack.getType()));

        this.drawHoveringText(tooltips, x, y);
    }

    /**
     * Renders the tooltip and the count for the hovered reward
     * @param stack the hovered stack
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    protected void renderToolTipAndCount(ItemStack stack, int x, int y) {
        List<String> tooltips = new ArrayList<>();

        tooltips.add(stack.getDisplayName());
        tooltips.add(TextFormatting.GREEN + Integer.toString(stack.getCount()));

        this.drawHoveringText(tooltips, x, y);
    }
}
