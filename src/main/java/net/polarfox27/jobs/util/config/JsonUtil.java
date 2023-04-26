package net.polarfox27.jobs.util.config;

import com.google.gson.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.polarfox27.jobs.data.registry.RewardsData;
import net.polarfox27.jobs.data.registry.unlock.BlockedData;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUtil {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Converts a long array to a JSON array
     * @param array the array to convert
     * @return the json version of the array
     */
    public static JsonArray longArrayToJSON(long[] array){
        JsonArray jsonArray = new JsonArray();
        for(long l : array)
            jsonArray.add(l);
        return jsonArray;
    }

    /**
     * load a long array from a JSON array
     * @param jsonArray the array to convert
     * @param allowNegative if false, all negative element will be replaced by zeros.
     * @return the long array
     */
    public static long[] longArrayFromJSON(JsonArray jsonArray, boolean allowNegative){
        long[] array = new long[jsonArray.size()];
        for(int i = 0; i < array.length; i++) {
            long x = jsonArray.get(i).getAsLong();
            array[i] = allowNegative || x >= 0 ? x : 0;
        }
        return array;
    }

    /**
     * Creates a JSON object representing an Item Stack
     * @param stack the stack to represent
     * @return the JSON object representing the stack
     */
    public static JsonObject itemStackToJSON(ItemStack stack){
        JsonObject object = new JsonObject();
        object.addProperty("item", stack.getItem().getRegistryName().toString());
        object.addProperty("count", stack.getCount());
        object.addProperty("metadata", stack.getDamageValue());
        return object;
    }

    /**
     * Creates the Item Stack represented in the JSON object
     * @param object the JSON object representing the Item Stack
     * @return the created Item Stack
     */
    public static Optional<ItemStack> itemStackFromJSON(JsonObject object){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if(item == null)
            return Optional.empty();
        int count = object.get("count").getAsInt();
        int metadata = -1;
        if(object.has("metadata"))
            metadata = object.get("metadata").getAsInt();
        return Optional.of(JobsUtil.itemStack(item, count, metadata));
    }

    /**
     * Creates a JSON object representing the XPData
     * @param data the XPData to represent
     * @return the JSON object representing the data
     */
    public static JsonObject itemXPDataToJSON(XPData.ItemXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("item", data.getItem().getRegistryName().toString());
        if(data.getMetadata() >= 0)
            object.addProperty("metadata", data.getMetadata());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    /**
     * Creates the Item XPData represented in the JSON object
     * @param object the JSON object representing the Item XPData
     * @return the created Item XPData
     */
    public static Optional<XPData.ItemXPData> itemXPDataFromJSON(JsonObject object){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if(item == null || item == Items.AIR)
            return Optional.empty();
        int metadata = -1;
        if(object.has("metadata"))
            metadata = object.get("metadata").getAsInt();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array, false);
        return Optional.of(new XPData.ItemXPData(xp, item, metadata));
    }

    /**
     * Creates a JSON object representing the XPData
     * @param data the XPData to represent
     * @return the JSON object representing the data
     */
    public static JsonObject blockXPDataToJSON(XPData.BlockXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("block", data.getBlock().getRegistryName().toString());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    /**
     * Creates the Block XPData represented in the JSON object
     * @param object the JSON object representing the Block XPData
     * @return the created Block XPData
     */
    public static Optional<XPData.BlockXPData> blockXPDataFromJSON(JsonObject object){
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(object.get("block").getAsString()));
        if(block == null || block == Blocks.AIR)
            return Optional.empty();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array, false);
        return Optional.of(new XPData.BlockXPData(xp, block));
    }

    /**
     * Creates a JSON object representing the XPData
     * @param data the XPData to represent
     * @return the JSON object representing the data
     */
    public static JsonObject entityXPDataToJSON(XPData.EntityXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("entity", EntityType.getKey(data.getEntity()).toString());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    /**
     * Creates the Entity XPData represented in the JSON object
     * @param object the JSON object representing the entity XPData
     * @return the created Entity XPData
     */
    public static Optional<XPData.EntityXPData> entityXPDataFromJSON(JsonObject object){
        EntityType<? extends Entity> entity = EntityType.byString(object.get("entity").getAsString()).orElse(null);
        if(entity == null)
            return Optional.empty();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array, false);
        return Optional.of(new XPData.EntityXPData(xp, entity));
    }

    /**
     * Creates a JSON array representing the Reward
     * @param reward the Reward to represent
     * @return the JSON array representing the Reward
     */
    public static JsonArray rewardToJSON(RewardsData.Reward reward){
        JsonArray stacks = new JsonArray();
        for(ItemStack s : reward.getRewards())
            stacks.add(itemStackToJSON(s));
        return stacks;
    }

    /**
     * Creates the Reward represented in the JSON array
     * @param array the JSON object representing the Reward
     * @param level the level of the Reward
     * @return the created Reward
     */
    public static Optional<RewardsData.Reward> rewardFromJSON(JsonArray array, int level){
        List<ItemStack> stacks = new ArrayList<>();
        for(JsonElement e : array)
            itemStackFromJSON(e.getAsJsonObject()).ifPresent(stacks::add);
        if(stacks.isEmpty())
            return Optional.empty();
        return Optional.of(new RewardsData.Reward(level, stacks));
    }

    /**
     * Creates a JSON object representing the Blocked Item data
     * @param data the Blocked Item data to represent
     * @return the JSON object representing the Blocked Craft data
     */
    public static JsonObject blockedItemToJSON(BlockedData.ItemBlockedData data){
        JsonObject object = new JsonObject();
        object.addProperty("item", ForgeRegistries.ITEMS.getKey(data.getItem()).toString());
        if(data.getMetadata() >= 0)
            object.addProperty("metadata", data.getMetadata());
        object.addProperty("level", data.getLevel());
        return object;
    }

    /**
     * Creates the Blocked Item data represented in the JSON object
     * @param object the JSON object representing the Blocked Item data
     * @return the created Blocked Item data
     */
    public static Optional<BlockedData.ItemBlockedData> blockedItemFromJSON(JsonObject object, BlockedData.Type type){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if(item == null)
            return Optional.empty();
        int metadata = -1;
        if(object.has("metadata"))
            metadata = object.get("metadata").getAsInt();
        int level = object.get("level").getAsInt();
        return Optional.of(new BlockedData.ItemBlockedData(level, type, item, metadata));
    }

    /**
     * Creates a JSON object representing the Blocked Block data
     * @param block the Blocked Block data to represent
     * @return the JSON object representing the Blocked Block data
     */
    public static JsonObject blockedBlockToJSON(BlockedData.BlockBlockedData block){
        JsonObject object = new JsonObject();
        object.addProperty("block", ForgeRegistries.BLOCKS.getKey(block.getBlock()).toString());
        object.addProperty("level", block.getLevel());
        return object;
    }

    /**
     * Creates the Blocked Block data represented in the JSON object
     * @param object the JSON object representing the Blocked Block data
     * @return the created Blocked Block data
     */
    public static Optional<BlockedData.BlockBlockedData> blockedBlockFromJSON(JsonObject object, BlockedData.Type type){
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(object.get("block").getAsString()));
        if(block == null)
            return Optional.empty();
        int level = object.get("level").getAsInt();
        return Optional.of(new BlockedData.BlockBlockedData(level, type, block));
    }
}
