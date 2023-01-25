package net.polarfox27.jobs.data;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraftforge.fml.network.NetworkDirection;
import net.polarfox27.jobs.data.registry.*;
import net.polarfox27.jobs.data.registry.XPRegistry.BlockXPRegistry;
import net.polarfox27.jobs.data.registry.XPRegistry.EntityXPRegistry;
import net.polarfox27.jobs.data.registry.XPRegistry.ItemXPRegistry;
import net.polarfox27.jobs.network.PacketUpdateClientJobsData;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ServerJobsData {

    public static final ItemXPRegistry CRAFTING_ITEMS_XP = new ItemXPRegistry("crafting", Items.CRAFTING_TABLE);
    public static final ItemXPRegistry SMELTING_ITEMS_XP = new ItemXPRegistry("smelting", Items.FURNACE);
    public static final BlockXPRegistry BREAKING_BLOCKS_XP = new BlockXPRegistry("breaking", Items.DIAMOND_PICKAXE);
    public static final BlockXPRegistry HARVESTING_CROPS_XP = new BlockXPRegistry("harvesting", Items.IRON_HOE);
    public static final EntityXPRegistry KILLING_ENTITY_XP = new EntityXPRegistry("killing", Items.NETHERITE_SWORD);
    public static final EntityXPRegistry BREEDING_ENTITY_XP = new EntityXPRegistry("breeding", Items.WHEAT);

    public static final Map<String, byte[]> JOBS_ICONS = new HashMap<>();

    public static final RewardsData REWARDS = new RewardsData();
    public static final LevelData JOBS_LEVELS = new LevelData();
    public static final TranslationData TRANSLATIONS = new TranslationData();
    public static final BlockedCraftsData BLOCKED_CRAFTS = new BlockedCraftsData();
    public static final BlockedBlocksData BLOCKED_BLOCKS = new BlockedBlocksData();
    private static final Set<XPRegistry<? extends XPData>> XP_REGISTRIES = new HashSet<>();


    /**
     * Registers the XP Registry
     * @param registry the registry to register
     */
    public static void registerXPRegistry(XPRegistry<? extends XPData> registry){
        XP_REGISTRIES.add(registry);
    }

    /**
     * Registers the common registries used by the mod
     */
    public static void registerCommonXPRegistries(){
        registerXPRegistry(CRAFTING_ITEMS_XP);
        registerXPRegistry(SMELTING_ITEMS_XP);
        registerXPRegistry(BREAKING_BLOCKS_XP);
        registerXPRegistry(HARVESTING_CROPS_XP);
        registerXPRegistry(KILLING_ENTITY_XP);
        registerXPRegistry(BREEDING_ENTITY_XP);
    }


    /**
     * Sends all the data about the jobs to the client
     * @param player the client who will receive the data
     */
    public static void sendDataToClient(ServerPlayerEntity player) {
        PacketUpdateClientJobsData packet1 = new PacketUpdateClientJobsData(XP_REGISTRIES,
                JOBS_LEVELS,
                BLOCKED_CRAFTS,
                BLOCKED_BLOCKS,
                JOBS_ICONS,
                TRANSLATIONS);
        PacketHandler.INSTANCE.sendTo(packet1,
                player.connection.getConnection(),
                NetworkDirection.PLAY_TO_CLIENT);
    }
}
