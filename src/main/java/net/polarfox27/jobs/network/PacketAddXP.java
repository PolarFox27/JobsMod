package net.polarfox27.jobs.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PacketAddXP{

    private String job;
    private long xpAdded;
    public PacketAddXP(){}
    public PacketAddXP(String j, long xp)
    {
        this.job = j;
        this.xpAdded = xp;
    }

    public static PacketAddXP fromBytes(PacketBuffer buf)
    {
        int length = buf.readInt();
    	return new PacketAddXP(buf.readUtf(length), buf.readLong());
	}


    public static void toBytes(PacketAddXP packet, PacketBuffer buf)
    {
        buf.writeInt(packet.job.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(packet.job);
        buf.writeLong(packet.xpAdded);
    }


    public static void handle(PacketAddXP message, Supplier<Context> ctx)
    {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            ClientJobsData.showAddGui(message.job, message.xpAdded);
        }
        ctx.get().setPacketHandled(true);
    }
}
