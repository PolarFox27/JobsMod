package net.polarfox27.jobs.gui.screens;


import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;
import net.polarfox27.jobs.gui.buttons.ButtonBack;
import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.jetbrains.annotations.NotNull;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiHowXP extends Screen implements SliderParent {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_how_xp.png");
    public int left;
    public int top;
    public final String job;
    public int verticalPage = 0;
    public int horizontalPage = 0;
    private SlideBarButton verticalSlideBar;
    private SlideBarButton horizontalSlideBar;
    private final List<XPRegistry<? extends XPData>> categories = new ArrayList<>();
    private final List<List<XPData>> xpLists = new ArrayList<>();
    private final List<Component> tooltip = new ArrayList<>();

    /**
     * Creates the How XP GUI for a job
     * @param j the job
     */
    public GuiHowXP(String j) {
    	super(new TextComponent(""));
        this.job = j;
        for(XPRegistry<? extends XPData> c : ClientJobsData.XP_REGISTRIES) {
        	if(!c.getXPDataByJob(job).isEmpty()) {
        		this.categories.add(c);
        	}
        }
        this.categories.sort(Comparator.comparing(XPRegistry::getName));

        for (XPRegistry<? extends XPData> category : this.categories) {
            List<XPData> stacks = ClientJobsData.getOrderedXPFromRegistry(job,
                    ClientJobsData.playerJobs.getLevelByJob(job),
                    category);
            this.xpLists.add(stacks);
        }
    }

    /**
     * creates the 2 sliders (1 vertical and 1 optional horizontal)
     */
    @Override
    public void init() {
        this.verticalSlideBar = new SlideBarButton(this.height/2 -45, this.height/2+45, this.width/2 + 65, this, true);
        this.horizontalSlideBar = new SlideBarButton(this.width/2-73, this.width/2+44, this.height/2+66, this, false);
        this.clearWidgets();
        this.addRenderableWidget(new ButtonBack(this.width/2 - 83, this.height/2-70, this));
        this.addRenderableWidget(verticalSlideBar);
        if(getLastPage(false) > 0)
            this.addRenderableWidget(horizontalSlideBar);
    }

    /**
     * @return false, this GUI doesn't pause the game
     */
    @Override
    public boolean isPauseScreen() 
    {
    	return false;
    }

    /**
     * Renders the GUI on the screen
     * @param mStack the render stack
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param partialTicks the rendering ticks
     */
    @Override
    public void render(@NotNull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.tooltip.clear();
        this.left = this.width/2 - 88;
        this.top = this.height/2 - 75;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        this.blit(mStack, this.left, this.top, 0, 0, 176, 150);//background
        if(getLastPage(false) > 0)
            this.blit(mStack, this.left, this.top + 140, 0, 150, 176, 24);

        super.render(mStack, mouseX, mouseY, partialTicks);
        if(xpLists.stream().anyMatch(x -> !x.isEmpty())){
            this.drawCategories(mStack, mouseX, mouseY);
            this.drawCategoriesStacks(mouseX, mouseY);
        }
        else{
            GuiUtil.renderCenteredString(mStack, I18n.get("text.no_way_of_gaining_xp"), Color.RED.getRGB(), this.width/2-10, this.height/2, 0.65f);
            return;
        }

        if(!tooltip.isEmpty())
            this.renderComponentTooltip(mStack, tooltip, mouseX, mouseY, Minecraft.getInstance().font);

        if(isDragging(verticalSlideBar, mouseX, mouseY))
            updateSlider(verticalSlideBar, mouseY);
        if(isDragging(horizontalSlideBar, mouseX, mouseY))
            updateSlider(horizontalSlideBar, mouseX);
    }

    /**
     * Renders the XP Categories on the screen
     * @param mStack the render stack
     * @param mouseX the x coordinate of the mouse, used to find the tooltips to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltips to render
     */
    private void drawCategories(PoseStack mStack, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        int size = 30*Math.min(this.categories.size(), 5) + Math.min(this.categories.size()-1, 4);
        int x = this.width/2 - 10 - size/2;
        int renderIndex = -1;
        for(int i = 0; i < Math.min(this.categories.size(), 5); i++) {
            this.itemRenderer.renderGuiItem(new ItemStack(this.categories.get(i+horizontalPage).getIcon()), x + i*31 + 7, this.top + 20);
            if(mouseX >= x + i*31 + 7 && mouseX < x + i*31 + 23 && mouseY >= this.top + 20 && mouseY < this.top + 36)
                renderIndex = i;
            if(i < Math.min(this.categories.size()-1, 4) && this.categories.size() > 1) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, BACKGROUND);
                this.blit(mStack, x + i * 31 + 30, this.top + 38, 176, 0, 1, 100);
            }
        }
        if(renderIndex != -1)
            this.tooltip.add(new TranslatableComponent("category." + this.categories.get(renderIndex).getName()));
    }

    /**
     * Renders the stacks for the XP Categories on the screen
     * @param mouseX the x coordinate of the mouse, used to find the tooltips to render
     * @param mouseY the y coordinate of the mouse, used to find the tooltips to render
     */
    private void drawCategoriesStacks(int mouseX, int mouseY) {
        int size = 30*Math.min(this.categories.size(), 5) + Math.min(this.categories.size()-1, 4);
        int p = this.width/2 - 3 - size/2;
        for(int i = 0; i < Math.min(this.categories.size(), 5); i++) {
            int x = p + i*31;
            List<XPData> xps = this.xpLists.get(horizontalPage + i);

            int hoveredIndex = -1;
            for(int j = this.verticalPage; j < xps.size() && j < this.verticalPage+5 && j >= 0; j++) {
                int y = this.top + 45 + (j - this.verticalPage)*18;
                ItemStack stack = xps.get(j).createStack();
                if(stack.getItem() == Items.BARRIER && stack.hasTag() && Minecraft.getInstance().level != null) {
                    EntityType<?> type = EntityType.byString(stack.getOrCreateTag().getString("entity"))
                            .orElse(null);
                    if(type == null)
                        continue;
                    Entity entity = type == EntityType.PLAYER ? Minecraft.getInstance().player :
                            type.create(Minecraft.getInstance().level);

                     if(entity instanceof LivingEntity)
                            GuiUtil.renderEntityInGui(x + 8, y + 16, EntityType.COW,
                                    this.width/2.0F - mouseX, this.height/2.0F - mouseY,
                                    (LivingEntity) entity);
                }
                else
                    this.itemRenderer.renderGuiItem(stack, x, y);

                if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16)
                    hoveredIndex = j;
            }
            if(hoveredIndex != -1) {
                XPData data = xps.get(hoveredIndex);
                if(data instanceof XPData.EntityXPData)
                    tooltip.add(new TextComponent(((XPData.EntityXPData)data).getEntityName()));
                else
                    tooltip.add(data.createStack().getHoverName());
                long xp;
                int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
                if (lvl < ClientJobsData.JOBS_LEVELS.getMaxLevel(job)) {
                    xp =  data.getXPByLevel(lvl);
                    if(xp != 0L)
                        tooltip.add(GuiUtil.coloredComponent(ChatFormatting.GREEN, new TranslatableComponent("text.xp", xp)));
                    else {
                        int unlockLevel = data.unlockingLevel(lvl);
                        if(unlockLevel > 0)
                            tooltip.add(GuiUtil.coloredComponent(ChatFormatting.RED,
                                    new TranslatableComponent("text.unlock_xp_lvl", unlockLevel)));
                        else
                            tooltip.add(GuiUtil.coloredComponent(ChatFormatting.RED,
                                    new TranslatableComponent("text.xp",  0)));
                    }
                }
                else
                    tooltip.add(GuiUtil.coloredComponent(ChatFormatting.DARK_PURPLE,
                            new TranslatableComponent("text.xp",  0)));
            }
        }
    }

    /**
     * Updates the vertical page when the mouse is scrolled
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @param direction the scroll direction
     * @return true
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        if (direction != 0) {
            int x = -1 * Integer.signum((int)direction);
            setPage(true, JobsUtil.clamp(this.verticalPage + x, 0, getLastPage(true)));
            this.verticalSlideBar.update();
        }
        return true;
    }

    /**
     * Checks if the player is dragging the slider
     * @param btn the slide bar button
     * @param mouseX the x coordinate of the mouse
     * @param mouseY the y coordinate of the mouse
     * @return true if the slide bar is dragged
     */
    @Override
    public boolean isDragging(SlideBarButton btn, int mouseX, int mouseY){
        return isDragging() && (
                (mouseX >= this.width/2 + 65 && mouseX <= this.width/2 + 77 &&
                mouseY >= this.top + 30 && mouseY <= this.top + 135 &&
                btn.isVertical) ||
                (mouseX >= this.width/2 -73 && mouseX <= this.width/2 + 44 &&
                mouseY >= this.height/2 + 66 && mouseY <= this.height/2 + 78 &&
                !btn.isVertical));
    }

    /**
     * Updates the slider based on its current position
     * @param btn the slide bar button
     * @param mousePos the position of mouse on its main axis
     */
    @Override
    public void updateSlider(SlideBarButton btn, int mousePos){
        int page;
        if(btn.isVertical){
            int y = JobsUtil.clamp(mousePos - 30 - this.top, 0, 105);
            page = (int)Math.round((y/105.0d)*(double)getLastPage(true));
        }
        else{
            int x = JobsUtil.clamp(mousePos - (this.width/2 - 73), 0, 117);
            page = (int)Math.round((x/117.0d)*(double)getLastPage(false));
        }
        setPage(btn.isVertical, page);
        btn.update();
    }

    /**
     * @param isVertical the orientation of the slide bar
     * @return the current page of the slider of the given orientation
     */
    @Override
    public int getPage(boolean isVertical) {
        return isVertical ? verticalPage : horizontalPage;
    }

    /**
     * @param isVertical the orientation of the slide bar
     * @return the last page of the slider of the given orientation
     */
    @Override
    public int getLastPage(boolean isVertical) {
        if(isVertical){
            int size = xpLists.stream()
                    .skip(horizontalPage)
                    .limit(5)
                    .max(Comparator.comparingInt(List::size))
                    .map(List::size)
                    .orElse(0);
            return size <= 5 ? 0 : size - 5;
        }
        else
            return categories.size() <= 5 ? 0 : categories.size() - 5;
    }

    /**
     * Sets the slider of the given orientation to a specific page
     * @param isVertical the orientation of the slide bar
     * @param page the new page
     */
    @Override
    public void setPage(boolean isVertical, int page) {
        if(isVertical)
            this.verticalPage = JobsUtil.clamp(page, 0, getLastPage(true));
        else {
            int prev = horizontalPage;
            this.horizontalPage = JobsUtil.clamp(page, 0, getLastPage(false));
            if(prev != horizontalPage){
                this.verticalPage = JobsUtil.clamp(this.verticalPage, 0, getLastPage(true));
                this.verticalSlideBar.update();
            }
        }
    }
}
