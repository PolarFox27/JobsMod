package net.polarfox27.jobs.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;

import java.util.Arrays;
import java.util.Map;

public class ReadConfigManager {

    /**
     * Reads the config json files of the server and puts them in ServerJobsData.
     * @param server the server from which to read the config files
     */
    public static void readConfigFiles(MinecraftServer server){
        WriteConfigManager.tryCreateEmptyConfigFiles(server);
        String baseFolder = FileUtil.getBaseFolder(server);
        String xpFolder = FileUtil.getXPFolder(server);
        String blockedFolder = FileUtil.getBlockedFolder(server);
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
        FileUtil.safeReadJSONFromFile(FileUtil.join(blockedFolder, FileUtil.BLOCKED_CRAFTS_FILE))
                .ifPresent(x -> loadBlockedItems(x, ServerJobsData.BLOCKED_CRAFTS));
        ModJobs.info("Loading blocked equipments...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(blockedFolder, FileUtil.BLOCKED_EQUIPMENTS_FILE))
                .ifPresent(x -> loadBlockedItems(x, ServerJobsData.BLOCKED_EQUIPMENTS));
        ModJobs.info("Loading blocked left clicks...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(blockedFolder, FileUtil.BLOCKED_LEFT_CLICKS_FILE))
                .ifPresent(x -> loadBlockedItems(x, ServerJobsData.BLOCKED_LEFT_CLICKS));
        ModJobs.info("Loading blocked right clicks...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(blockedFolder, FileUtil.BLOCKED_RIGHT_CLICKS_FILE))
                .ifPresent(x -> loadBlockedItems(x, ServerJobsData.BLOCKED_RIGHT_CLICKS));
        ModJobs.info("Loading blocked blocks...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(blockedFolder, FileUtil.BLOCKED_BLOCKS_FILE))
                .ifPresent(x -> loadBlockedBlocks(x, ServerJobsData.BLOCKED_BLOCKS));

        ModJobs.info("Loading jobs xp...", false);
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.CRAFTING_ITEMS_XP.getFileName()))
                .ifPresent((obj) -> loadItemRegistry(obj, ServerJobsData.CRAFTING_ITEMS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.SMELTING_ITEMS_XP.getFileName()))
                .ifPresent((obj) -> loadItemRegistry(obj, ServerJobsData.SMELTING_ITEMS_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.BREAKING_BLOCKS_XP.getFileName()))
                .ifPresent((obj) -> loadBlockRegistry(obj, ServerJobsData.BREAKING_BLOCKS_XP, false));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.HARVESTING_CROPS_XP.getFileName()))
                .ifPresent((obj) -> loadBlockRegistry(obj, ServerJobsData.HARVESTING_CROPS_XP, true));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.KILLING_ENTITY_XP.getFileName()))
                .ifPresent((obj) -> loadEntityRegistry(obj, ServerJobsData.KILLING_ENTITY_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.BREEDING_ENTITY_XP.getFileName()))
                .ifPresent((obj) -> loadEntityRegistry(obj, ServerJobsData.BREEDING_ENTITY_XP));
        FileUtil.safeReadJSONFromFile(FileUtil.join(xpFolder, ServerJobsData.FISHING_ITEMS_XP.getFileName()))
                .ifPresent((obj) -> loadItemRegistry(obj, ServerJobsData.FISHING_ITEMS_XP));
        ModJobs.info("Loading jobs icons...", false);
        JobsIconUtil.loadJobsIcon(server);
    }

    /**
     * Reads an Item XP registry json
     * @param object the json object containing the configuration
     * @param registry the registry to configure
     */
    public static void loadItemRegistry(JsonObject object, XPRegistry.ItemXPRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.itemXPDataFromJSON(element.getAsJsonObject())
                            .filter(x -> registry.getXPDataByJob(e.getKey()).stream().map(XPData.ItemXPData::getItem).noneMatch(y -> y == x.getItem()))
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads a Block XP registry json
     * @param object the json object containing the configuration
     * @param registry the registry to configure
     * @param isCrop whether the registry uses crops or normal blocks
     */
    public static void loadBlockRegistry(JsonObject object, XPRegistry.BlockXPRegistry registry, boolean isCrop){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockXPDataFromJSON(element.getAsJsonObject(), isCrop)
                            .filter(x -> registry.getXPDataByJob(e.getKey()).stream().map(XPData.BlockXPData::getBlock).noneMatch(y -> y == x.getBlock()))
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads an Entity XP registry json
     * @param object the json object containing the configuration
     * @param registry the registry to configure
     */
    public static void loadEntityRegistry(JsonObject object, XPRegistry.EntityXPRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.entityXPDataFromJSON(element.getAsJsonObject())
                            .filter(x -> registry.getXPDataByJob(e.getKey()).stream().map(XPData.EntityXPData::getEntity).noneMatch(y -> y == x.getEntity()))
                            .ifPresent(x -> registry.addDataForJob(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + registry.getFileName()
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads the Rewards json
     * @param object the json object containing the configuration
     */
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

    /**
     * Reads the Levels json
     * @param object the json object containing the configuration
     */
    public static void loadLevels(JsonObject object){
        try{
            ServerJobsData.JOBS_LEVELS.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                long[] levels = JsonUtil.longArrayFromJSON(e.getValue().getAsJsonArray(), false);
                if(Arrays.stream(levels).anyMatch(x -> x == 0)){
                    ModJobs.info("Error while reading file <" + FileUtil.LEVELS_FILE
                            + "> : invalid value of 0 for job " + e.getKey() + " !", true);
                    continue;
                }
                ServerJobsData.JOBS_LEVELS.addJobLevel(e.getKey(), levels);
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.LEVELS_FILE
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads the Blocked Crafts json
     * @param object the json object containing the configuration
     */
    public static void loadBlockedItems(JsonObject object, ItemBlockedRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockedItemFromJSON(element.getAsJsonObject(), registry.getType())
                            .ifPresent(x -> registry.addBlockedData(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.BLOCKED_CRAFTS_FILE
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads the Blocked Blocks json
     * @param object the json object containing the configuration
     */
    public static void loadBlockedBlocks(JsonObject object, BlockBlockedRegistry registry){
        try{
            registry.clear();
            for(Map.Entry<String, JsonElement> e : object.entrySet()) {
                for(JsonElement element : e.getValue().getAsJsonArray())
                    JsonUtil.blockedBlockFromJSON(element.getAsJsonObject(), registry.getType())
                            .ifPresent(x -> registry.addBlockedData(e.getKey(), x));
            }
        }
        catch(ClassCastException | IllegalStateException e){
            ModJobs.info("Error while reading file <" + FileUtil.BLOCKED_CRAFTS_FILE
                    + "> : json syntax error !", true);
        }
    }

    /**
     * Reads the Translations json
     * @param object the json object containing the configuration
     */
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
