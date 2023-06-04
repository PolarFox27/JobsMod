package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import org.lwjgl.opengl.GL11;

public class ButtonArrow extends GuiButton {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    private final boolean isUp;
    private final MainJobsMenu gui;

    /**
     * Creates a button to move up or down the Main Jobs Menu
     * @param id the button id
     * @param posX the x coordinate
     * @param posY the y coordinate
     * @param gui the Main Jobs Menu GUI
     * @param isUp sets the direction of the arrow to be up or down
     */
    public ButtonArrow(int id, int posX, int posY, MainJobsMenu gui, boolean isUp) {
        super(id, posX, posY, 17, 10, "");
        this.gui = gui;
        this.isUp = isUp;
    }

    /**
     * Renders the widget on the screen
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
            int x = this.hovered ? 17 : 0;
            int y = isUp ? 220 : 230;
            this.drawTexturedModalRect(this.x, this.y, x, y, 17, 10);
        }
    }

    /**
     * Scrolls the Main Jobs Menu up or down when the button is clicked, depending on the button's direction
     */
    public void onPress() {
        double direction = isUp ? 1 : -1;
        gui.mouseScrolled(0, 0, direction);
    }
}
