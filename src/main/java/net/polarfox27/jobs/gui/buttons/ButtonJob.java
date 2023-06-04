package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.util.GuiUtil;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

public class ButtonJob extends GuiButton {



    private final String title;
    private final String job;

    /**
     * Creates a Job Button
     * @param id the button id
     * @param posX the x coordinate
     * @param posY the y coordinate
     * @param j the job the button is representing
     */
    public ButtonJob(int id, int posX, int posY, String j) {
        super(id, posX, posY, 200, 40, "");
        this.title = ClientJobsData.getJobName(j);
        this.job = j;
    }


    /**
     * Renders the widget on the screen
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            float f = 1.0f;
            GL11.glColor4f(f, f, f, 1.0F);
            GuiUtil.drawJobIcon(this.job, this.x+20, this.y+20, 32);
            long xp = ClientJobsData.playerJobs.getXPByJob(this.job);
            int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
            long total = lvl >= ClientJobsData.JOBS_LEVELS.getMaxLevel(this.job) ? xp :
                    ClientJobsData.JOBS_LEVELS.getXPForLevel(this.job, lvl+1);
            GuiUtil.renderProgressBar(this, this.x+45, this.y+15, 150, 12, xp, total);
            drawName();
        }
    }

    /**
     * Renders the Job name and level on the screen
     */
    private void drawName() {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
        String name = this.title + " (" + I18n.format("text.level") + " " + lvl + ")";
        int x = 120 - Minecraft.getMinecraft().fontRenderer.getStringWidth(name)/2;
        int y = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT/2;
        Minecraft.getMinecraft().fontRenderer.drawString(name, this.x + x, this.y + y, Color.black.getRGB());
    }


    /**
     * Opens the corresponding Jobs Infos screen when pressed.
     */
    public void onPress() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiJobInfos(job));
    }
}
