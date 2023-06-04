package net.polarfox27.jobs.util.handlers;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.network.*;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModJobs.MOD_ID);

    public static void registerPackets() {
        INSTANCE.registerMessage(PacketUpdateClientJob.MessageHandler.class, PacketUpdateClientJob.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(PacketAskClientUpdate.MessageHandler.class, PacketAskClientUpdate.class, 1, Side.SERVER);
        INSTANCE.registerMessage(PacketAddXP.PacketAddXPHandler.class, PacketAddXP.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(PacketUpdateClientJob.MessageHandler.class, PacketUpdateClientJob.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(PacketLevelUp.MessageHandler.class, PacketLevelUp.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(PacketSendRewardsClient.MessageHandler.class, PacketSendRewardsClient.class, 5, Side.CLIENT);
        INSTANCE.registerMessage(PacketUpdateClientJobsData.MessageHandler.class, PacketUpdateClientJobsData.class, 6, Side.CLIENT);
    }
}
