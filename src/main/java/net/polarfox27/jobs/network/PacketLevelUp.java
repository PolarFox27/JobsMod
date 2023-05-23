package com.dorian2712.jobs.network;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.util.Constants.Job;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketLevelUp implements IMessage {

    private Job job = Job.NONE;

    public PacketLevelUp(){}
    public PacketLevelUp(Job j)
    {
        this.job = j;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.job = Job.byIndex(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(job.index);
    }

    public static class MessageHandler implements IMessageHandler<PacketLevelUp, IMessage>
    {

        @Override
        public IMessage onMessage(PacketLevelUp message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT)
            {
                if(Minecraft.getMinecraft().player == null) return null;
                ClientInfos.showLevelUpGui(message.job);
            }
            return null;
        }
    }
}
