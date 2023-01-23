package net.polarfox27.jobs.gui.screens;

import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.JobsUtil;

public interface SliderParent {

    public int getPage(boolean isVertical);
    public int getLastPage(boolean isVertical);
    public void setPage(boolean isVertical, int page);

    public void updateSlider(SlideBarButton slidebar, int position);

    public boolean isDragging(SlideBarButton slidebar, int mouseX, int mouseY);
}
