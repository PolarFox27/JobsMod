package com.dorian2712.jobs.util.save;

import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.data.GainXPUtil;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;

public class LoadXPValues {




    public static void loadCraftItemXP()
    {
        try
        {
            File file = new File(LoadUtil.getDataFolder() + "/xp_craftitem.json");
            if(!file.exists())
            {
                LoadUtil.createEmptyJSON(new File(LoadUtil.getDataFolder()), "xp_craftitem.json");
                return;
            }

            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(file));
            for(Entry<String, JsonElement> entry : json.entrySet())
            {
                String name = entry.getKey().replace('-', ':');
                long[] xps = new long[25];
                JsonObject property = entry.getValue().getAsJsonObject();
                JsonArray array = property.get("xp").getAsJsonArray();
                for(int i = 0; i < 25; i++)
                {
                    xps[i] = array.get(i).getAsLong();
                }

                GainXPUtil.CRAFT_ITEM_JOB.put(Item.getByNameOrId(name), Job.valueOf(property.get("job").getAsString()));
                GainXPUtil.CRAFT_ITEM_XP.put(Item.getByNameOrId(name), xps);
            }
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load CraftItem XP !");
        }

    }

    public static void loadSmeltItemXP()
    {
        try
        {
            File file = new File(LoadUtil.getDataFolder() + "/xp_smeltitem.json");
            if(!file.exists())
            {
                LoadUtil.createEmptyJSON(new File(LoadUtil.getDataFolder()), "xp_smeltitem.json");
                return;
            }

            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(file));
            for(Entry<String, JsonElement> entry : json.entrySet())
            {
                String name = entry.getKey().replace('-', ':');
                long[] xps = new long[25];
                JsonObject property = entry.getValue().getAsJsonObject();
                JsonArray array = property.get("xp").getAsJsonArray();
                for(int i = 0; i < 25; i++)
                {
                    xps[i] = array.get(i).getAsLong();
                }

                GainXPUtil.SMELT_ITEM_JOB.put(Item.getByNameOrId(name), Job.valueOf(property.get("job").getAsString()));
                GainXPUtil.SMELT_ITEM_XP.put(Item.getByNameOrId(name), xps);
            }
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load SmeltItem XP !");
        }

    }


    public static void loadBreakBlockXP()
    {
        try
        {
            File file = new File(LoadUtil.getDataFolder() + "/xp_breakblock.json");
            if(!file.exists())
            {
                LoadUtil.createEmptyJSON(new File(LoadUtil.getDataFolder()), "xp_breakblock.json");
                return;
            }

            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(file));
            for(Entry<String, JsonElement> entry : json.entrySet())
            {
                String name = entry.getKey().replace('-', ':');
                long[] xps = new long[25];
                JsonObject property = entry.getValue().getAsJsonObject();
                JsonArray array = property.get("xp").getAsJsonArray();
                for(int i = 0; i < 25; i++)
                {
                    xps[i] = array.get(i).getAsLong();
                }

                GainXPUtil.BREAK_BLOCK_JOB.put(Block.getBlockFromName(name), Job.valueOf(property.get("job").getAsString()));
                GainXPUtil.BREAK_BLOCK_XP.put(Block.getBlockFromName(name), xps);
            }
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load BlockBreak XP !");
        }

    }

    public static void loadHarvestCropXP()
    {
        try
        {
            File file = new File(LoadUtil.getDataFolder() + "/xp_harvestcrop.json");
            if(!file.exists())
            {
                LoadUtil.createEmptyJSON(new File(LoadUtil.getDataFolder()), "xp_harvestcrop.json");
                return;
            }

            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(file));
            for(Entry<String, JsonElement> entry : json.entrySet())
            {
                String name = entry.getKey().replace('-', ':');
                long[] xps = new long[25];
                JsonObject property = entry.getValue().getAsJsonObject();
                JsonArray array = property.get("xp").getAsJsonArray();
                for(int i = 0; i < 25; i++)
                {
                    xps[i] = array.get(i).getAsLong();
                }

                GainXPUtil.HARVEST_CROP_JOB.put(Item.getByNameOrId(name), Job.valueOf(property.get("job").getAsString()));
                GainXPUtil.HARVEST_CROP_XP.put(Item.getByNameOrId(name), xps);
            }
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load HarvestCrop XP !");
        }

    }

    public static void loadKillEntityXP()
    {
        try
        {
            File file = new File(LoadUtil.getDataFolder() + "/xp_killentity.json");
            if(!file.exists())
            {
                LoadUtil.createEmptyJSON(new File(LoadUtil.getDataFolder()), "xp_killentity.json");
                return;
            }

            JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(file));
            for(Entry<String, JsonElement> entry : json.entrySet())
            {
                String name = entry.getKey().replace("-", " ");
                long[] xps = new long[25];
                JsonObject property = entry.getValue().getAsJsonObject();
                JsonArray array = property.get("xp").getAsJsonArray();
                for(int i = 0; i < 25; i++)
                {
                    xps[i] = array.get(i).getAsLong();
                }

                GainXPUtil.KILL_ENTITY_JOB.put(name, Job.valueOf(property.get("job").getAsString()));
                GainXPUtil.KILL_ENTITY_XP.put(name, xps);
            }
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load KillEntity XP !");
        }

    }


}
