package com.dorian2712.jobs.gui.screens;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.gui.buttons.ButtonBack;
import com.dorian2712.jobs.gui.buttons.ButtonXPCategory;
import com.dorian2712.jobs.gui.buttons.SlideBarUnlock;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.Constants.XPCategories;
import com.dorian2712.jobs.util.JobsMath;
import com.dorian2712.jobs.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class GuiJobInfos extends GuiScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_job_infos.png");
    private static final ResourceLocation ICONES = new ResourceLocation(Reference.MOD_ID, "textures/gui/jobs_icons.png");
    private static final ResourceLocation UNLOCK_BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_unlocked_items.png");
    private final Job job;
    public int left;
    public int top;
    private int offsetUnlock;
    private RenderItem renderItem;
    public int page;
    public final int pageNumber;
    private List<Item> unlocked_items = new ArrayList<>();
    public GuiJobInfos(Job job)
    {
        this.left = this.width/2 - 110;
        this.top = this.height/2 - 90;
        this.job = job;
        this.offsetUnlock = 0;
        this.renderItem = Minecraft.getMinecraft().getRenderItem();
        this.page = 0;
        this.unlocked_items = ClientInfos.getClassedUnlockedItems(this.job);
        this.pageNumber = unlocked_items.size() <= 7 ? 1 : unlocked_items.size() - 6;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.addButton(new ButtonBack(0, this.width/2 - 105 + offsetUnlock, this.height/2-85));
        this.addButton(new ButtonXPCategory(1, this.width/2 - 84 + offsetUnlock, this.height/2 + 40, XPCategories.XP));
        this.addButton(new ButtonXPCategory(2, this.width/2 + 4 + offsetUnlock, this.height/2 + 40, XPCategories.UNLOCK));

        if(offsetUnlock == -70)
        {
            this.addButton(new SlideBarUnlock(3, this.width/2 + offsetUnlock + 115 + 48, this.top + 14 + 16, this));
        }
        super.initGui();
    }
    
    @Override
    public boolean doesGuiPauseGame() 
    {
    	return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.left = this.width/2 - 110 + offsetUnlock;
        this.top = this.height/2 - 90;
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, 220, 180);//background
        this.drawGradients();

        if(offsetUnlock != 0)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(UNLOCK_BACKGROUND);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.left + 225, this.top + 14, 0, 0, 70, 152);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(offsetUnlock != 0)
            this.drawUnlockedItems(mouseX, mouseY);

    }

    private void drawUnlockedItems( int mouseX, int mouseY)
    {
        RenderHelper.disableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();
        int renderIndex = -1;
        for(int i = 0; i < (unlocked_items.size() >= 7 ? 7 : unlocked_items.size() - page); i++)
        {
            renderItem.renderItemAndEffectIntoGUI(new ItemStack(unlocked_items.get(i + page)), this.left + 242, this.top + 27 + i*18);
            if(mouseX >= this.left + 242      && mouseX < this.left + 258 &&
               mouseY >= this.top + 27 + i*18 && mouseY < this.top + 27 + 16 + i*18)
                renderIndex = i;
        }
        if(renderIndex != -1) renderToolTip(new ItemStack(unlocked_items.get(renderIndex + page)), mouseX, mouseY);
        RenderHelper.enableStandardItemLighting();
    }

    private void drawGradients()
    {
        int lvl = ClientInfos.job.getLevelByJob(job);
        Minecraft.getMinecraft().getTextureManager().bindTexture(ICONES);
        this.drawTexturedModalRect(this.width/2 - 20 + offsetUnlock, this.top + 7, 40*this.job.index, 0, 40, 40);//icon
        this.drawTexturedModalRect(this.width/2 - 20 + offsetUnlock, this.top + 7, 0, 40, 40, 40);//icon
        this.drawTexturedModalRect(this.width/2 - 75 + offsetUnlock, this.top + 65, 0, 80, 150, 12); //background1
        if(lvl < 25)
            this.drawTexturedModalRect(this.width/2 - 75 + offsetUnlock, this.top + 90, 0, 80, 150, 12);//background2

        int y = lvl < 25 ? 92 : 104;
        int size1 = lvl < 25 ? (int) (((double)ClientInfos.job.getXPByJob(job)/ (double)Constants.XP_BY_LEVEL[lvl + 1]) * 150) : 150;
        int size2 = lvl < 25 ? (int) (((double)ClientInfos.job.toTotalXPs()[job.index]/ (double)Constants.TOTAL_XP_BY_LEVEL[25]) * 150) : 0;
        String text1 = lvl < 25 ? ClientInfos.job.getXPByJob(job) + "/" + Constants.XP_BY_LEVEL[lvl + 1] : I18n.translateToLocal("text.level.max");;
        String text2 = lvl < 25 ? ClientInfos.job.toTotalXPs()[job.index] + "/" + Constants.TOTAL_XP_BY_LEVEL[25] : "";
        String title1 = I18n.translateToLocal("text.level") + " " + lvl;
        String title2 = lvl < 25 ? I18n.translateToLocal("text.total_progression") : "";

        this.drawTexturedModalRect(this.width/2 - 75 + offsetUnlock, this.top + 65, 0, y, size1, 12);//gradient1
        this.drawTexturedModalRect(this.width/2 - 75 + offsetUnlock, this.top + 90, 0, y, size2, 12);//gradient2
        this.fontRenderer.drawString(text1, this.width/2 + offsetUnlock - this.fontRenderer.getStringWidth(text1)/2, this.top + 67, Color.WHITE.getRGB());
        this.fontRenderer.drawString(text2, this.width/2 + offsetUnlock - this.fontRenderer.getStringWidth(text2)/2, this.top + 92, Color.WHITE.getRGB());
        this.fontRenderer.drawString(title1, this.width/2 + offsetUnlock - this.fontRenderer.getStringWidth(title1)/2, this.top + 56, Color.BLACK.getRGB());
        this.fontRenderer.drawString(title2, this.width/2 + offsetUnlock - this.fontRenderer.getStringWidth(title2)/2, this.top + 81, Color.BLACK.getRGB());

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch(button.id)
        {
            case 0:
                Minecraft.getMinecraft().displayGuiScreen(new MainJobsMenu());
                break;
            case 1:
                Minecraft.getMinecraft().displayGuiScreen(new GuiHowXP(this.job));
                break;
            case 2:
                if(offsetUnlock == 0)
                {
                    offsetUnlock = -70;
                }
                else if(offsetUnlock == -70)
                {
                    offsetUnlock = 0;
                }
                initGui();
                break;
            default:
                break;
        }
        super.actionPerformed(button);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent)
    {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan((double)(mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float)Math.atan((double)(mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float)Math.atan((double)(mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }


    @Override
    public List<String> getItemToolTip(ItemStack stack)
    {
        List<String> tooltip = new ArrayList<>();
        tooltip.add(stack.getDisplayName());
        if(ClientInfos.CRAFT_UNLOCK_LVL.get(stack.getItem()) > ClientInfos.job.getLevelByJob(this.job))
        {
            tooltip.add(TextFormatting.RED  + I18n.translateToLocal("text.unlock_lvl") + " " + ClientInfos.CRAFT_UNLOCK_LVL.get(stack.getItem()));
        }
        else tooltip.add(TextFormatting.GREEN  + I18n.translateToLocal("text.unlock_craft"));

        return tooltip;
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int x;
        if (Mouse.getEventDWheel() != 0 && offsetUnlock == -70)
        {
            x = -1 * Integer.signum(Mouse.getEventDWheel());
            this.page = JobsMath.clamp(this.page + x, 0, this.pageNumber-1);
            this.buttonList.get(3).y = this.top + 30 + (int)(((double)this.page/(double) (this.pageNumber - 1))*105);
        }
    }
}
