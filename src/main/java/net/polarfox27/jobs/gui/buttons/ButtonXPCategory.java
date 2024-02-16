package net.polarfox27.jobs.gui.buttons;


import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ButtonXPCategory extends Button {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private final int xTexStart;
    private final int yTexStart;
    private final Type type;
    private final GuiJobInfos parent;

    /**
     * Creates the button
     * @param x the x coordinate
     * @param y the y coordinate
     * @param type the category type (XP / UNLOCK)
     * @param parent the parent GUI
     */
    public ButtonXPCategory(int x, int y, Type type, GuiJobInfos parent) {
        super(x, y, 80, 16, TextUtil.EMPTY, new OnPressed(), TextUtil.BUTTON_NARRATOR);
        this.type = type;
        this.xTexStart = type == Type.XP ? 0 : 16;
        this.yTexStart = 196;
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
            if(this.isHovered)
                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
            else
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            GuiUtil.drawTexture(gui, BACKGROUND, this.getX(), this.getY(), this.xTexStart, this.yTexStart, 16, 16);
            String name = GuiUtil.translate("category." + type.name().toLowerCase());
            GuiUtil.renderString(gui, name, this.getX() + 20, this.getY() + 4, Color.BLACK.getRGB());
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
    
    public static class OnPressed implements OnPress{

        /**
         * Opens the How XP GUI or shows the Blocked Stacks when a button is clicked, depending on its type
         * @param btn the button clicked
         */
        @Override
		public void onPress(@NotNull Button btn) {
			if(!(btn instanceof ButtonXPCategory button))
                return;
            switch(button.type){
                case XP:
                    Minecraft.getInstance().setScreen(new GuiHowXP(button.parent.job));
                case UNLOCK:
                    button.parent.offsetUnlock = button.parent.offsetUnlock == 0 ? -70 : 0;
                    button.parent.init();
            }
		}
    	
    }


    public enum Type{
        XP,
        UNLOCK
    }
}
