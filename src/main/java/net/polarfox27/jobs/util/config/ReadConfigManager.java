package net.polarfox27.jobs.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.registry.XPRegistry;

import java.util.Map;

public class ReadConfigManager {

    public static void readConfigFiles(MinecraftServer server){
        WriteConfigManager.tryCreateEmptyConfigFiles(server);
        String baseFolder = FileUtil.getBaseFolder(server);
        String xpFolder = FileUtil.getXPFolder(server);
        ModJobs.info("Loading jobs levels...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(baseFolder, FileUtil.LEVELS_FILE))
                .ifPresent(ReadConfigManager::loadLevels);
        ModJobs.info("Loading jobs translations...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(baseFolder, FileUtil.TRANSLATIONS_FILE))
                .ifPresent(ReadConfigManager::loadTranslations);
        ModJobs.info("Loading job rewards...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(baseFolder, FileUtil.REWARDS_FILE))
                .ifPresent(ReadConfigManager::loadRewards);
        ModJobs.info("Loading blocked crafts...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(baseFolder, FileUtil.BLOCKED_CRAFTS_FILE))
                .ifPresent(ReadConfigManager::loadBlockedCrafts);
        ModJobs.info("Loading blocked blocks...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(baseFolder, FileUtil.BLOCKED_BLOCKS_FILE))
                .ifPresent(ReadConfigManager::loadBlockedBlocks);
        ModJobs.info("Loading jobs xp...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.CRAFTING_ITEMS_XP.getFileName()))
                .ifPresent((obj) -> loadItemRegistry(obj, ServerJobsData.CRAFTING_ITEMS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.SMELTING_ITEMS_XP.getFileName()))
                .ifPresent((obj) -> loadItemRegistry(obj, ServerJobsData.SMELTING_ITEMS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.BREAKING_BLOCKS_XP.getFileName()))
                .ifPresent((obj) -> loadBlockRegistry(obj, ServerJobsData.BREAKING_BLOCKS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.HARVESTING_CROPS_XP.getFileName()))
                .ifPresent((obj) -> loadBlockRegistry(obj, ServerJobsData.HARVESTING_CROPS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.KILLING_ENTITY_XP.getFileName()))
                .ifPresent((obj) -> loadEntityRegistry(obj, ServerJobsData.KILLING_ENTITY_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.BREEDING_ENTITY_XP.getFileName()))
                .ifPresent((obj) -> loadEntityRegistry(obj, ServerJobsData.BREEDING_ENTITY_XP));
        ModJobs.info("Loading jobs icons...", false);
        JobsIconUtil.loadJobsIcon(server);
    }

    public static void loadItemRegistry(JsonObject object, XPRegistry.ItemXPRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.itemXPDataFromJSON(element.getAsJsonObject())
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    public static void loadBlockRegistry(JsonObject object, XPRegistry.BlockXPRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockXPDataFromJSON(element.getAsJsonObject())
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    public static void loadEntityRegistry(JsonObject object, XPRegistry.EntityXPRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.entityXPDataFromJSON(element.getAsJsonObject())
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    public static void loadRewards(JsonObject object){
        try{
            ServerJobsData.REWARDS.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) { //job entry
                for(Map.Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet())
                    JsonUtil.rewardFromJSON(e2.getValue().getAsJsonArray(), Integer.parseInt(e2.getKey()))
                            .ifPresent(x -> ServerJobsData.REWARDS.addRewardForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException | NumberFormatException e){
            ModJobs.info("Error while reading file <" + FileUtil.REWARDS_FILE
                    + "> : json syntax error !", true);
        }
    }

    public static void loadLevels(JsonObject object){
        try{
            ServerJobsData.JOBS_LEVELS.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet())
                ServerJobsData.JOBS_LEVELS.addJobLevel(e.getKey(),
                        JsonUtil.longArrayFromJSON(e.getValue().getAsJsonArray()));
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.LEVELS_FILE
                    + "> : json syntax error !", true);
        }
    }

    public static void loadBlockedCrafts(JsonObject object){
        try{
            ServerJobsData.BLOCKED_CRAFTS.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockedCraftFromJSON(element.getAsJsonObject())
                            .ifPresent(x -> ServerJobsData.BLOCKED_CRAFTS.addBlockedCraftForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.BLOCKED_CRAFTS_FILE
                    + "> : json syntax error !", true);
        }
    }

    public static void loadBlockedBlocks(JsonObject object){
        try{
            ServerJobsData.BLOCKED_BLOCKS.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockedBlockFromJSON(element.getAsJsonObject())
                            .ifPresent(x -> ServerJobsData.BLOCKED_BLOCKS.addBlockedBlockForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.BLOCKED_CRAFTS_FILE
                    + "> : json syntax error !", true);
        }
    }

    public static void loadTranslations(JsonObject object){
        try{
            ServerJobsData.TRANSLATIONS.reset(ServerJobsData.JOBS_LEVELS.getJobs());
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(Map.Entry<String, JsonElement> e2 : e.getValue().getAsJsonObject().entrySet())
                    ServerJobsData.TRANSLATIONS.addTranslation(e.getKey(), e2.getKey(), e2.getValue().getAsString());
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.TRANSLATIONS_FILE
                    + "> : json syntax error !", true);
        }
    }
}
