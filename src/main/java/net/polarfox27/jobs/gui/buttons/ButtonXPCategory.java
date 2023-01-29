package net.polarfox27.jobs.gui.buttons;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.GuiHowXP;
import net.polarfox27.jobs.gui.screens.GuiJobInfos;
import org.lwjgl.opengl.GL11;

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
        super(x, y, 80, 16, new StringTextComponent(""), new OnPressed());
        this.type = type;
        this.xTexStart = type == Type.XP ? 0 : 16;
        this.yTexStart = 196;
        this.parent = parent;
    }

    /**
     * Renders the widget on the screen
     * @param mStack
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
    	if (this.visible) {
            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
            int i = this.xTexStart;
            int j = this.yTexStart;

            if(hovered) GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
            else GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.blit(mStack, this.x, this.y, i, j, 16, 16);
            String name = "category." + type.name().toLowerCase();
            String txt = I18n.get(name);
            int txtWidth = Minecraft.getInstance().font.width(txt);
            Minecraft.getInstance().font.draw(mStack, txt, this.x + 48 - txtWidth/2.0F, this.y + 5, Color.BLACK.getRGB());
        }
    }
    
    public static class OnPressed implements IPressable{

        /**
         * Opens the How XP GUI or shows the Blocked Stacks when a button is clicked, depending on its type
         * @param btn the button clicked
         */
        @Override
		public void onPress(Button btn) {
			if(!(btn instanceof ButtonXPCategory))
                return;
			ButtonXPCategory button = (ButtonXPCategory)btn;
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
        UNLOCK;
    }
}
