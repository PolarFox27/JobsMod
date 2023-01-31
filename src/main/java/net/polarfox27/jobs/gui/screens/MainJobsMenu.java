package net.polarfox27.jobs.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.buttons.ButtonArrow;
import net.polarfox27.jobs.gui.buttons.ButtonJob;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainJobsMenu extends Screen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    public int index = 0;
    private final List<String> jobs;

    /**
     * Creates the Main Jobs Menu GUI
     */
    public MainJobsMenu() {
		super(new TranslationTextComponent("text.jobs.title"));
        this.jobs = new ArrayList<>(ClientJobsData.JOBS_LEVELS.getJobs());
	}

    /**
     * Creates all the Jobs Button and up and down arrows if there are more than 4 Jobs
     */
    @Override
    protected void init() {
        this.buttons.clear();
        this.children.clear();
        int offset = 0;
        for(String job : jobs.stream().skip(index).limit(4).collect(Collectors.toList())){
            this.addButton(new ButtonJob(this.width/2 - 100, this.height/2 - 67 + offset, job));
            offset += 40;
        }
        if(index > 0){
            this.addButton(new ButtonArrow(this.width/2-9, 43, this, true));
        }
        if(index < lastIndex()){
            this.addButton(new ButtonArrow(this.width/2-9, this.height/2+93, this, false));
        }
    }

    /**
     * @return false, this GUI doesn't pause the game
     */
    @Override
    public boolean isPauseScreen() {
    	return false;
    }

    /**
     * @return the last page of the menu based on the amount of jobs
     */
    private int lastIndex(){
        int x = this.jobs.size()-4;
        if(x < 0)
            x = 0;
        return x;
    }

    /**
     * Renders the GUI on the screen
     * @param mStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
    	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
        this.blit(mStack, this.width/2 - 128, this.height/2 - 110, 0, 0, 256, 220);
        GuiUtil.renderCenteredString(mStack, I18n.get("text.jobs.title"), Color.black.getRGB(), this.width/2, 30, 2.0f);
    	super.render(mStack, mouseX, mouseY, partialTicks);
    }

    /**
     * Goes one page up or down when the mouse is scrolled
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param direction the direction of the scroll
     * @return true
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        if (direction != 0) {
            int x = -1 * Integer.signum((int)direction);
            this.index = JobsUtil.clamp(this.index + x, 0, lastIndex());
            this.init();
        }
        return true;
    }
}
