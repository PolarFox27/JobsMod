package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.TextUtil;
import org.lwjgl.opengl.GL11;

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
        super(x, y, 18, 10, TextUtil.EMPTY, new OnPressed());
        this.xTexStart = 0;
        this.yTexStart = 212;
        this.xDiffText = 18;
        this.parent = parent;
    }

    /**
     * Renders the widget on the screen
     * @param mStack the render stack
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    @Override
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = hovered ? this.xDiffText + this.xTexStart : this.xTexStart;
            this.blit(mStack, this.x, this.y, i, yTexStart, this.width, this.height);
        }
    }
    
    public static class OnPressed implements IPressable{

        /**
         * Goes back to the previous GUI when the button is clicked
         * @param btn the button clicked
         */
        @Override
		public void onPress(Button btn) {
			if(!(btn instanceof ButtonBack))
                return;
			ButtonBack button = (ButtonBack)btn;
			if(button.parent instanceof GuiJobInfos)
				Minecraft.getInstance().setScreen(new MainJobsMenu());
			if(button.parent instanceof GuiHowXP)
				Minecraft.getInstance().setScreen(new GuiJobInfos(((GuiHowXP)button.parent).job));
		}
    	
    }
}
