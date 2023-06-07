package net.polarfox27.jobs.network;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.LevelData;
import net.polarfox27.jobs.data.registry.TranslationData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;
import net.polarfox27.jobs.data.registry.xp.XPData;
import net.polarfox27.jobs.data.registry.xp.XPRegistry;
import net.polarfox27.jobs.util.config.JobsIconUtil;

public class PacketUpdateClientJobsData implements IMessage {

    public Set<XPRegistry<? extends XPData>> registries;
    public LevelData JOBS_LEVELS;
    public Set<ItemBlockedRegistry> BLOCKED_ITEMS_REGISTRIES;
    public Set<BlockBlockedRegistry> BLOCKED_BLOCKS_REGISTRIES;
    Map<String, byte[]> JOBS_ICONS;

    TranslationData TRANSLATIONS;


    /**
     * empty constructor
     */
    public PacketUpdateClientJobsData(){}

    /**
     * Creates the packet containing all information
     * @param registries xp registries
     * @param levelData levels and xp
     * @param blockedItemsRegistries blocked items registries
     * @param blockedBlocksRegistries blocked blocks registries
     * @param icons jobs icons
     * @param translationData translations
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


    @Override
    public void fromBytes(ByteBuf buf) {
        this.registries = new HashSet<>();
        int amount = buf.readInt();
        for(int i = 0; i < amount; i++)
            registries.add(XPRegistry.fromBytes(buf));
        System.out.println("XP Registries loaded");
        this.JOBS_LEVELS = new LevelData(buf);
        System.out.println("Levels loaded : " + Arrays.toString(JOBS_LEVELS.getJobs().toArray()));
        this.BLOCKED_ITEMS_REGISTRIES = new HashSet<>();
        this.BLOCKED_BLOCKS_REGISTRIES = new HashSet<>();
        int size1 = buf.readInt();
        for(int i = 0; i < size1; i++)
            BLOCKED_ITEMS_REGISTRIES.add(new ItemBlockedRegistry(buf));
        System.out.println("Blocked Items Registries loaded");
        int size2 = buf.readInt();
        for(int i = 0; i < size2; i++)
            BLOCKED_BLOCKS_REGISTRIES.add(new BlockBlockedRegistry(buf));
        System.out.println("Blocked Blocks Registries loaded");

        this.JOBS_ICONS = JobsIconUtil.fromBytes(buf);
        System.out.println("Icons loaded");
        this.TRANSLATIONS = new TranslationData(buf);
        System.out.println("Translations loaded");
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(registries.size());
        for(XPRegistry<? extends XPData> registry : registries)
            registry.writeToBytes(buf);
        JOBS_LEVELS.writeToBytes(buf);
        buf.writeInt(BLOCKED_ITEMS_REGISTRIES.size());
        for(ItemBlockedRegistry r : BLOCKED_ITEMS_REGISTRIES)
            r.writeToBytes(buf);
        buf.writeInt(BLOCKED_BLOCKS_REGISTRIES.size());
        for(BlockBlockedRegistry r : BLOCKED_BLOCKS_REGISTRIES)
            r.writeToBytes(buf);
        JobsIconUtil.toBytes(buf, JOBS_ICONS);
        TRANSLATIONS.writeToBytes(buf);
    }



    public static class MessageHandler implements IMessageHandler<PacketUpdateClientJobsData, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateClientJobsData message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT) {
                ClientJobsData.XP_REGISTRIES = message.registries;
                ClientJobsData.JOBS_ICONS = message.JOBS_ICONS;
                ClientJobsData.JOBS_LEVELS = message.JOBS_LEVELS;
                ClientJobsData.TRANSLATIONS = message.TRANSLATIONS;
                for(ItemBlockedRegistry r : message.BLOCKED_ITEMS_REGISTRIES)
                    ClientJobsData.BLOCKED_ITEMS_REGISTRIES.put(r.getType(), r);
                for(BlockBlockedRegistry r : message.BLOCKED_BLOCKS_REGISTRIES)
                    ClientJobsData.BLOCKED_BLOCKS_REGISTRIES.put(r.getType(), r);
            }
            return null;
        }
    }
}
