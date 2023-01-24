package net.polarfox27.jobs.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.util.handler.PacketHandler;

import java.util.function.Supplier;

public class PacketAskClientUpdate{

    public static final PacketAskClientUpdate instance = new PacketAskClientUpdate();


    /**
     * Reads the packet from the buffer (actually returns the default instance)
     * @param buf the buffer to read
     * @return the default instance
     */
    public static PacketAskClientUpdate fromBytes(PacketBuffer buf){
        return instance;
    }


    /**
     * Writes the packet to the buffer
     * @param packet the packet to write
     * @param buf the buffer where to write
     */
    public static void toBytes(PacketAskClientUpdate packet, PacketBuffer buf){
    }

    /**
     * Handles the packet on the server by sending back the jobs to the client
     * @param message the packet to handle
     * @param ctx the context of the packet
     */
    public static void handle(PacketAskClientUpdate message, Supplier<Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER &&
            ctx.get().getSender() != null) {
            PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(PlayerData.getPlayerJobs(ctx.get().getSender())),
                                          ctx.get().getNetworkManager(),
                                          NetworkDirection.PLAY_TO_CLIENT);
        }
        ctx.get().setPacketHandled(true);
    }
}
