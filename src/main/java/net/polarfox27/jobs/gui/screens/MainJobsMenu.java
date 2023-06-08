package net.polarfox27.jobs.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.buttons.ButtonArrow;
import net.polarfox27.jobs.gui.buttons.ButtonJob;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainJobsMenu extends GuiScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    public int index = 0;
    private final List<String> jobs;

    /**
     * Creates the Main Jobs Menu GUI
     */
    public MainJobsMenu() {
        this.jobs = new ArrayList<>(ClientJobsData.JOBS_LEVELS.getJobs());
	}

    /**
     * Creates all the Jobs Button and up and down arrows if there are more than 4 Jobs
     */
    @Override
    public void initGui() {
        this.buttonList.clear();
        int offset = 0;
        int id = 0;
        for(String job : jobs.stream().skip(index).limit(4).collect(Collectors.toList())){
            this.addButton(new ButtonJob(id, this.width/2 - 100, this.height/2 - 67 + offset, job));
            id++;
            offset += 40;
        }
        if(index > 0){
            this.addButton(new ButtonArrow(id, this.width/2-9, 54, this, true));
            id++;
        }
        if(index < lastIndex()){
            this.addButton(new ButtonArrow(id, this.width/2-9, this.height/2+93, this, false));
        }
    }

    /**
     * @return false, this GUI doesn't pause the game
     */
    @Override
    public boolean doesGuiPauseGame() {
    	return false;
    }

    /**
     * @return the last page of the menu based on the amount of jobs
     */
    public int lastIndex(){
        int x = this.jobs.size()-4;
        if(x < 0)
            x = 0;
        return x;
    }

    /**
     * Renders the GUI on the screen
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        this.drawTexturedModalRect(this.width/2 - 128, this.height/2 - 110, 0, 0, 256, 220);
        GuiUtil.renderCenteredString(I18n.format("text.jobs.title"), Color.black.getRGB(), this.width/2, this.height/2 - 95, 2.0f);
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Goes one page up or down when the mouse is scrolled
     */
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (Mouse.getEventDWheel() != 0) {
            int x = -1 * Integer.signum(Mouse.getEventDWheel());
            this.index = JobsUtil.clamp(this.index + x, 0, lastIndex());
            this.initGui();
        }
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) {
        if(button instanceof ButtonJob)
            ((ButtonJob)button).onPress();
        else if(button instanceof ButtonArrow)
            ((ButtonArrow)button).onPress();
    }
}
