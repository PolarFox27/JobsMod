package net.polarfox27.jobs.network;

import net.polarfox27.jobs.data.ClientInfos;
import net.polarfox27.jobs.data.ModConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSendClientConfig implements IMessage{
	
	private ModConfig config;
	public PacketSendClientConfig() {}
	
	public PacketSendClientConfig(ModConfig config) 
	{
		this.config = config;
	}
	
	
	public static void toBytes(PacketBuffer buf, PacketSendClientConfig packet)
	{
		
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        this.config = new ModConfig().setHardMode(buf.readBoolean());
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeBoolean(this.config.isHard());
    }

    public static class MessageHandler implements IMessageHandler<PacketSendClientConfig, IMessage>
    {

        @Override
        public IMessage onMessage(PacketSendClientConfig message, MessageContext ctx)
        {
            if(ctx.side == Side.CLIENT)
            	ClientInfos.config = message.config;
            return null;
        }
    }

}
