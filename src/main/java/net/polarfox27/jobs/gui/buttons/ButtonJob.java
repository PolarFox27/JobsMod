package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.util.GuiUtil;
import org.lwjgl.opengl.GL11;

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
        super(posX, posY, 200, 40, new StringTextComponent(""),new OnPressed());
        this.title = ClientJobsData.getJobName(j);
        this.job = j;
    }


    /**
     * Renders the widget on the screen
     * @param mStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            float f = 1.0f;
            GL11.glColor4f(f, f, f, 1.0F);
            GuiUtil.drawJobIcon(mStack, this, this.job, this.x+20, this.y+20, 32);
            long xp = ClientJobsData.playerJobs.getXPByJob(this.job);
            int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
            long total = lvl >= ClientJobsData.JOBS_LEVELS.getMaxLevel(this.job) ? xp :
                    ClientJobsData.JOBS_LEVELS.getXPForLevel(this.job, lvl+1);
            GuiUtil.renderProgressBar(mStack, this, this.x+45, this.y+15, 150, 12, xp, total);
            drawName(mStack);
        }
    }

    /**
     * Renders the Job name and level on the screen
     * @param mStack
     */
    private void drawName(MatrixStack mStack) {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
        String name = this.title + " (" + I18n.get("text.level") + " " + lvl + ")";
        int x = 120 - Minecraft.getInstance().font.width(name)/2;
        int y = Minecraft.getInstance().font.lineHeight/2;
        Minecraft.getInstance().font.draw(mStack, name, this.x + x, this.y + y, Color.black.getRGB());
    }
    
    public static class OnPressed implements IPressable{

        /**
         * Opens a Jobs Infos GUI when the button is clicked
         * @param btn the button clicked
         */
        @Override
		public void onPress(Button btn) {
			if(!(btn instanceof ButtonJob))
                return;
            ButtonJob b = (ButtonJob)btn;
			Minecraft.getInstance().setScreen(new GuiJobInfos(b.job));
		}
    	
    }
}
