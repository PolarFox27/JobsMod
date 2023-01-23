package net.polarfox27.jobs.gui.buttons;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.SliderParent;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.opengl.GL11;

public class SlideBarButton extends Button {

    private final ResourceLocation texture = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_how_xp.png");
    private final SliderParent parent;

    public final boolean isVertical;
    public final int startPos;
    public final int endPos;

    public SlideBarButton(int startPos, int endPos, int otherPos, SliderParent parent, boolean isVertical) {
        super((isVertical ? otherPos : startPos), (isVertical ? startPos : otherPos),
                (isVertical ? 12 : 15), (isVertical ? 15 : 12),
                new StringTextComponent(""), new OnPressed());
        this.parent = parent;
        this.isVertical = isVertical;
        this.endPos = endPos;
        this.startPos = startPos;
    }

    @Override
    public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            boolean disabled = this.parent.getLastPage(isVertical) == 0;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bind(this.texture);

            int textY = 0;
            int textX = isVertical ? 177 : 189;
            if(disabled)
                textY += this.isVertical ? 15 : 12;
            int width = isVertical ? 12 : 15;
            int height = isVertical ? 15 : 12;
            this.blit(mStack, this.x, this.y, textX, textY, width, height); //Slide bar
        }
    }

    private void updatePos(int pos){
        int p = JobsUtil.clamp(pos, startPos, endPos);
        if(isVertical)
            this.y = p;
        else
            this.x = p;
    }

    public void update(){
        float progress = (float)parent.getPage(isVertical)/(float)parent.getLastPage(isVertical);
        int pos = startPos + (int)((endPos-startPos)*progress);
        updatePos(pos);
    }

    public void update(int mousePos){
        int pos = JobsUtil.clamp(mousePos - startPos, 0, endPos-startPos);
        updatePos(startPos+pos);
        int page = (int)(getProgress()*parent.getLastPage(isVertical));
        parent.setPage(isVertical, page);
    }

    public float getProgress(){
        int pos = isVertical ? y : x;
        return ((float) (pos - startPos)/ (float) (endPos-startPos));
    }

    public static class OnPressed implements IPressable{
    	@Override
		public void onPress(Button btn) {}    	
    }
}

