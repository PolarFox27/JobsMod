package net.polarfox27.jobs.util.config;

import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.polarfox27.jobs.data.registry.BlockedBlocksData;
import net.polarfox27.jobs.data.registry.BlockedCraftsData;
import net.polarfox27.jobs.data.registry.RewardsData;
import net.polarfox27.jobs.data.registry.XPData;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUtil {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonArray longArrayToJSON(long[] array){
        JsonArray jsonArray = new JsonArray();
        for(long l : array)
            jsonArray.add(l);
        return jsonArray;
    }

    public static long[] longArrayFromJSON(JsonArray jsonArray){
        long[] array = new long[jsonArray.size()];
        for(int i = 0; i < array.length; i++)
            array[i] = jsonArray.get(i).getAsLong();
        return array;
    }

    public static JsonObject itemStackToJSON(ItemStack stack){
        JsonObject object = new JsonObject();
        object.addProperty("item", stack.getItem().getRegistryName().toString());
        object.addProperty("count", stack.getCount());
        object.addProperty("metadata", stack.getDamageValue());
        return object;
    }

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

    public static JsonObject itemXPDataToJSON(XPData.ItemXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("item", data.getItem().getRegistryName().toString());
        if(data.getMetadata() >= 0)
            object.addProperty("metadata", data.getMetadata());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    public static Optional<XPData.ItemXPData> itemXPDataFromJSON(JsonObject object){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if(item == null)
            return Optional.empty();
        int metadata = -1;
        if(object.has("metadata"))
            metadata = object.get("metadata").getAsInt();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array);
        return Optional.of(new XPData.ItemXPData(xp, item, metadata));
    }

    public static JsonObject blockXPDataToJSON(XPData.BlockXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("block", data.getBlock().getRegistryName().toString());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    public static Optional<XPData.BlockXPData> blockXPDataFromJSON(JsonObject object){
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(object.get("block").getAsString()));
        if(block == null)
            return Optional.empty();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array);
        return Optional.of(new XPData.BlockXPData(xp, block));
    }

    public static JsonObject entityXPDataToJSON(XPData.EntityXPData data){
        JsonObject object = new JsonObject();
        object.addProperty("entity", EntityType.getKey(data.getEntity()).toString());
        JsonArray array = longArrayToJSON(data.getXP_values());
        object.add("xp", array);
        return object;
    }

    public static Optional<XPData.EntityXPData> entityXPDataFromJSON(JsonObject object){
        EntityType<? extends Entity> entity = EntityType.byString(object.get("entity").getAsString()).orElse(null);
        if(entity == null)
            return Optional.empty();
        JsonArray array = object.getAsJsonArray("xp");
        long[] xp = longArrayFromJSON(array);
        return Optional.of(new XPData.EntityXPData(xp, entity));
    }

    public static JsonArray rewardToJSON(RewardsData.Reward reward){
        JsonArray stacks = new JsonArray();
        for(ItemStack s : reward.getRewards())
            stacks.add(itemStackToJSON(s));
        return stacks;
    }

    public static Optional<RewardsData.Reward> rewardFromJSON(JsonArray array, int level){
        List<ItemStack> stacks = new ArrayList<>();
        for(JsonElement e : array)
            itemStackFromJSON(e.getAsJsonObject()).ifPresent(stacks::add);
        if(stacks.isEmpty())
            return Optional.empty();
        return Optional.of(new RewardsData.Reward(level, stacks));
    }

    public static JsonObject blockedCraftToJSON(BlockedCraftsData.BlockedCraft craft){
        JsonObject object = new JsonObject();
        object.addProperty("item", ForgeRegistries.ITEMS.getKey(craft.getCraft()).toString());
        if(craft.getMetadata() >= 0)
            object.addProperty("metadata", craft.getMetadata());
        object.addProperty("level", craft.getLevel());
        return object;
    }

    public static Optional<BlockedCraftsData.BlockedCraft> blockedCraftFromJSON(JsonObject object){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("item").getAsString()));
        if(item == null)
            return Optional.empty();
        int metadata = -1;
        if(object.has("metadata"))
            metadata = object.get("metadata").getAsInt();
        int level = object.get("level").getAsInt();
        return Optional.of(new BlockedCraftsData.BlockedCraft(level, item, metadata));
    }

    public static JsonObject blockedBlockToJSON(BlockedBlocksData.BlockedBlock block){
        JsonObject object = new JsonObject();
        object.addProperty("block", ForgeRegistries.BLOCKS.getKey(block.getBlock()).toString());
        object.addProperty("level", block.getLevel());
        return object;
    }

    public static Optional<BlockedBlocksData.BlockedBlock> blockedBlockFromJSON(JsonObject object){
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(object.get("block").getAsString()));
        if(block == null)
            return Optional.empty();
        int level = object.get("level").getAsInt();
        return Optional.of(new BlockedBlocksData.BlockedBlock(level, block));
    }
}
