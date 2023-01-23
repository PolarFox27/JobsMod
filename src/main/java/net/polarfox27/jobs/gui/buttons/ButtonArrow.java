package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.MainJobsMenu;
import net.polarfox27.jobs.util.GuiUtil;
import org.lwjgl.opengl.GL11;

public class ButtonArrow extends Button {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/main_menu.png");

    private final boolean isUp;
    private final MainJobsMenu gui;

    public ButtonArrow(int posX, int posY, MainJobsMenu gui, boolean isUp) {
        super(posX, posY, 17, 10, new StringTextComponent(""), new OnPressed());
        this.gui = gui;
        this.isUp = isUp;
    }

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
