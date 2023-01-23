package net.polarfox27.jobs.gui.screens;


import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.XPData;
import net.polarfox27.jobs.data.registry.XPRegistry;
import net.polarfox27.jobs.gui.buttons.ButtonBack;
import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GuiHowXP extends Screen implements SliderParent {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_how_xp.png");
    public int left;
    public int top;
    public final String job;
    public int verticalPage = 0;
    public int horizontalPage = 0;
    private SlideBarButton verticalSlidebar;
    private SlideBarButton horizontalSlidebar;
    private final List<XPRegistry<? extends XPData>> categories = new ArrayList<>();
    private final List<List<XPData>> xpLists = new ArrayList<>();
    private final List<IReorderingProcessor> tooltip = new ArrayList<>();

    public GuiHowXP(String j) {
    	super(new StringTextComponent(""));
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

    @Override
    public void init() {
        this.verticalSlidebar = new SlideBarButton(this.height/2 -45, this.height/2+45, this.width/2 + 65, this, true);
        this.horizontalSlidebar = new SlideBarButton(this.width/2-73, this.width/2+44, this.height/2+66, this, false);
        this.buttons.clear();
        this.children.clear();
        this.addButton(new ButtonBack(this.width/2 - 83, this.height/2-70, this));
        this.addButton(verticalSlidebar);
        if(getLastPage(false) > 0)
            this.addButton(horizontalSlidebar);
    }
    
    @Override
    public boolean isPauseScreen() 
    {
    	return false;
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.tooltip.clear();
        this.left = this.width/2 - 88;
        this.top = this.height/2 - 75;
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(mStack, this.left, this.top, 0, 0, 176, 150);//background
        if(getLastPage(false) > 0)
            this.blit(mStack, this.left, this.top + 140, 0, 150, 176, 24);


        super.render(mStack, mouseX, mouseY, partialTicks);
        if(xpLists.stream().anyMatch(x -> !x.isEmpty())){
            this.drawCategories(mStack, mouseX, mouseY);
            this.drawCategoriesStacks(mStack, mouseX, mouseY);
        }
        else{
            GuiUtil.renderCenteredString(mStack, I18n.get("text.no_way_of_gaining_xp"), Color.RED.getRGB(), this.width/2-10, this.height/2, 0.65f);
            return;
        }

        if(!tooltip.isEmpty())
            this.renderToolTip(mStack, tooltip, mouseX, mouseY, Minecraft.getInstance().font);

        if(isDragging(verticalSlidebar, mouseX, mouseY))
            updateSlider(verticalSlidebar, mouseY);
        if(isDragging(horizontalSlidebar, mouseX, mouseY))
            updateSlider(horizontalSlidebar, mouseX);
    }

    private void drawCategories(MatrixStack mStack, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
        int size = 30*Math.min(this.categories.size(), 5) + Math.min(this.categories.size()-1, 4);
        int x = this.width/2 - 10 - size/2;
        int renderIndex = -1;
        RenderHelper.setupForFlatItems();
        for(int i = 0; i < Math.min(this.categories.size(), 5); i++) {
            this.itemRenderer.renderGuiItem(new ItemStack(this.categories.get(i+horizontalPage).getIcon()), x + i*31 + 7, this.top + 20);
            if(mouseX >= x + i*31 + 7 && mouseX < x + i*31 + 23 && mouseY >= this.top + 20 && mouseY < this.top + 36)
                renderIndex = i;
            if(i < Math.min(this.categories.size()-1, 4) && this.categories.size() > 1) {
                Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
                this.blit(mStack, x + i * 31 + 30, this.top + 38, 176, 0, 1, 100);
            }
        }
        if(renderIndex != -1)
            this.tooltip.add(new StringTextComponent(I18n.get("category." + this.categories.get(renderIndex).getName())).getVisualOrderText());
    }
    
    private void drawCategoriesStacks(MatrixStack mStack, int mouseX, int mouseY) {
        int size = 30*Math.min(this.categories.size(), 5) + Math.min(this.categories.size()-1, 4);
        int p = this.width/2 - 3 - size/2;
        for(int i = 0; i < Math.min(this.categories.size(), 5); i++) {
            int x = p + i*31;
            List<XPData> xps = this.xpLists.get(horizontalPage + i);
            RenderHelper.setupForFlatItems();

            int hoveredIndex = -1;
            for(int j = this.verticalPage; j < xps.size() && j < this.verticalPage+5 && j >= 0; j++) {
                int y = this.top + 45 + (j - this.verticalPage)*18;
                ItemStack stack = xps.get(j).createStack();
                if(stack.getItem() == Items.BARRIER && stack.hasTag() && Minecraft.getInstance().level != null) {
                    EntityType.byString(stack.getOrCreateTag().getString("entity"))
                            .map(t -> t.create(Minecraft.getInstance().level))
                            .filter(e -> e instanceof LivingEntity)
                            .map(e -> (LivingEntity)e)
                            .ifPresent(e ->  GuiUtil.renderEntityInGui(x + 8, y + 16, 10,
                                    this.width/2.0F - mouseX, this.height/2.0F - mouseY,
                                    e));
                }
                else
                    this.itemRenderer.renderGuiItem(stack, x, y);

                if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16)
                    hoveredIndex = j;
            }
            if(hoveredIndex != -1) {
                XPData data = xps.get(hoveredIndex);
                if(data instanceof XPData.EntityXPData)
                    tooltip.add(new StringTextComponent(((XPData.EntityXPData)data).getEntityName()).getVisualOrderText());
                else
                    tooltip.add(new StringTextComponent(data.createStack().getDisplayName().getString()
                            .replace("[", "")
                            .replace("]", ""))
                            .getVisualOrderText());
                long xp = 0L;
                int lvl = ClientJobsData.playerJobs.getLevelByJob(this.job);
                if (lvl < ClientJobsData.JOBS_LEVELS.getMaxLevel(job)) {
                    xp =  data.getXPByLevel(lvl);
                    if(xp != 0L)
                        tooltip.add(new StringTextComponent(TextFormatting.GREEN + "" + xp + " xp").getVisualOrderText());
                    else {
                        int unlockLevel = data.unlockingLevel(lvl);
                        if(unlockLevel > 0)
                            tooltip.add(new StringTextComponent(TextFormatting.RED + I18n.get("text.unlock_xp_lvl") + " " + unlockLevel)
                                    .getVisualOrderText());
                        else
                            tooltip.add(new StringTextComponent(TextFormatting.RED + "0 xp")
                                    .getVisualOrderText());
                    }
                }
                else
                    tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "0 xp").getVisualOrderText());
            }
            RenderHelper.setupFor3DItems();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
        if (direction != 0) {
            int x = -1 * Integer.signum((int)direction);
            setPage(true, JobsUtil.clamp(this.verticalPage + x, 0, getLastPage(true)));
            this.verticalSlidebar.update();
        }
        return true;
    }

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

    @Override
    public int getPage(boolean isVertical) {
        return isVertical ? verticalPage : horizontalPage;
    }

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

    @Override
    public void setPage(boolean isVertical, int page) {
        if(isVertical)
            this.verticalPage = JobsUtil.clamp(page, 0, getLastPage(true));
        else {
            int prev = horizontalPage;
            this.horizontalPage = JobsUtil.clamp(page, 0, getLastPage(false));
            if(prev != horizontalPage){
                this.verticalPage = JobsUtil.clamp(this.verticalPage, 0, getLastPage(true));
                this.verticalSlidebar.update();
            }
        }
    }
}
