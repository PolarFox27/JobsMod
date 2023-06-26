package net.polarfox27.jobs.gui.buttons;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import net.polarfox27.jobs.util.GuiUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

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
        super(x, y, 80, 16, Component.literal(""), new OnPressed());
        this.type = type;
        this.xTexStart = type == Type.XP ? 0 : 16;
        this.yTexStart = 196;
        this.parent = parent;
    }

    /**
     * Renders the widget on the screen
     * @param mStack the render stack
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the rendering ticks
     */
    public void renderButton(@NotNull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, BACKGROUND);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            if(hovered)
                RenderSystem.setShaderColor(0.8F, 0.8F, 0.8F, 1.0F);
            else
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            this.blit(mStack, this.x, this.y, this.xTexStart, this.yTexStart, 16, 16);
            String name = "category." + type.name().toLowerCase();
            String txt = GuiUtil.translate(name);
            int txtWidth = Minecraft.getInstance().font.width(txt);
            Minecraft.getInstance().font.draw(mStack, txt, this.x + 48 - txtWidth/2.0F, this.y + 5, Color.BLACK.getRGB());
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
