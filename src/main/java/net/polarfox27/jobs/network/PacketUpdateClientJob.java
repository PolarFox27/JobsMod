package net.polarfox27.jobs.network;

import net.polarfox27.jobs.data.ClientJobsData;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

public class PacketUpdateClientJob implements IMessage {

    private PlayerJobs jobs;

    public PacketUpdateClientJob(){}
    public PacketUpdateClientJob(PlayerJobs jobs) {
        this.jobs = jobs;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        this.jobs = new PlayerJobs(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        this.jobs.writeToBytes(buf);
    }

    public static class MessageHandler implements IMessageHandler<PacketUpdateClientJob, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateClientJob message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ClientJobsData.playerJobs = message.jobs;
            }

            return null;
        }
    }
}
