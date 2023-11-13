package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.lwjgl.opengl.GL11;

public class ButtonArrow extends Button {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    private final boolean isUp;
    private final MainJobsMenu gui;

    /**
     * Creates a button to move up or down the Main Jobs Menu
     * @param posX the x coordinate
     * @param posY the y coordinate
     * @param gui the Main Jobs Menu GUI
     * @param isUp sets the direction of the arrow to be up or down
     */
    public ButtonArrow(int posX, int posY, MainJobsMenu gui, boolean isUp) {
        super(posX, posY, 17, 10, TextUtil.EMPTY, new OnPressed());
        this.gui = gui;
        this.isUp = isUp;
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
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
            int x = this.isHovered() ? 17 : 0;
            int y = isUp ? 220 : 230;
            GuiUtil.drawTexture(mStack, this, this.x, this.y, x, y, 17, 10);
        }
    }

    public static class OnPressed implements IPressable{

        /**
         * Scrolls the Main Jobs Menu up or down when the button is clicked, depending on the button's direction
         * @param btn the button clicked
         */
        @Override
        public void onPress(Button btn) {
            if(!(btn instanceof ButtonArrow))
                return;
            ButtonArrow b = (ButtonArrow) btn;
            double direction = b.isUp ? 1 : -1;
            b.gui.mouseScrolled(0, 0, direction);
        }
    }
}
