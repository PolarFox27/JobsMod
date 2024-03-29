package net.polarfox27.jobs.gui.screens;

import net.polarfox27.jobs.gui.buttons.SlideBarButton;

public interface SliderParent {

    /**
     * @param isVertical the orientation of the slide bar
     * @return the current page of the slider of the given orientation
     */
    int getPage(boolean isVertical);

    /**
     * @param isVertical the orientation of the slide bar
     * @return the last page of the slider of the given orientation
     */
    int getLastPage(boolean isVertical);

    /**
     * Sets the slider of the given orientation to a specific page
     * @param isVertical the orientation of the slide bar
     * @param page the new page
     */
    void setPage(boolean isVertical, int page);

    /**
     * Updates the slider based on its current position
     * @param slideBar the slide bar button
     * @param position the new position of the slide bar on its main axis
     */
    void updateSlider(SlideBarButton slideBar, int position);

    /**
     * Checks if the player is dragging the slider
     * @param slideBar the slide bar button
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return true if the slide bar is dragged
     */
    boolean isDragging(SlideBarButton slideBar, int mouseX, int mouseY);
}
