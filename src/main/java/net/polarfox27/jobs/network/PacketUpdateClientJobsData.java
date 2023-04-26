package net.polarfox27.jobs.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.data.registry.TranslationData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;
import net.polarfox27.jobs.util.config.JobsIconUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketUpdateClientJobsData{

    public Set<XPRegistry<? extends XPData>> registries = new HashSet<>();
    public LevelData JOBS_LEVELS = null;
    public Set<ItemBlockedRegistry> BLOCKED_ITEMS_REGISTRIES = new HashSet<>();
    public Set<BlockBlockedRegistry> BLOCKED_BLOCKS_REGISTRIES = new HashSet<>();
    Map<String, byte[]> JOBS_ICONS = new HashMap<>();

    TranslationData TRANSLATIONS = null;

    public PacketUpdateClientJobsData(){}

    /**
     * Creates the packet containing all information
     * @param registries
     * @param levelData
     * @param blockedItemsRegistries
     * @param blockedBlocksRegistries
     * @param icons
     * @param translationData
     */
    public PacketUpdateClientJobsData(Set<XPRegistry<? extends XPData>> registries,
                                    LevelData levelData,
                                      Set<ItemBlockedRegistry> blockedItemsRegistries,
                                      Set<BlockBlockedRegistry> blockedBlocksRegistries,
                                      Map<String, byte[]> icons,
                                      TranslationData translationData){
        this.registries = registries;
        this.JOBS_LEVELS = levelData;
        this.BLOCKED_ITEMS_REGISTRIES = blockedItemsRegistries;
        this.BLOCKED_BLOCKS_REGISTRIES = blockedBlocksRegistries;
        this.JOBS_ICONS = icons;
        this.TRANSLATIONS = translationData;
    }


    /**
     * Reads the packet from the buffer
     * @param buf the buffer to read
     * @return the packet read
     */
    public static PacketUpdateClientJobsData fromBytes(FriendlyByteBuf buf) {
        Set<XPRegistry<? extends XPData>> registries = new HashSet<>();
        int amount = buf.readInt();
        for(int i = 0; i < amount; i++)
            registries.add(XPRegistry.fromBytes(buf));
        LevelData levelData = new LevelData(buf);
        Set<ItemBlockedRegistry> blockedItemsRegistries = new HashSet<>();
        Set<BlockBlockedRegistry> blockedBlocksRegistries = new HashSet<>();
        int size1 = buf.readInt();
        for(int i = 0; i < size1; i++)
            blockedItemsRegistries.add(new ItemBlockedRegistry(buf));
        int size2 = buf.readInt();
        for(int i = 0; i < size2; i++)
            blockedBlocksRegistries.add(new BlockBlockedRegistry(buf));
        Map<String, byte[]> icons = JobsIconUtil.fromBytes(buf);
        TranslationData translationData = new TranslationData(buf);
    	return new PacketUpdateClientJobsData(registries, levelData, blockedItemsRegistries, blockedBlocksRegistries, icons, translationData);
    }


    /**
     * Writes the packet to the buffer
     * @param packet the packet to write
     * @param buf the buffer where to write
     */
    public static void toBytes(PacketUpdateClientJobsData packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.registries.size());
        for(XPRegistry<? extends XPData> registry : packet.registries)
            registry.writeToBytes(buf);
        packet.JOBS_LEVELS.writeToBytes(buf);
        buf.writeInt(packet.BLOCKED_ITEMS_REGISTRIES.size());
        for(ItemBlockedRegistry r : packet.BLOCKED_ITEMS_REGISTRIES)
            r.writeToBytes(buf);
        buf.writeInt(packet.BLOCKED_BLOCKS_REGISTRIES.size());
        for(BlockBlockedRegistry r : packet.BLOCKED_BLOCKS_REGISTRIES)
            r.writeToBytes(buf);
        JobsIconUtil.toBytes(buf, packet.JOBS_ICONS);
        packet.TRANSLATIONS.writeToBytes(buf);
    }


    /**
     * Handles the packet on the client side by putting all the information in ClientJobsdata
     * @param message the packet to handle
     * @param ctx the context of the packet
     */
    public static void handle(PacketUpdateClientJobsData message, Supplier<Context> ctx)
    {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            ClientJobsData.XP_REGISTRIES = message.registries;
            ClientJobsData.JOBS_ICONS = JobsIconUtil.convertToClient(message.JOBS_ICONS);
            ClientJobsData.JOBS_LEVELS = message.JOBS_LEVELS;
            ClientJobsData.TRANSLATIONS = message.TRANSLATIONS;
            for(ItemBlockedRegistry r : message.BLOCKED_ITEMS_REGISTRIES)
                ClientJobsData.BLOCKED_ITEMS_REGISTRIES.put(r.getType(), r);
            for(BlockBlockedRegistry r : message.BLOCKED_BLOCKS_REGISTRIES)
                ClientJobsData.BLOCKED_BLOCKS_REGISTRIES.put(r.getType(), r);
        }
        ctx.get().setPacketHandled(true);
    }
}
