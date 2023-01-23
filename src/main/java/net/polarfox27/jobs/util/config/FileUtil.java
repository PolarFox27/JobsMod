package net.polarfox27.jobs.util.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.polarfox27.jobs.ModJobs;

import java.io.*;
import java.util.Optional;

public class FileUtil {

    public static final String LEVELS_FILE = "levels.json";
    public static final String TRANSLATIONS_FILE = "translations.json";
    public static final String BLOCKED_CRAFTS_FILE = "blocked_crafts.json";
    public static final String BLOCKED_BLOCKS_FILE = "blocked_blocks.json";
    public static final String REWARDS_FILE = "rewards.json";

    public static String getXPFolder(MinecraftServer server) {
        return getBaseFolder(server) + "/xp";
    }

    public static String getIconFolder(MinecraftServer server) {
        return getBaseFolder(server) + "/icons";
    }

    public static String getBaseFolder(MinecraftServer server) {
        String name = server.getMotd().substring(server.getMotd().indexOf(" - ")+3);
        if(server.isSingleplayer())
            return server.getServerDirectory().getAbsolutePath() + "/saves/" + name + "/jobs";
        else
            return server.getServerDirectory().getAbsolutePath() + "/jobs";
    }

    public static File join(String path, String name){
        return new File(path + "/" + name);
    }

    public static boolean safeWriteJSONtoFile(File f, JsonObject object){
        try{
            FileWriter writer = new FileWriter(f);
            JsonUtil.GSON.toJson(object, writer);
            writer.close();
            return true;
        }
        catch (IOException e) {
            ModJobs.info("Error while writing to file <" + f.getName() + ">", true);
        }
        return false;
    }

    public static Optional<JsonObject> safeReadJSONFromFile(File f){
        try{
            if(f.exists())
                return Optional.of(new JsonParser().parse(new FileReader(f)).getAsJsonObject());
        }
        catch (FileNotFoundException e) {
            ModJobs.info("Error while reading file <" + f.getName() + "> : file doesn't exist !", true);
        }
        catch (JsonParseException | IllegalStateException e) {
            ModJobs.info("Error while reading file <" + f.getName() + "> : json syntax error !", true);
        }
        return Optional.empty();
    }
}
