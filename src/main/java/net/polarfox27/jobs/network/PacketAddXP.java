package net.polarfox27.jobs.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.util.JobsUtil;

public class PacketAddXP implements IMessage {

    private String job;
    private long xpAdded;
    public PacketAddXP(){}
    public PacketAddXP(String j, long xp) {
        this.job = j;
        this.xpAdded = xp;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.job = ByteBufUtils.readUTF8String(buf);
        this.xpAdded = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.job);
        buf.writeLong(this.xpAdded);
    }

    public static class PacketAddXPHandler implements IMessageHandler<PacketAddXP, IMessage> {

        @Override
        public IMessage onMessage(PacketAddXP message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT)
            {
                ClientJobsData.showAddGui(message.job, message.xpAdded);
            }
            return null;
        }
    }
}
