package net.polarfox27.jobs.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

import java.util.function.Supplier;

public class PacketUpdateClientJob{

    private PlayerJobs jobs = null;

    public PacketUpdateClientJob(){}
    public PacketUpdateClientJob(PlayerJobs jobs) {
        this.jobs = jobs;
    }



    public static PacketUpdateClientJob fromBytes(PacketBuffer buf) {
    	return new PacketUpdateClientJob(new PlayerJobs(buf));
    }


    public static void toBytes(PacketUpdateClientJob packet, PacketBuffer buf) {
        packet.jobs.writeToBytes(buf);
    }

    public static void handle(PacketUpdateClientJob message, Supplier<Context> ctx) {
        if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ClientJobsData.playerJobs = message.jobs;
        }
        ctx.get().setPacketHandled(true);
    }
}
