package net.polarfox27.jobs.gui.buttons;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.screens.SliderParent;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import net.polarfox27.jobs.util.TextUtil;
import org.jetbrains.annotations.NotNull;

public class SlideBarButton extends Button {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModJobs.MOD_ID + ":textures/gui/gui_how_xp.png");
    private final SliderParent parent;

    public final boolean isVertical;
    public final int startPos;
    public final int endPos;


    /**
     * Creates a slide bar
     * @param startPos the first position of the slide bar on its main axis
     * @param endPos the last position of the slide bar on its main axis
     * @param otherPos the position of the slide bar on its second axis
     * @param parent the parent GUI
     * @param isVertical sets the main axis to be the Y-Axis if this is true, the X-Axis otherwise
     */
    public SlideBarButton(int startPos, int endPos, int otherPos, SliderParent parent, boolean isVertical) {
        super((isVertical ? otherPos : startPos), (isVertical ? startPos : otherPos),
                (isVertical ? 12 : 15), (isVertical ? 15 : 12),
                TextUtil.EMPTY, new OnPressed(), TextUtil.BUTTON_NARRATOR);
        this.parent = parent;
        this.isVertical = isVertical;
        this.endPos = endPos;
        this.startPos = startPos;
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
            boolean disabled = this.parent.getLastPage(isVertical) == 0;

            int textY = 0;
            int textX = isVertical ? 177 : 189;
            if(disabled)
                textY += this.isVertical ? 15 : 12;
            int width = isVertical ? 12 : 15;
            int height = isVertical ? 15 : 12;
            GuiUtil.drawTexture(gui, TEXTURE, this.getX(), this.getY(), textX, textY, width, height); //Slide bar
        }
    }

    /**
     * Updates the position of the slide bar across its axis
     * @param pos the new position (clamped between startPos and endPos)
     */
    private void updatePos(int pos){
        int p = JobsUtil.clamp(pos, startPos, endPos);
        if(isVertical)
            this.setY(p);
        else
            this.setX(p);
    }

    /**
     * Updates the position of the slide bar across its axis based on the page the parent is at
     */
    public void update(){
        float progress = (float)parent.getPage(isVertical)/(float)parent.getLastPage(isVertical);
        int pos = startPos + (int)((endPos-startPos)*progress);
        updatePos(pos);
    }

    public static class OnPressed implements OnPress{

        /**
         * Does nothing when pressed
         * @param btn the button pressed
         */
        @Override
		public void onPress(@NotNull Button btn) {}
    }
}

