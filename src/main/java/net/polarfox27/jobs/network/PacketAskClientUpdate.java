package net.polarfox27.jobs.network;

import net.polarfox27.jobs.util.handlers.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAskClientUpdate implements IMessage {


    public PacketAskClientUpdate(){}


    @Override
    public void fromBytes(ByteBuf buf){}

    @Override
    public void toBytes(ByteBuf buf){}

    public static class MessageHandler implements IMessageHandler<PacketAskClientUpdate, IMessage> {

        @Override
        public IMessage onMessage(PacketAskClientUpdate message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(ctx.getServerHandler().player), ctx.getServerHandler().player);
            }

            return null;
        }
    }
}
