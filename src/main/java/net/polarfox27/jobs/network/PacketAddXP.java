package net.polarfox27.jobs.network;

import net.polarfox27.jobs.data.ClientInfos;
import net.polarfox27.jobs.util.Constants.Job;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAddXP implements IMessage {

    private int job;
    private long xpAdded;
    public PacketAddXP(){}
    public PacketAddXP(Job j, long xp)
    {
        this.job = j.index;
        this.xpAdded = xp;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.job = buf.readInt();
        this.xpAdded = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.job);
        buf.writeLong(this.xpAdded);
    }

    public static class PacketAddXPHandler implements IMessageHandler<PacketAddXP, IMessage> {

        @Override
        public IMessage onMessage(PacketAddXP message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT)
            {
                ClientInfos.showAddGui(Job.byIndex(message.job), message.xpAdded);
            }
            return null;
        }
    }
}
