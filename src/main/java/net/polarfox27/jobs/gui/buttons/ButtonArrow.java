package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

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
        super(posX, posY, 17, 10, TextUtil.EMPTY, new OnPressed(), TextUtil.BUTTON_NARRATOR);
        this.gui = gui;
        this.isUp = isUp;
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
            RenderSystem.setShaderTexture(0, BACKGROUND);
            int x = this.isHoveredOrFocused() ? 17 : 0;
            int y = isUp ? 220 : 230;
            GuiUtil.drawTexture(gui, BACKGROUND, this.getX(), this.getY(), x, y, 17, 10);
        }
    }

    public static class OnPressed implements OnPress{

        /**
         * Scrolls the Main Jobs Menu up or down when the button is clicked, depending on the button's direction
         * @param btn the button clicked
         */
        @Override
        public void onPress(@NotNull Button btn) {
            if(!(btn instanceof ButtonArrow b))
                return;
            double direction = b.isUp ? 1 : -1;
            b.gui.mouseScrolled(0, 0, direction);
        }
    }
}
