package net.polarfox27.jobs.gui.screens;


import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.unlock.UnlockStack;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class GuiLevelUp extends Screen {

    public static final ResourceLocation TEXTURES = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_level_up.png");
    private final String job;
    private final int previousLevel;

    /**
     * Creates a Level Up GUI for a job
     *
     * @param job the job that leveled up
     * @param previousLevel the level the player was at before leveling up
     */
    public GuiLevelUp(String job, int previousLevel) {
    	super(TextUtil.EMPTY);
        this.job = job;
        this.previousLevel = previousLevel;
    }

    /**
     * @return false, this GUI doesn't pause the game
     */
    @Override
    public boolean isPauseScreen() {
    	return false;
    }

    /**
     * Renders the GUI on the screen
     * @param gui the render object
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the rendering ticks
     */
    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        GuiUtil.drawTexture(gui, TEXTURES, this.width/2 - 88, this.height/2 - 75, 0, 0, 176, 150); //background
        GuiUtil.drawJobIcon(gui, job, this.width/2, this.height/2-47, 40);

        String lvl = GuiUtil.translate("text.level", ClientJobsData.playerJobs.getLevelByJob(job));
        GuiUtil.renderProgressBarWithText(gui, this.width/2 - 75, this.height/2 - 25, 150, 12, 1, 1, lvl);

        String unlock = GuiUtil.translate("text.unlocked");
        GuiUtil.renderCenteredString(gui, unlock, Color.BLACK.getRGB(), this.width/2, this.height/2, 1.0f);
        this.drawUnlockedStacks(gui, mouseX, mouseY);

        String reward = GuiUtil.translate("text.rewards");
        GuiUtil.renderCenteredString(gui, reward, Color.BLACK.getRGB(), this.width/2, this.height/2 + 36, 1.0f);
        this.drawRewardStacks(gui, mouseX, mouseY);

        super.render(gui, mouseX, mouseY, partialTicks);
    }


    /**
     * Renders the Unlocked stacks of the screen
     * @param gui the render object
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawUnlockedStacks(GuiGraphics gui, int mouseX, int mouseY) {
        List<UnlockStack> stacks = ClientJobsData.getUnlockedStacksSorted(this.job);
        stacks.removeIf(x -> x.getLevel() > ClientJobsData.playerJobs.getLevelByJob(job) || x.getLevel() <= previousLevel);

        if(stacks.isEmpty()){
            String text = GuiUtil.translate("text.no_unlock");
            GuiUtil.renderCenteredString(gui, text, Color.RED.getRGB(), this.width/2, this.height/2 + 19, 0.75f);
            return;
        }

        int hovered = -1;
        int y = this.height/2 + 11;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++) {
            gui.renderItem(stacks.get(i).getStack(), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderUnlockedCraftToolTip(gui, stacks.get(hovered), mouseX, mouseY);
    }

    /**
     * Renders the rewards stacks on the screen
     * @param gui the render object
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawRewardStacks(GuiGraphics gui, int mouseX, int mouseY) {
        List<ItemStack> stacks = ClientJobsData.CURRENT_REWARDS;

        if(stacks.isEmpty()){
            String text = GuiUtil.translate("text.no_reward");
            GuiUtil.renderCenteredString(gui, text, Color.RED.getRGB(), this.width/2, this.height/2 + 56, 0.75f);
            return;
        }

        int hovered = -1;
        int y = this.height/2 + 48;
        int sizeX = stacks.size()*16 + (stacks.size()-1)*6;
        int x = this.width/2 - sizeX/2;
        for(int i = 0; i < stacks.size(); i++) {
            gui.renderItem(stacks.get(i).copy(), x + i*22, y);
            if(mouseX >= x + i*22 && mouseX < x + i*22 + 16 && mouseY >= y && mouseY < y + 16)
                hovered = i;
        }
        if(hovered != -1)
            this.renderToolTipAndCount(gui, stacks.get(hovered), mouseX, mouseY);
    }


    /**
     * Renders the tooltip for the hovered UnlockStack
     * @param gui the render object
     * @param stack the hovered UnlockStack
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    private void renderUnlockedCraftToolTip(GuiGraphics gui, UnlockStack stack, int x, int y) {
        List<Component> tooltips = new ArrayList<>();
        tooltips.add(stack.getStack().getHoverName());
        for(String t : stack.getTypes())
            tooltips.add(Component.translatable("text.unlock_" + t));
        gui.renderComponentTooltip(Minecraft.getInstance().font, tooltips, x, y);
    }

    /**
     * Renders the tooltip and the count for the hovered reward
     * @param gui the render object
     * @param stack the hovered stack
     * @param x the x coordinate of the mouse
     * @param y the y coordinate of the mouse
     */
    protected void renderToolTipAndCount(GuiGraphics gui, ItemStack stack, int x, int y) {
        List<Component> tooltips = new ArrayList<>();

        tooltips.add(stack.getHoverName());
        tooltips.add(Component.literal(ChatFormatting.GREEN + Integer.toString(stack.getCount())));

        gui.renderComponentTooltip(Minecraft.getInstance().font, tooltips, x, y);
    }
}
