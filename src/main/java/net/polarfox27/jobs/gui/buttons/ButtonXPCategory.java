package net.polarfox27.jobs.gui.buttons;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

public class ButtonXPCategory extends GuiButton {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private final int xTexStart;
    private final int yTexStart;
    private final Type type;
    private final GuiJobInfos parent;

    /**
     * Creates the button
     * @param id the button id
     * @param parent the parent screen
     * @param x the x coordinate
     * @param y the y coordinate
     * @param type the category type (XP / UNLOCK)
     */
    public ButtonXPCategory(int id, GuiJobInfos parent, int x, int y, Type type) {
        super(id, x, y, 80, 16, "");
        this.parent = parent;
        this.type = type;
        this.xTexStart = type == Type.XP ? 0 : 16;
        this.yTexStart = 196;
    }

    /**
     * Renders the widget on the screen
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the render ticks
     */
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);

            if(hovered) GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
            else GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexturedModalRect(this.x, this.y, xTexStart, yTexStart, 16, 16);
            String name = "category." + type.name().toLowerCase();
            String txt = I18n.format(name);
            int txtWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(txt);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            Minecraft.getMinecraft().fontRenderer.drawString(txt, this.x + 48 - txtWidth/2, this.y + 5, Color.BLACK.getRGB());
        }
    }

    /**
     * Opens the How XP GUI or shows the Blocked Stacks when a button is clicked, depending on its type
     */
    public void onPress() {
        switch(type){
            case XP:
                Minecraft.getMinecraft().displayGuiScreen(new GuiHowXP(parent.job));
            case UNLOCK:
                this.parent.offsetUnlock = this.parent.offsetUnlock == 0 ? -70 : 0;
                this.parent.initGui();
        }
    }


    public enum Type{
        XP,
        UNLOCK
    }
}
