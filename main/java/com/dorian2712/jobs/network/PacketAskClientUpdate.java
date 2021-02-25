package com.dorian2712.jobs.network;

import com.dorian2712.jobs.util.handlers.PacketHandler;
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
                String uuid = ctx.getServerHandler().player.getGameProfile().getId().toString();
                PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(uuid), ctx.getServerHandler().player);
            }

            return null;
        }
    }
}
