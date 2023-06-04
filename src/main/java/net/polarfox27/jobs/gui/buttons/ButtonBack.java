package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import org.lwjgl.opengl.GL11;

public class ButtonBack extends GuiButton {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffText;

    /**
     * Creates a back arrow button
     * @param id the button id
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public ButtonBack(int id, int x, int y) {
        super(id, x, y, 18, 10, "");
        this.xTexStart = 0;
        this.yTexStart = 212;
        this.xDiffText = 18;
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
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = hovered ? this.xDiffText + this.xTexStart : this.xTexStart;
            this.drawTexturedModalRect(this.x, this.y, i, yTexStart, this.width, this.height);
        }
    }
}
