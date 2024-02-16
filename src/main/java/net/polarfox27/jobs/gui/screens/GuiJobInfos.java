package net.polarfox27.jobs.gui.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.unlock.UnlockStack;
import net.polarfox27.jobs.gui.buttons.ButtonBack;
import net.polarfox27.jobs.gui.buttons.ButtonXPCategory;
import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiJobInfos extends Screen implements SliderParent{

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private static final ResourceLocation UNLOCK_BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_unlocked_stacks.png");
    public final String job;
    public int left;
    public int top;
    public int offsetUnlock;
    public int page;
    private final List<UnlockStack> unlocked_stacks;

    private SlideBarButton slideBar;

    /**
     * Creates a Job Infos GUI
     * @param job the job
     */
    public GuiJobInfos(String job) {
    	super(TextUtil.EMPTY);
        this.job = job;
        this.offsetUnlock = 0;
        this.page = 0;
        this.unlocked_stacks = ClientJobsData.getUnlockedStacksSorted(this.job);
    }

    /**
     * Creates the buttons for XP and Unlock Stacks and the slide bar for the Unlocked Stacks
     */
    @Override
    public void init() {
        this.slideBar = new SlideBarButton(this.top + 30, this.top+135, this.width/2 + 93, this, true);
        this.clearWidgets();
        this.addRenderableWidget(new ButtonBack(this.width/2 - 105 + offsetUnlock, this.height/2-85, this));
        this.addRenderableWidget(new ButtonXPCategory(this.width/2 - 84 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.XP, this));
        this.addRenderableWidget(new ButtonXPCategory(this.width/2 + 4 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.UNLOCK, this));

        if(offsetUnlock == -70) {
            this.addRenderableWidget(slideBar);
        }
        super.init();
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
    	this.left = this.width/2 - 110 + offsetUnlock;
        this.top = this.height/2 - 90;
        GuiUtil.drawTexture(gui, BACKGROUND, this.left, this.top, 0, 0, 220, 180);//background
        this.drawGradients(gui);

        if(offsetUnlock != 0) {
            GuiUtil.drawTexture(gui, UNLOCK_BACKGROUND, this.left + 225, this.top + 14, 0, 0, 70, 152);
        }
        super.render(gui, mouseX, mouseY, partialTicks);
        if(offsetUnlock != 0)
            this.drawUnlockedItems(gui, mouseX, mouseY);

        if(isDragging(slideBar, mouseX, mouseY))
            updateSlider(slideBar, mouseY);

        super.render(gui, mouseX, mouseY, partialTicks);
    }

    /**
     * Renders the Unlocked Stacks
     * @param gui the render object
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawUnlockedItems(GuiGraphics gui, int mouseX, int mouseY) {
        int renderIndex = -1;
        for(int i = 0; i < (unlocked_stacks.size() >= 7 ? 7 : unlocked_stacks.size() - page); i++) {
            gui.renderItem(unlocked_stacks.get(i + page).getStack(), this.left + 242, this.top + 27 + i*18);
            if(mouseX >= this.left + 242      && mouseX < this.left + 258 &&
               mouseY >= this.top + 27 + i*18 && mouseY < this.top + 27 + 16 + i*18)
                renderIndex = i;
        }
        if(renderIndex != -1)
            gui.renderComponentTooltip(Minecraft.getInstance().font,
                    getItemToolTip(unlocked_stacks.get(renderIndex + page)), mouseX, mouseY);
    }

    /**
     * Renders the progress bars of the Job
     * @param gui the render object
     */
    private void drawGradients(GuiGraphics gui) {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(job);
        boolean isMaxLevel = ClientJobsData.playerJobs.isMax(job);
        GuiUtil.renderCenteredString(gui, ClientJobsData.getJobName(job), Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top+15, 1.5f);
        GuiUtil.drawJobIcon(gui, this.job, this.width/2 + offsetUnlock, this.top + 48, 48);

        String title1 = GuiUtil.translate("text.level", lvl);
        long progress1 = ClientJobsData.playerJobs.getXPByJob(job);
        long total1 = ClientJobsData.JOBS_LEVELS.getXPForLevel(job, lvl+1);
        GuiUtil.renderProgressBar(gui, this.width/2 - 75 + offsetUnlock, this.top + 90, 150, 12, progress1, total1);
        GuiUtil.renderCenteredString(gui, title1, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 86, 1.0f);

        if(isMaxLevel)
            return;

        long progress2 = ClientJobsData.playerJobs.getTotalXPByJob(job);
        long total2 = ClientJobsData.JOBS_LEVELS.getTotalXPForJob(job);
        String title2 = lvl < ClientJobsData.JOBS_LEVELS.getMaxLevel(job) ? GuiUtil.translate("text.total_progression") : "";
        GuiUtil.renderProgressBar(gui, this.width / 2 - 75 + offsetUnlock, this.top + 115, 150, 12, progress2, total2);
        GuiUtil.renderCenteredString(gui, title2, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 111, 1.0f);
    }


    /**
     * @param stack the stack hovered
     * @return the list of tooltip of the stack
     */
    public List<Component> getItemToolTip(UnlockStack stack) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(stack.getStack().getHoverName());

        if(stack.getLevel() > ClientJobsData.playerJobs.getLevelByJob(this.job)) {
            for(String t : stack.getTypes())
                tooltip.add(TextUtil.coloredComponent(ChatFormatting.RED, Component.translatable("text.unlock_" + t + "_lvl", stack.getLevel())));
        }
        else
            for(String t : stack.getTypes())
                tooltip.add(Component.translatable("text.unlock_" + t));
        return tooltip;
    }

    /**
     * Updates the Unlocked Stacks when the mouse is scrolled
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param direction the scroll direction
     * @return true
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
    	if (direction != 0 && offsetUnlock == -70) {
            int x = -1 * Integer.signum((int)direction);
            setPage(true, JobsUtil.clamp(this.page + x, 0, getLastPage(true)));
            this.slideBar.update();
        }
    	return true;
    }

    /**
     * checks if the slide bar is dragged
     * @param btn the slide bar button
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return true if the slide bar is dragged
     */
    @Override
    public boolean isDragging(SlideBarButton btn, int mouseX, int mouseY){
        return isDragging() &&
                mouseX >= this.width/2 + offsetUnlock + 163 && mouseX <= this.width/2 + offsetUnlock + 175 &&
                mouseY >= this.top + 30 && mouseY <= this.top + 135;
    }

    /**
     * Updates the slider based on its current position
     * @param btn the slide bar button
     * @param mouseY the position of the mouse on the Y axis
     */
    @Override
    public void updateSlider(SlideBarButton btn, int mouseY){
        int y = JobsUtil.clamp(mouseY - 30 - this.top, 0, 105);
        setPage(true, (int)Math.round((y/105.0d)*(double)getLastPage(true)));
        btn.update();
    }

    /**
     * @param isVertical not used because there is only 1 slider
     * @return the current page of the slider of the given orientation
     */
    @Override
    public int getPage(boolean isVertical) {
        return page;
    }

    /**
     * @param isVertical not used because there is only 1 slider
     * @return the last page of the slider
     */
    @Override
    public int getLastPage(boolean isVertical) {
        return unlocked_stacks.size() <= 7 ? 0 : unlocked_stacks.size() - 7;
    }

    /**
     * Sets the slider to a specific page
     * @param isVertical not used because there is only 1 slider
     * @param page the new page
     */
    @Override
    public void setPage(boolean isVertical, int page) {
        this.page = JobsUtil.clamp(page, 0, getLastPage(true));
    }
}
