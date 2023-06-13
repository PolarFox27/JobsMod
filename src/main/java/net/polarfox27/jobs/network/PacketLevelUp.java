package net.polarfox27.jobs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.util.JobsUtil;

public class PacketLevelUp implements IMessage {

    private String job = "";

    public PacketLevelUp(){}
    public PacketLevelUp(String j) {
        this.job = j;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.job = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.job);
    }

    public static class MessageHandler implements IMessageHandler<PacketLevelUp, IMessage> {

        @Override
        public IMessage onMessage(PacketLevelUp message, MessageContext ctx) {
            if(ctx.side == Side.CLIENT) {
                if(Minecraft.getMinecraft().player == null) return null;
                ClientJobsData.showLevelUpGui(message.job);
            }
            return null;
        }
    }
}
