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
    public static final String BLOCKED_EQUIPMENTS_FILE = "blocked_equipments.json";
    public static final String BLOCKED_LEFT_CLICKS_FILE = "blocked_left_clicks.json";
    public static final String BLOCKED_RIGHT_CLICKS_FILE = "blocked_right_clicks.json";
    public static final String BLOCKED_BLOCKS_FILE = "blocked_blocks.json";
    public static final String REWARDS_FILE = "rewards.json";

    /**
     * @param server the server where the config is stored
     * @return the path to the XP Config Folder
     */
    public static String getXPFolder(MinecraftServer server) {
        return getBaseFolder(server) + "/xp";
    }

    /**
     * @param server the server where the config is stored
     * @return the path to the Blocked Items/Blocks Config Folder
     */
    public static String getBlockedFolder(MinecraftServer server) {
        return getBaseFolder(server) + "/blocked";
    }

    /**
     * @param server the server where the config is stored
     * @return the path to the Icons Folder
     */
    public static String getIconFolder(MinecraftServer server) {
        return getBaseFolder(server) + "/icons";
    }

    /**
     * @param server the server where the config is stored
     * @return the path to the Base Config Folder
     */
    public static String getBaseFolder(MinecraftServer server) {
        String name = server.getMotd().substring(server.getMotd().indexOf(" - ")+3);
        if(server.isSingleplayer())
            return server.getServerDirectory().getAbsolutePath() + "/saves/" + name + "/jobs";
        else
            return server.getServerDirectory().getAbsolutePath() + "/jobs";
    }

    /**
     * Joins the parent path and the file name, and get the file at that location
     * @param path the parent folder path
     * @param name the file name
     * @return the file at that location
     */
    public static File join(String path, String name){
        return new File(path + "/" + name);
    }

    /**
     * Writes a JSON object to a file and catch any exception that might be thrown
     * @param f the file to write
     * @param object the object to write in the file
     * @return true if the object was written successfully
     */
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

    /**
     * Reads a JSON object from a file and catch any exception that might be thrown
     * @param f the file to read
     * @return the object stored in the file
     */
    public static Optional<JsonObject> safeReadJSONFromFile(File f){
        try{
            if(f.exists())
                return Optional.of(JsonParser.parseReader(new FileReader(f)).getAsJsonObject());
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
