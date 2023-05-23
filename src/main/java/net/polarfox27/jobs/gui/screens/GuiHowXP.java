package com.dorian2712.jobs.gui.screens;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Mouse;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.gui.buttons.ButtonBack;
import com.dorian2712.jobs.gui.buttons.SlideBarXP;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Entities;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.Constants.XPCategories;
import com.dorian2712.jobs.util.JobsMath;
import com.dorian2712.jobs.util.Reference;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class GuiHowXP extends GuiScreen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_how_xp.png");
    public int left;
    public int top;
    private Job job;
    public int page = 0;
    public int pageNumber = 0;
    private List<XPCategories> categories = new ArrayList<>();
    private Map<Integer, List<ItemStack>> itemLists = new HashMap<>();
    private List<String> tooltip = new ArrayList<>();

    public GuiHowXP(Job j)
    {
        this.job = j;
        this.page = 0;
        for(XPCategories c : XPCategories.getXPValues())
        {
        	if(!ClientInfos.getStacksFromCategories(c, this.job).isEmpty())
        	{
        		this.categories.add(c);
        	}
        }
        
        for (int i = 0; i < this.categories.size(); i ++)
        {
            List<ItemStack> stacks = ClientInfos.getStacksFromCategories(this.categories.get(i), this.job);
            this.itemLists.put(i, stacks);
        }
        for(Map.Entry<Integer, List<ItemStack>> entry : this.itemLists.entrySet())
        {
            if(entry.getValue().size()-4 > pageNumber)
                this.pageNumber = entry.getValue().size()-4;
        }
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.addButton(new ButtonBack(0, this.width/2 - 83, this.height/2-70));
        this.addButton(new SlideBarXP(1, this.width/2 + 65, this.height/2 - 46, this));
    }
    
    @Override
    public boolean doesGuiPauseGame() 
    {
    	return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.tooltip.clear();
        this.left = this.width/2 - 88;
        this.top = this.height/2 - 75;
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.left, this.top, 0, 0, 176, 150);//background

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCategories(mouseX, mouseY);
        this.drawCategoriesStacks(mouseX, mouseY);

        if(!tooltip.isEmpty())
            this.drawHoveringText(tooltip, mouseX, mouseY);
    }

    private void drawCategories(int mouseX, int mouseY)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        int size = this.categories.size()*30 + this.categories.size()-1;
        int x = this.left + 3  + 149/2 - size/2;
        int renderIndex = -1;
        for(int i = 0; i < this.categories.size(); i++)
        {
            this.drawTexturedModalRect(x + i*31 + 7, this.top + 10, this.categories.get(i).index*16, 150, 16, 16);
            if(mouseX >= x + i*31 + 7 && mouseX < x + i*31 + 23 && mouseY >= this.top + 10 && mouseY < this.top + 26)
                renderIndex = i;
            if(i < this.categories.size() - 1)
                this.drawTexturedModalRect(x + i*31 + 30, this.top + 10, 176, 0, 1, 126);
        }
        if(renderIndex != -1) this.tooltip.add(I18n.translateToLocal("category." + this.categories.get(renderIndex).name()));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                Minecraft.getMinecraft().displayGuiScreen(new GuiJobInfos(this.job));
            default:
                break;
        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        int x;
        if (Mouse.getEventDWheel() != 0)
        {
            x = -1 * Integer.signum(Mouse.getEventDWheel());
            this.page = JobsMath.clamp(this.page + x, 0, this.pageNumber-1);
            this.buttonList.get(1).y = this.top + 29 + (int)(((double)this.page/(double) (this.pageNumber - 1))*91);
        }
    }

    private void drawCategoriesStacks(int mouseX, int mouseY)
    {
        int size = this.categories.size()*30 + this.categories.size()-1;
        for(int i = 0; i < this.categories.size(); i++)
        {
            int x = this.left + 3  + 149/2 - size/2 + 7 + i*31;
            List<ItemStack> stacks = this.itemLists.get(i);
            RenderHelper.disableStandardItemLighting();
            RenderHelper.enableGUIStandardItemLighting();

            int hoveredIndex = -1;
            for(int j = this.page; j < stacks.size() && j < this.page+5 && j >= 0; j++)
            {
                int y = this.top + 39 + (j - this.page)*18;
                ItemStack stack = stacks.get(j);
                if(stack.getItem() == Items.DIAMOND_SWORD && stack.hasTagCompound())
                {
                	Entities entity = Constants.Entities.byName(stack.getTagCompound().getString("class"));
                    GuiJobInfos.drawEntityOnScreen(x + 8, y + 16, entity.scale/2, this.width/2 - mouseX, this.height/2 - mouseY,
                            Constants.getEntityInstance(entity, Minecraft.getMinecraft().player.world));
                }
                else this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);

                if(mouseX >= x && mouseX < x+16 && mouseY >= y && mouseY < y+16)
                    hoveredIndex = j;
            }
            if(hoveredIndex != -1) {
                ItemStack s = stacks.get(hoveredIndex);
                if(s.getItem() == Items.DIAMOND_SWORD && s.hasTagCompound())
                    tooltip.add(s.getTagCompound().getString("class"));

                else tooltip.add(s.getDisplayName());
                long xp = 0L;
                int lvl = ClientInfos.job.getLevelByJob(this.job);
                if (lvl < 25)
                {
                    if(ClientInfos.CRAFT_ITEM_JOB.containsKey(s.getItem()))
                    {
                        xp = ClientInfos.CRAFT_ITEM_XP.get(s.getItem())[lvl];
                    }
                    else if(ClientInfos.SMELT_ITEM_JOB.containsKey(s.getItem()))
                    {
                        xp = ClientInfos.SMELT_ITEM_XP.get(s.getItem())[lvl];
                    }
                    else if(ClientInfos.BREAK_BLOCK_JOB.containsKey(Block.getBlockFromItem(s.getItem())))
                    {
                        xp = ClientInfos.BREAK_BLOCK_XP.get(Block.getBlockFromItem(s.getItem()))[lvl];
                    }
                    else if(ClientInfos.HARVEST_CROP_JOB.containsKey(s.getItem()))
                    {
                        xp = ClientInfos.HARVEST_CROP_XP.get(s.getItem())[lvl];
                    }
                    else if(s.hasTagCompound() && s.getItem() == Items.DIAMOND_SWORD)
                    {
                        if(ClientInfos.KILL_ENTITY_JOB.containsKey(Entities.byName(s.getTagCompound().getString("class"))))
                            xp = ClientInfos.KILL_ENTITY_XP.get(Entities.byName(s.getTagCompound().getString("class")))[lvl];
                    }
                    if(xp != 0L) tooltip.add(TextFormatting.GREEN + "" + xp + " xp");
                    else
                    {
                        boolean toUnlock = false;
                        for(int j = lvl+1; j < 25; j++)
                        {
                            if(ClientInfos.CRAFT_ITEM_XP.containsKey(s.getItem()))
                                if(ClientInfos.CRAFT_ITEM_XP.get(s.getItem())[j] > 0)
                                {
                                    toUnlock = true;
                                    tooltip.add(TextFormatting.RED + I18n.translateToLocal("text.unlock_lvl") + " " + j);
                                    break;
                                }
                            if(ClientInfos.SMELT_ITEM_XP.containsKey(s.getItem()))
                                if(ClientInfos.SMELT_ITEM_XP.get(s.getItem())[j] > 0)
                                {
                                    toUnlock = true;
                                    tooltip.add(TextFormatting.RED + I18n.translateToLocal("text.unlock_lvl") + " " + j);
                                    break;
                                }
                            if(ClientInfos.BREAK_BLOCK_XP.containsKey(Block.getBlockFromItem(s.getItem())))
                                if(ClientInfos.BREAK_BLOCK_XP.get(Block.getBlockFromItem(s.getItem()))[j] > 0)
                                {
                                    toUnlock = true;
                                    tooltip.add(TextFormatting.RED + I18n.translateToLocal("text.unlock_lvl") + " " + j);
                                    break;
                                }
                            if(ClientInfos.HARVEST_CROP_XP.containsKey(s.getItem()))
                                if(ClientInfos.HARVEST_CROP_XP.get(s.getItem())[j] > 0)
                                {
                                    toUnlock = true;
                                    tooltip.add(TextFormatting.RED + I18n.translateToLocal("text.unlock_lvl") + " " + j);
                                    break;
                                }
                            if(s.getItem() == Items.DIAMOND_SWORD && s.hasTagCompound())
                            {
                            	if(ClientInfos.KILL_ENTITY_XP.containsKey(Entities.byName(s.getTagCompound().getString("class"))))
                            		if(ClientInfos.KILL_ENTITY_XP.get(Entities.byName(s.getTagCompound().getString("class")))[j] > 0)
                            		{
                            			toUnlock = true;
                            			tooltip.add(TextFormatting.RED + I18n.translateToLocal("text.unlock_lvl") + " " + j);
                            			break;
                            		}
                            }

                        }
                        if(!toUnlock)
                            tooltip.add(TextFormatting.RED + "0 xp");
                    }
                }
                else
                {
                    tooltip.add(TextFormatting.DARK_PURPLE + "0 xp");
                }
            }
            RenderHelper.enableStandardItemLighting();
        }
    }
}
