package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.GuiUtil;

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
        super(posX, posY, 17, 10, new TextComponent(""), new OnPressed());
        this.gui = gui;
        this.isUp = isUp;
    }

    /**
     * Renders the widget on the screen
     * @param mStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void renderButton(PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, BACKGROUND);
            int x = this.isHoveredOrFocused() ? 17 : 0;
            int y = isUp ? 220 : 230;
            GuiUtil.drawTexture(mStack, gui, this.x, this.y, x, y, 17, 10);
        }
    }

    public static class OnPressed implements OnPress{

        /**
         * Scrolls the Main Jobs Menu up or down when the button is clicked, depending on the button's direction
         * @param btn the button clicked
         */
        @Override
        public void onPress(Button btn) {
            if(!(btn instanceof ButtonArrow b))
                return;
            double direction = b.isUp ? 1 : -1;
            b.gui.mouseScrolled(0, 0, direction);
        }
    }
}
