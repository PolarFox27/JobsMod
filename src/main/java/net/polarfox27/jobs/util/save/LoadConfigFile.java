package net.polarfox27.jobs.util.save;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.polarfox27.jobs.data.GainXPUtil;
import net.polarfox27.jobs.data.ModConfig;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class LoadConfigFile {
	
	
	public static void loadConfigFile()
	{
		File config = getConfigFile();
		if(!config.exists())
		{
			try
			{
				createEmptyConfigFile();
			}
			catch (IOException e) {e.printStackTrace();}
			GainXPUtil.config = ModConfig.defaultConfig();
			return;
		}
		else
		{
			try 
			{
				JsonObject json = (JsonObject) new JsonParser().parse(new FileReader(config));
				boolean isHardMode = json.get("isHardMode").getAsBoolean();
				GainXPUtil.config = new ModConfig().setHardMode(isHardMode);
			} 
			catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) 
			{
				System.out.println(TextFormatting.RED + "[Jobs] Failed to load Config !");
			}
		}
	}
	
	public static void createEmptyConfigFile() throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("isHardMode", false);
        FileWriter writer = new FileWriter(getConfigFile());
        new Gson().toJson(json, writer);
        writer.close();

        System.out.println(TextFormatting.BLUE + "[Jobs]" +
                TextFormatting.YELLOW +  " Warning : File <config.json> not found. An empty file was created");
    }
	
	public static File getConfigFile()
    {
    	MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
    	if(server.isSinglePlayer())
    		return new File(server.getDataDirectory().getAbsolutePath() + "/saves/" + server.getFolderName() + "/jobs/config.json");
    	else
    		return new File(server.getDataDirectory().getAbsolutePath() + "/jobs/config.json");
    }

}
