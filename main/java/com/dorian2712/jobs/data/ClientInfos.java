package com.dorian2712.jobs.data;

import com.dorian2712.jobs.gui.GuiGainXP;
import com.dorian2712.jobs.gui.screens.GuiLevelUp;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Job;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientInfos {

    public static JobsInfo job = new JobsInfo();
    public static GuiGainXP.GuiAddXpInfos addXPInfos = null;
    public static ModConfig config;

    public static void showAddGui(Job job, long addXp)
    {
        if(addXPInfos == null)
        {
            addXPInfos = new GuiGainXP.GuiAddXpInfos(job, addXp);
            return;
        }
        if(job == addXPInfos.job)
        {
            addXPInfos.xpAdded += addXp;
            addXPInfos.ticks = System.currentTimeMillis() + 5000;
        }
        else
            addXPInfos = new GuiGainXP.GuiAddXpInfos(job, addXp);
    }

    public static void showLevelUpGui(Job j)
    {
        Minecraft.getMinecraft().displayGuiScreen(new GuiLevelUp(j));
        Minecraft.getMinecraft().setIngameNotInFocus();
    }

    public static Map<Block, long[]> BREAK_BLOCK_XP = new HashMap<>();
    public static Map<Block, Job> BREAK_BLOCK_JOB = new HashMap<>();

    public static Map<Item, long[]> HARVEST_CROP_XP = new HashMap<>();
    public static Map<Item, Job> HARVEST_CROP_JOB = new HashMap<>();

    public static Map<Item, long[]> CRAFT_ITEM_XP = new HashMap<>();
    public static Map<Item, Job> CRAFT_ITEM_JOB = new HashMap<>();

    public static Map<Item, long[]> SMELT_ITEM_XP = new HashMap<>();
    public static Map<Item, Job> SMELT_ITEM_JOB = new HashMap<>();

    public static Map<String, long[]> KILL_ENTITY_XP = new HashMap<>();
    public static Map<String, Job> KILL_ENTITY_JOB = new HashMap<>();


    public static Map<Item, Integer> CRAFT_UNLOCK_LVL = new HashMap<>();
    public static Map<Item, Job> CRAFT_UNLOCK_JOB = new HashMap<>();

    public static List<ItemStack> CURRENT_REWARDS = new ArrayList<>();

    public static List<Item> getClassedUnlockedItems(Job job)
    {
        List<Item> listNotInOrder = new ArrayList<>();
        List<Item> listInOrder = new ArrayList<>();
        for(Map.Entry<Item, Job> entry : ClientInfos.CRAFT_UNLOCK_JOB.entrySet())
        {
            if(entry.getValue() == job)
                listNotInOrder.add(entry.getKey());
        }

        while(!listNotInOrder.isEmpty())
        {
            int lvl = 26;
            int index = -1;
            Item i = null;
            for(Item item : listNotInOrder)
            {
                if(ClientInfos.CRAFT_UNLOCK_LVL.get(item) < lvl)
                {
                    index = listNotInOrder.indexOf(item);
                    lvl = ClientInfos.CRAFT_UNLOCK_LVL.get(item);
                    i = item;
                }
            }
            listInOrder.add(i);
            listNotInOrder.remove(index);
        }

        return listInOrder;
    }


    public static List<ItemStack> getStacksFromCategories(Constants.XPCategories category, Job j)
    {
        List<ItemStack> list = new ArrayList<>();
        switch (category)
        {
            case BREAKING:
                for(Block b : BREAK_BLOCK_XP.keySet())
                {
                	if(BREAK_BLOCK_JOB.get(b) == j)
                		list.add(new ItemStack(b));
                }
                break;
            case CRAFTING:
                for(Item i : CRAFT_ITEM_XP.keySet())
                {
                	if(CRAFT_ITEM_JOB.get(i) == j)
                		list.add(new ItemStack(i));
                }
                break;
            case SMELTING:
                for(Item i : SMELT_ITEM_XP.keySet())
                {
                	if(SMELT_ITEM_JOB.get(i) == j)
                        list.add(new ItemStack(i));
                }
                break;
            case HARVESTING:
                for(Item i : HARVEST_CROP_XP.keySet())
                {
                	if(HARVEST_CROP_JOB.get(i) == j)
                		list.add(new ItemStack(i));
                }
                break;
            case KILLING:
                for(String s : KILL_ENTITY_XP.keySet())
                {
                    if(KILL_ENTITY_JOB.get(s) == j)
                    {
                        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
                        stack.setTagCompound(new NBTTagCompound());
                        stack.getTagCompound().setString("class", s);
                        list.add(stack);
                    }
                }
                break;
            default:
            	break;            	   
        }
        return list;
    }
}
