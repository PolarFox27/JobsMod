package net.polarfox27.jobs.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkDirection;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.data.registry.RewardsData;
import net.polarfox27.jobs.data.registry.TranslationData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.BlockedData;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;
import net.polarfox27.jobs.data.registry.xp.XPRegistry.BlockXPRegistry;
import net.polarfox27.jobs.data.registry.xp.XPRegistry.EntityXPRegistry;
import net.polarfox27.jobs.data.registry.xp.XPRegistry.ItemXPRegistry;
import net.polarfox27.jobs.network.PacketUpdateClientJobsData;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ServerJobsData {

    public static final ItemXPRegistry CRAFTING_ITEMS_XP = new ItemXPRegistry("crafting", Items.CRAFTING_TABLE);
    public static final ItemXPRegistry SMELTING_ITEMS_XP = new ItemXPRegistry("smelting", Items.FURNACE);
    public static final BlockXPRegistry BREAKING_BLOCKS_XP = new BlockXPRegistry("breaking", Items.DIAMOND_PICKAXE);
    public static final BlockXPRegistry HARVESTING_CROPS_XP = new BlockXPRegistry("harvesting", Items.IRON_HOE);
    public static final EntityXPRegistry KILLING_ENTITY_XP = new EntityXPRegistry("killing", Items.NETHERITE_SWORD);
    public static final EntityXPRegistry BREEDING_ENTITY_XP = new EntityXPRegistry("breeding", Items.WHEAT);
    public static final ItemXPRegistry FISHING_ITEMS_XP = new ItemXPRegistry("fishing", Items.FISHING_ROD);

    public static final Map<String, byte[]> JOBS_ICONS = new HashMap<>();

    public static final RewardsData REWARDS = new RewardsData();
    public static final LevelData JOBS_LEVELS = new LevelData();
    public static final TranslationData TRANSLATIONS = new TranslationData();
    public static final BlockBlockedRegistry BLOCKED_BLOCKS = new BlockBlockedRegistry(BlockedData.Type.BREAKING);
    public static final ItemBlockedRegistry BLOCKED_CRAFTS = new ItemBlockedRegistry(BlockedData.Type.CRAFTING);
    public static final ItemBlockedRegistry BLOCKED_EQUIPMENTS = new ItemBlockedRegistry(BlockedData.Type.EQUIP);
    public static final ItemBlockedRegistry BLOCKED_LEFT_CLICKS = new ItemBlockedRegistry(BlockedData.Type.LEFT_CLICK);
    public static final ItemBlockedRegistry BLOCKED_RIGHT_CLICKS = new ItemBlockedRegistry(BlockedData.Type.RIGHT_CLICK);
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
        registerXPRegistry(FISHING_ITEMS_XP);
    }


    /**
     * Sends all the data about the jobs to the client
     * @param player the client who will receive the data
     */
    public static void sendDataToClient(ServerPlayer player) {
        Set<ItemBlockedRegistry> itemBlockedRegistries = Arrays.stream(
                new ItemBlockedRegistry[]{BLOCKED_CRAFTS, BLOCKED_EQUIPMENTS, BLOCKED_LEFT_CLICKS, BLOCKED_RIGHT_CLICKS}
                ).collect(Collectors.toSet());
        Set<BlockBlockedRegistry> blockBlockedRegistries = Arrays.stream(
                new BlockBlockedRegistry[]{BLOCKED_BLOCKS}).collect(Collectors.toSet());
        PacketUpdateClientJobsData packet1 = new PacketUpdateClientJobsData(XP_REGISTRIES,
                JOBS_LEVELS,
                itemBlockedRegistries,
                blockBlockedRegistries,
                JOBS_ICONS,
                TRANSLATIONS);
        PacketHandler.INSTANCE.sendTo(packet1,
                player.connection.connection,
                NetworkDirection.PLAY_TO_CLIENT);
    }
}
