package com.dorian2712.jobs.network;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUpdateClientJob implements IMessage {

    private long[] xps = new long[]{0, 0, 0, 0};

    public PacketUpdateClientJob(){}
    public PacketUpdateClientJob(EntityPlayer player)
    {
        this.xps = PlayerData.getPlayerJobs(player).toTotalXPs();
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        for(int i = 0; i < 4; i++)
            this.xps[i] = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        for(int i = 0; i < 4; i++)
            buf.writeLong(xps[i]);
    }

    public static class MessageHandler implements IMessageHandler<PacketUpdateClientJob, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateClientJob message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                ClientInfos.job = new JobsInfo().fromTotalXPs(message.xps);
            }

            return null;
        }
    }
}
