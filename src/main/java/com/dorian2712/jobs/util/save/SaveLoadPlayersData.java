package com.dorian2712.jobs.util.save;


import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerList;
import com.dorian2712.jobs.util.Constants.Job;
import com.google.gson.*;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

public class SaveLoadPlayersData {

	public static String getDataFolder()
    {
    	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    	if(server.isSinglePlayer())
    		return server.getDataDirectory().getAbsolutePath() + "/saves/" + server.getFolderName() + "/jobs/playerdata";
    	else
    		return server.getDataDirectory().getAbsolutePath() + "/jobs/playerdata";
    }
    private static void createFolder()
    {
        File folder = new File(getDataFolder());
        if(!folder.exists())
        {
            folder.mkdirs();
        }
    }
    private static JsonParser getParser(){
        return new JsonParser();
    }

    public static void saveData(){
        try
        {
            createFolder();
            for(Entry<String, JobsInfo> e : PlayerList.getEntrySet())
            {
                JsonObject json = new JsonObject();
                json.addProperty("uuid", e.getKey());
                JsonObject jobs = new JsonObject();
                long[] xps = e.getValue().toTotalXPs();
                for(int i = 0; i < 4; i++)
                {
                    jobs.addProperty(Job.byIndex(i).name, xps[i]);
                }
                json.add("jobs", (JsonElement)jobs);

                Gson gson = new Gson();
                FileWriter writer = new FileWriter(new File(getDataFolder() + "/" + e.getKey() + ".json"));
                gson.toJson(json, writer);
                writer.close();
            }
            System.out.println(TextFormatting.BLUE + "[Jobs] Players data saved");
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to save Players data ");
        }
    }

    public static void loadData()
    {
        try
        {
            createFolder();
            for(File f : new File(getDataFolder()).listFiles())
            {
                if(f.getAbsolutePath().contains(".json"))
                {
                    JsonObject json = (JsonObject) getParser().parse(new FileReader(f));
                    String uuid = json.get("uuid").getAsString();

                    JsonObject jobs = (JsonObject) json.get("jobs");
                    long[] xps = new long[]{0, 0, 0, 0};
                    for(Job j : Job.values())
                    {
                        if(j != Job.NONE)
                            xps[j.index] = jobs.get(j.name).getAsLong();
                    }

                    PlayerList.put(uuid, new JobsInfo().fromTotalXPs(xps));

                }
            }
            System.out.println(TextFormatting.BLUE + "[Jobs] Players data loaded");
        }
        catch(IOException e)
        {
            System.out.println(TextFormatting.RED + "[Jobs] Failed to load Players data !");
        }
    }
}
