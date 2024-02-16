package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

public class ButtonBack extends Button {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffText;
    private final Screen parent;

    /**
     * Creates a back arrow button
     * @param x the x coordinate
     * @param y the y coordinate
     * @param parent the parent GUI
     */
    public ButtonBack(int x, int y, Screen parent) {
        super(x, y, 18, 10, TextUtil.EMPTY, new OnPressed(), TextUtil.BUTTON_NARRATOR);
        this.xTexStart = 0;
        this.yTexStart = 212;
        this.xDiffText = 18;
        this.parent = parent;
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
            int i = this.xTexStart;
            if (this.isHovered)
                i += this.xDiffText;
            GuiUtil.drawTexture(gui, BACKGROUND, this.getX(), this.getY(), i, this.yTexStart, this.width, this.height);
        }
    }
    
    public static class OnPressed implements OnPress{

        /**
         * Goes back to the previous GUI when the button is clicked
         * @param btn the button clicked
         */
        @Override
		public void onPress(@NotNull Button btn) {
			if(!(btn instanceof ButtonBack button))
                return;
            if(button.parent instanceof GuiJobInfos)
				Minecraft.getInstance().setScreen(new MainJobsMenu());
			if(button.parent instanceof GuiHowXP)
				Minecraft.getInstance().setScreen(new GuiJobInfos(((GuiHowXP)button.parent).job));
		}
    	
    }
}
