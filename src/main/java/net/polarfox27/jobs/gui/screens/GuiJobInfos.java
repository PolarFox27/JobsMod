package net.polarfox27.jobs.gui.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.UnlockStack;
import net.polarfox27.jobs.gui.buttons.ButtonBack;
import net.polarfox27.jobs.gui.buttons.ButtonXPCategory;
import net.polarfox27.jobs.gui.buttons.SlideBarButton;
import net.polarfox27.jobs.util.GuiUtil;
import net.polarfox27.jobs.util.JobsUtil;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuiJobInfos extends Screen implements SliderParent{

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_job_infos.png");
    private static final ResourceLocation UNLOCK_BACKGROUND = new ResourceLocation(ModJobs.MOD_ID, "textures/gui/gui_unlocked_stacks.png");
    public final String job;
    public int left;
    public int top;
    public int offsetUnlock;
    private final ItemRenderer renderItem;
    public int page;
    private final List<UnlockStack> unlocked_stacks;

    private SlideBarButton slidebar;
    public GuiJobInfos(String job) {
    	super(new StringTextComponent(""));
        this.job = job;
        this.offsetUnlock = 0;
        this.renderItem = Minecraft.getInstance().getItemRenderer();
        this.page = 0;
        this.unlocked_stacks = ClientJobsData.getUnlockedStacksSorted(this.job);
    }

    @Override
    public void init() {
        this.slidebar = new SlideBarButton(this.top + 30, this.top+135, this.width/2 + 93, this, true);
        this.buttons.clear();
        this.children.clear();
        this.addButton(new ButtonBack(this.width/2 - 105 + offsetUnlock, this.height/2-85, this));
        this.addButton(new ButtonXPCategory(this.width/2 - 84 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.XP, this));
        this.addButton(new ButtonXPCategory(this.width/2 + 4 + offsetUnlock, this.height/2 + 57, ButtonXPCategory.Type.UNLOCK, this));

        if(offsetUnlock == -70) {
            this.addButton(slidebar);
        }
        super.init();
    }
    
    @Override
    public boolean isPauseScreen() {
    	return false;
    }

    
    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
    	super.render(mStack, mouseX, mouseY, partialTicks);
    	this.left = this.width/2 - 110 + offsetUnlock;
        this.top = this.height/2 - 90;
        Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.blit(mStack, this.left, this.top, 0, 0, 220, 180);//background
        this.drawGradients(mStack);

        if(offsetUnlock != 0) {
            Minecraft.getInstance().getTextureManager().bind(UNLOCK_BACKGROUND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.blit(mStack, this.left + 225, this.top + 14, 0, 0, 70, 152);
        }
        super.render(mStack, mouseX, mouseY, partialTicks);
        if(offsetUnlock != 0)
            this.drawUnlockedItems(mStack, mouseX, mouseY);

        if(isDragging(slidebar, mouseX, mouseY))
            updateSlider(slidebar, mouseY);
    }

    private void drawUnlockedItems(MatrixStack mStack, int mouseX, int mouseY) {
        RenderHelper.setupForFlatItems();
        int renderIndex = -1;
        for(int i = 0; i < (unlocked_stacks.size() >= 7 ? 7 : unlocked_stacks.size() - page); i++) {
            renderItem.renderGuiItem(unlocked_stacks.get(i + page).getStack(), this.left + 242, this.top + 27 + i*18);
            if(mouseX >= this.left + 242      && mouseX < this.left + 258 &&
               mouseY >= this.top + 27 + i*18 && mouseY < this.top + 27 + 16 + i*18)
                renderIndex = i;
        }
        if(renderIndex != -1) renderToolTip(mStack, getItemToolTip(unlocked_stacks.get(renderIndex + page)), mouseX, mouseY,
                Minecraft.getInstance().font);
        RenderHelper.setupFor3DItems();
    }

    private void drawGradients(MatrixStack mStack) {
        int lvl = ClientJobsData.playerJobs.getLevelByJob(job);
        boolean isMaxLevel = ClientJobsData.playerJobs.isMax(job);
        GuiUtil.renderCenteredString(mStack, ClientJobsData.getJobName(job), Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top+15, 1.5f);
        GuiUtil.drawJobIcon(mStack, this, this.job, this.width/2 + offsetUnlock, this.top + 48, 48);

        String title1 = I18n.get("text.level") + " " + lvl;
        long progress1 = ClientJobsData.playerJobs.getXPByJob(job);
        long total1 = ClientJobsData.JOBS_LEVELS.getXPForLevel(job, lvl+1);
        GuiUtil.renderProgressBar(mStack, this, this.width/2 - 75 + offsetUnlock, this.top + 90, 150, 12, progress1, total1);
        GuiUtil.renderCenteredString(mStack, title1, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 86, 1.0f);

        if(isMaxLevel)
            return;

        long progress2 = ClientJobsData.playerJobs.getTotalXPByJob(job);
        long total2 = ClientJobsData.JOBS_LEVELS.getTotalXPForJob(job);
        String title2 = lvl < ClientJobsData.JOBS_LEVELS.getMaxLevel(job) ? I18n.get("text.total_progression") : "";
        GuiUtil.renderProgressBar(mStack, this, this.width / 2 - 75 + offsetUnlock, this.top + 115, 150, 12, progress2, total2);
        GuiUtil.renderCenteredString(mStack, title2, Color.BLACK.getRGB(), this.width/2 + offsetUnlock, this.top + 111, 1.0f);
    }


    public List<IReorderingProcessor> getItemToolTip(UnlockStack stack) {
        List<IReorderingProcessor> tooltip = new ArrayList<>();
        tooltip.add(new StringTextComponent(stack.getStack().getDisplayName().getString()
                                                                  .replace("[", "")
                                                                  .replace("]", ""))
                                                                  .getVisualOrderText());
        if(stack.getLevel() > ClientJobsData.playerJobs.getLevelByJob(this.job)) {
            tooltip.add(new StringTextComponent(
                    TextFormatting.RED  + I18n.get("text.unlock_" + stack.getType() + "_lvl")
                            + " " + stack.getLevel())
                    .getVisualOrderText());
        }
        else
            tooltip.add(new StringTextComponent(
                    TextFormatting.GREEN  + I18n.get("text.unlock_" + stack.getType()))
                    .getVisualOrderText());
        return tooltip;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double direction) {
    	if (direction != 0 && offsetUnlock == -70) {
            int x = -1 * Integer.signum((int)direction);
            setPage(true, JobsUtil.clamp(this.page + x, 0, getLastPage(true)));
            this.slidebar.update();
        }
    	return true;
    }

    @Override
    public boolean isDragging(SlideBarButton btn, int mouseX, int mouseY){
        return isDragging() &&
                mouseX >= this.width/2 + offsetUnlock + 163 && mouseX <= this.width/2 + offsetUnlock + 175 &&
                mouseY >= this.top + 30 && mouseY <= this.top + 135;
    }

    @Override
    public void updateSlider(SlideBarButton btn, int mouseY){
        int y = JobsUtil.clamp(mouseY - 30 - this.top, 0, 105);
        setPage(true, (int)Math.round((y/105.0d)*(double)getLastPage(true)));
        btn.update();
    }

    @Override
    public int getPage(boolean isVertical) {
        return page;
    }

    @Override
    public int getLastPage(boolean isVertical) {
        return unlocked_stacks.size() <= 7 ? 0 : unlocked_stacks.size() - 7;
    }

    @Override
    public void setPage(boolean isVertical, int page) {
        this.page = JobsUtil.clamp(page, 0, getLastPage(true));
    }
}
