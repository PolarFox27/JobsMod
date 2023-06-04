package net.polarfox27.jobs.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.unlock.UnlockStack;
import net.polarfox27.jobs.gui.buttons.ButtonBack;
import net.polarfox27.jobs.gui.buttons.ButtonXPCategory;
import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiJobInfos extends GuiScreen implements SliderParent{

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
        this.job = job;
        this.offsetUnlock = 0;
        this.page = 0;
        this.unlocked_stacks = ClientJobsData.getUnlockedStacksSorted(this.job);
    }

    /**
     * Creates the buttons for XP and Unlock Stacks and the slide bar for the Unlocked Stacks
     */
    @Override
    public void initGui() {
        this.slideBar = new SlideBarButton(0, this.top + 30, this.top+135, this.width/2 + 93, this, true);
        this.buttonList.clear();
        this.addButton(new ButtonBack(1, this.width/2 - 105 + offsetUnlock, this.height/2-85));
        this.addButton(new ButtonXPCategory(2, this, this.width/2 - 84 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.XP));
        this.addButton(new ButtonXPCategory(3, this, this.width/2 + 4 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.UNLOCK));

        if(offsetUnlock == -70) {
            this.addButton(slideBar);
        }
        super.initGui();
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
    	super.drawScreen(mouseX, mouseY, partialTicks);
    	this.left = this.width/2 - 110 + offsetUnlock;
        this.top = this.height/2 - 90;
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, 220, 180);//background
        this.drawGradients();

        if(offsetUnlock != 0) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(UNLOCK_BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.left + 225, this.top + 14, 0, 0, 70, 152);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(offsetUnlock != 0)
            this.drawUnlockedItems(mouseX, mouseY);

        if(isDragging(slideBar, mouseX, mouseY))
            updateSlider(slideBar, mouseY);
    }

    /**
     * Renders the Unlocked Stacks
     * @param mouseX the x coordinate of the mouse, used to find the tooltip to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltip to render
     */
    private void drawUnlockedItems(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        int renderIndex = -1;
        for(int i = 0; i < (unlocked_stacks.size() >= 7 ? 7 : unlocked_stacks.size() - page); i++) {
            itemRender.renderItemIntoGUI(unlocked_stacks.get(i + page).getStack(), this.left + 242, this.top + 27 + i*18);
            if(mouseX >= this.left + 242      && mouseX < this.left + 258 &&
               mouseY >= this.top + 27 + i*18 && mouseY < this.top + 27 + 16 + i*18)
                renderIndex = i;
        }
        if(renderIndex != -1)
            drawHoveringText(getItemToolTip(unlocked_stacks.get(renderIndex + page)), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * Renders the progress bars of the Job
     */
    private void drawGradients() {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(job);
        boolean isMaxLevel = ClientJobsData.playerJobs.isMax(job);
        GuiUtil.renderCenteredString(ClientJobsData.getJobName(job), Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top+15, 1.5f);
        GuiUtil.drawJobIcon(this.job, this.width/2 + offsetUnlock, this.top + 48, 48);

        String title1 = I18n.format("text.level") + " " + lvl;
        long progress1 = ClientJobsData.playerJobs.getXPByJob(job);
        long total1 = ClientJobsData.JOBS_LEVELS.getXPForLevel(job, lvl+1);
        GuiUtil.renderProgressBar(this, this.width/2 - 75 + offsetUnlock, this.top + 90, 150, 12, progress1, total1);
        GuiUtil.renderCenteredString(title1, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 86, 1.0f);

        if(isMaxLevel)
            return;

        long progress2 = ClientJobsData.playerJobs.getTotalXPByJob(job);
        long total2 = ClientJobsData.JOBS_LEVELS.getTotalXPForJob(job);
        String title2 = lvl < ClientJobsData.JOBS_LEVELS.getMaxLevel(job) ? I18n.format("text.total_progression") : "";
        GuiUtil.renderProgressBar(this, this.width / 2 - 75 + offsetUnlock, this.top + 115, 150, 12, progress2, total2);
        GuiUtil.renderCenteredString(title2, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 111, 1.0f);
    }


    /**
     * @param stack the stack hovered
     * @return the list of tooltip of the stack
     */
    public List<String> getItemToolTip(UnlockStack stack) {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(stack.getStack().getDisplayName());
        if(stack.getLevel() > ClientJobsData.playerJobs.getLevelByJob(this.job)) {
            tooltip.add(TextFormatting.RED  + I18n.format("text.unlock_" + stack.getType() + "_lvl")
                            + " " + stack.getLevel());
        }
        else
            tooltip.add(TextFormatting.GREEN  + I18n.format("text.unlock_" + stack.getType()));
        return tooltip;
    }

    /**
     * Goes one page up or down when the mouse is scrolled
     */
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (Mouse.getEventDWheel() != 0 && offsetUnlock == -70) {
            int x = -1 * Integer.signum(Mouse.getEventDWheel());
            setPage(true, JobsUtil.clamp(this.page + x, 0, getLastPage(true)));
            this.slideBar.update();
        }
    }

    /**
     * Performs the action of clicking on the button
     * @param button the button clicked
     */
    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if(button instanceof ButtonXPCategory)
            ((ButtonXPCategory)button).onPress();
        else if(button instanceof ButtonBack)
            Minecraft.getMinecraft().displayGuiScreen(new MainJobsMenu());
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
        return Mouse.isGrabbed() &&
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
