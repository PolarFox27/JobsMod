package net.polarfox27.jobs.util.config;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.registry.BlockedBlocksData;
import net.polarfox27.jobs.data.registry.BlockedCraftsData;
import net.polarfox27.jobs.data.registry.RewardsData;
import net.polarfox27.jobs.data.registry.XPData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WriteConfigManager {


    /**
     * Checks if all config files are present, and create template files if not
     * @param server the server in whose files the config is checked
     */
    public static void tryCreateEmptyConfigFiles(MinecraftServer server) {
        String baseFolder = FileUtil.getBaseFolder(server);
        String xpFolder = FileUtil.getXPFolder(server);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.CRAFTING_ITEMS_XP.getFileName(), 0);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.SMELTING_ITEMS_XP.getFileName(), 0);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.BREAKING_BLOCKS_XP.getFileName(), 1);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.HARVESTING_CROPS_XP.getFileName(), 1);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.KILLING_ENTITY_XP.getFileName(), 2);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.BREEDING_ENTITY_XP.getFileName(), 2);
        tryCreateEmptyXPFile(xpFolder, ServerJobsData.FISHING_ITEMS_XP.getFileName(), 0);
        tryCreateEmptyLevelFile(baseFolder, FileUtil.LEVELS_FILE);
        tryCreateEmptyRewardFile(baseFolder, FileUtil.REWARDS_FILE);
        tryCreateEmptyBlockedCraftFile(baseFolder, FileUtil.BLOCKED_CRAFTS_FILE);
        tryCreateEmptyBlockedBlockFile(baseFolder, FileUtil.BLOCKED_BLOCKS_FILE);
        tryCreateEmptyTranslationFile(baseFolder, FileUtil.TRANSLATIONS_FILE);
    }

    /**
     * Sends a warning message to the console if a template file was created.
     * @param file the file created.
     */
    public static void showFileCreated(String file){
        ModJobs.info(TextFormatting.YELLOW +  "Warning : File <" +
                        file + "> not found. An empty file was created",
                        false);
    }


    /**
     * Creates a template xp registry file
     * type 0 = item
     * type 1 = block
     * type 2 = entity
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     * @param type the xp registry type (see types above)
     */
    public static void tryCreateEmptyXPFile(String folder, String name, int type) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        JsonObject defaultProperty = new JsonObject();
        long[] base_xp = new long[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
        switch(type){
            case 0:
                defaultProperty = JsonUtil.itemXPDataToJSON(new XPData.ItemXPData(base_xp, Items.GOLDEN_APPLE, -1));
                break;
            case 1:
                defaultProperty = JsonUtil.blockXPDataToJSON(new XPData.BlockXPData(base_xp, Blocks.COAL_ORE));
                break;
            case 2:
                defaultProperty = JsonUtil.entityXPDataToJSON(new XPData.EntityXPData(base_xp, EntityType.COW));
                break;
            default:
                return;
        }
        JsonArray array1 = new JsonArray();
        array1.add(defaultProperty);
        json.add("miner", array1);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }

    /**
     * Creates a template levels file.
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     */
    public static void tryCreateEmptyLevelFile(String folder, String name) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        JsonObject defaultProperty = new JsonObject();

        JsonArray array = new JsonArray();
        for(int i = 0; i < 20; i++)
            array.add((i+1)*100);
        json.add("miner", array);
        json.add("farmer", array);
        json.add("alchemist", array);
        json.add("hunter", array);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }


    /**
     * Creates a template translations file.
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     */
    public static void tryCreateEmptyTranslationFile(String folder, String name) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        JsonObject miner = new JsonObject();
        miner.addProperty("en_us", "Miner");
        miner.addProperty("fr_fr", "Mineur");
        JsonObject farmer = new JsonObject();
        farmer.addProperty("en_us", "Farmer");
        farmer.addProperty("fr_fr", "Fermier");
        JsonObject alchemist = new JsonObject();
        alchemist.addProperty("en_us", "Alchemist");
        alchemist.addProperty("fr_fr", "Alchimiste");
        JsonObject hunter = new JsonObject();
        hunter.addProperty("en_us", "Hunter");
        hunter.addProperty("fr_fr", "Chasseur");
        json.add("miner", miner);
        json.add("farmer", farmer);
        json.add("alchemist", alchemist);
        json.add("hunter", hunter);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }


    /**
     * Creates a template rewards file.
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     */
    public static void tryCreateEmptyRewardFile(String folder, String name) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        List<ItemStack> rewards1 = new ArrayList<>();
        List<ItemStack> rewards2 = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            rewards1.add(new ItemStack(Items.DIAMOND, 1));
            rewards2.add(new ItemStack(Items.EMERALD, 2));
        }
        JsonArray default1 = JsonUtil.rewardToJSON(new RewardsData.Reward(3, rewards1));
        JsonArray default2 = JsonUtil.rewardToJSON(new RewardsData.Reward(5, rewards2));
        JsonObject object = new JsonObject();
        object.add("3", default1);
        object.add("5", default2);
        json.add("miner", object);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }

    /**
     * Creates a template blocked crafts file.
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     */
    public static void tryCreateEmptyBlockedCraftFile(String folder, String name) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        JsonObject default1 = JsonUtil.blockedCraftToJSON(new BlockedCraftsData.BlockedCraft(2, Items.IRON_BOOTS, -1));
        JsonObject default2 = JsonUtil.blockedCraftToJSON(new BlockedCraftsData.BlockedCraft(5, Items.DIAMOND_BOOTS, -1));
        JsonArray array = new JsonArray();
        array.add(default1);
        array.add(default2);
        json.add("miner", array);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }

    /**
     * Creates a template blocked blocks file.
     * @param folder the parent folder in which the file will be created
     * @param name the file name
     */
    public static void tryCreateEmptyBlockedBlockFile(String folder, String name) {
        new File(folder).mkdirs();
        File f = FileUtil.join(folder, name);
        if(f.exists())
            return;
        JsonObject json = new JsonObject();
        JsonObject default1 = JsonUtil.blockedBlockToJSON(new BlockedBlocksData.BlockedBlock(2, Blocks.IRON_BLOCK));
        JsonObject default2 = JsonUtil.blockedBlockToJSON(new BlockedBlocksData.BlockedBlock(5, Blocks.DIAMOND_BLOCK));
        JsonArray array = new JsonArray();
        array.add(default1);
        array.add(default2);
        json.add("miner", array);
        if(FileUtil.safeWriteJSONtoFile(f, json))
            showFileCreated(name);
    }

}
