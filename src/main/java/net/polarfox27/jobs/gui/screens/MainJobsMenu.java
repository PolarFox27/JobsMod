package net.polarfox27.jobs.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.buttons.ButtonArrow;
import net.polarfox27.jobs.gui.buttons.ButtonJob;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MainJobsMenu extends Screen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    public int index = 0;
    private final List<String> jobs;

    /**
     * Creates the Main Jobs Menu GUI
     */
    public MainJobsMenu() {
		super(new TranslatableComponent("text.jobs.title"));
        this.jobs = new ArrayList<>(ClientJobsData.JOBS_LEVELS.getJobs());
	}

    /**
     * Creates all the Jobs Button and up and down arrows if there are more than 4 Jobs
     */
    @Override
    protected void init() {
        this.clearWidgets();
        int offset = 0;
        for(String job : jobs.stream().skip(index).limit(4).toList()){
            this.addRenderableWidget(new ButtonJob(this.width/2 - 100, this.height/2 - 67 + offset, job));
            offset += 40;
        }
        if(index > 0){
            this.addRenderableWidget(new ButtonArrow(this.width/2-9, 43, this, true));
        }
        if(index < lastIndex()){
            this.addRenderableWidget(new ButtonArrow(this.width/2-9, this.height/2+93, this, false));
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
     * @param mStack the render stack
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the rendering ticks
     */
    @Override
    public void render(@NotNull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(mStack, this.width/2 - 128, this.height/2 - 110, 0, 0, 256, 220);
        GuiUtil.renderCenteredString(mStack, I18n.get("text.jobs.title"),
                Color.black.getRGB(), this.width/2, this.height/2 - 95, 2.0f);
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
