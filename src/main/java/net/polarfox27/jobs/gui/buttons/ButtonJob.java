package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ButtonJob extends Button {



    private final String title;
    private final String job;

    /**
     * Creates a Job Button
     * @param posX the x coordinate
     * @param posY the y coordinate
     * @param j the job the button is representing
     */
    public ButtonJob(int posX, int posY, String j) {
        super(posX, posY, 200, 40, TextUtil.EMPTY,new OnPressed(), TextUtil.BUTTON_NARRATOR);
        this.title = ClientJobsData.getJobName(j);
        this.job = j;
    }


    /**
     * Renders the widget on the screen
     * @param gui the render object
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the rendering ticks
     */
    @Override
    public void renderWidget(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            GuiUtil.drawJobIcon(gui, this.job, this.getX()+20, this.getY()+20, 32);
            long xp = ClientJobsData.playerJobs.getXPByJob(this.job);
            int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
            long total = lvl >= ClientJobsData.JOBS_LEVELS.getMaxLevel(this.job) ? xp :
                    ClientJobsData.JOBS_LEVELS.getXPForLevel(this.job, lvl+1);
            GuiUtil.renderProgressBar(gui,
                    this.getX()+45, this.getY()+15, 150, 12, xp, total);
            drawName(gui);
        }
    }

    /**
     * Renders the Job name and level on the screen
     * @param gui the render object
     */
    private void drawName(GuiGraphics gui) {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
        String name = GuiUtil.translate("text.job.button", this.title, lvl);
        int x = 120 - Minecraft.getInstance().font.width(name)/2;
        int y = Minecraft.getInstance().font.lineHeight/2;
        GuiUtil.renderString(gui, name, this.getX() + x, this.getY() + y, Color.black.getRGB());
    }
    
    public static class OnPressed implements OnPress{

        /**
         * Opens a Jobs Infos GUI when the button is clicked
         * @param btn the button clicked
         */
        @Override
		public void onPress(@NotNull Button btn) {
			if(!(btn instanceof ButtonJob b))
                return;
            Minecraft.getInstance().setScreen(new GuiJobInfos(b.job));
		}
    	
    }
}
