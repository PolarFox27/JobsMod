package net.polarfox27.jobs.network;


import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.*;
import net.polarfox27.jobs.util.config.JobsIconUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class PacketUpdateClientJobsData{

    public Set<XPRegistry<? extends XPData>> registries = new HashSet<>();
    public LevelData JOBS_LEVELS = null;
    public BlockedCraftsData BLOCKED_CRAFTS = null;
    public BlockedBlocksData BLOCKED_BLOCKS = null;
    Map<String, byte[]> JOBS_ICONS = new HashMap<>();

    TranslationData TRANSLATIONS = null;

    public PacketUpdateClientJobsData(){}

    /**
     * Creates the packet containing all information
     * @param registries
     * @param levelData
     * @param blockedCraftsData
     * @param blockedBlocksData
     * @param icons
     * @param translationData
     */
    public PacketUpdateClientJobsData(Set<XPRegistry<? extends XPData>> registries,
                                    LevelData levelData,
                                      BlockedCraftsData blockedCraftsData,
                                      BlockedBlocksData blockedBlocksData,
                                      Map<String, byte[]> icons,
                                      TranslationData translationData){
        this.registries = registries;
        this.JOBS_LEVELS = levelData;
        this.BLOCKED_CRAFTS = blockedCraftsData;
        this.BLOCKED_BLOCKS = blockedBlocksData;
        this.JOBS_ICONS = icons;
        this.TRANSLATIONS = translationData;
    }


    /**
     * Reads the packet from the buffer
     * @param buf the buffer to read
     * @return the packet read
     */
    public static PacketUpdateClientJobsData fromBytes(PacketBuffer buf) {
        Set<XPRegistry<? extends XPData>> registries = new HashSet<>();
        int amount = buf.readInt();
        for(int i = 0; i < amount; i++)
            registries.add(XPRegistry.fromBytes(buf));
        LevelData levelData = new LevelData(buf);
        BlockedCraftsData blockedCrafts = new BlockedCraftsData(buf);
        BlockedBlocksData blockedBlocks = new BlockedBlocksData(buf);
        Map<String, byte[]> icons = JobsIconUtil.fromBytes(buf);
        TranslationData translationData = new TranslationData(buf);
    	return new PacketUpdateClientJobsData(registries, levelData, blockedCrafts, blockedBlocks, icons, translationData);
    }


    /**
     * Writes the packet to the buffer
     * @param packet the packet to write
     * @param buf the buffer where to write
     */
    public static void toBytes(PacketUpdateClientJobsData packet, PacketBuffer buf) {
        buf.writeInt(packet.registries.size());
        for(XPRegistry<? extends XPData> registry : packet.registries)
            registry.writeToBytes(buf);
        packet.JOBS_LEVELS.writeToBytes(buf);
        packet.BLOCKED_CRAFTS.writeToBytes(buf);
        packet.BLOCKED_BLOCKS.writeToBytes(buf);
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
            ClientJobsData.BLOCKED_CRAFTS = message.BLOCKED_CRAFTS;
            ClientJobsData.BLOCKED_BLOCKS = message.BLOCKED_BLOCKS;
        }
        ctx.get().setPacketHandled(true);
    }
}
