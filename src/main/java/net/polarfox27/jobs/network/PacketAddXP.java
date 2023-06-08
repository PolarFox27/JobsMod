package net.polarfox27.jobs.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PacketAddXP{

    private final String job;
    private final long xpAdded;

    /**
     * Constructs a packet containing the xp added for a job
     * @param j the job to which xp is added
     * @param xp the amount of xp added
     */
    public PacketAddXP(String j, long xp)
    {
        this.job = j;
        this.xpAdded = xp;
    }

    /**
     * Reads the packet from the buffer
     * @param buf the buffer to read
     * @return the packet read
     */
    public static PacketAddXP fromBytes(FriendlyByteBuf buf)
    {
        int length = buf.readInt();
    	return new PacketAddXP(buf.readUtf(length), buf.readLong());
	}


    /**
     * Writes the packet to the buffer
     * @param packet the packet to write
     * @param buf the buffer where to write
     */
    public static void toBytes(PacketAddXP packet, FriendlyByteBuf buf)
    {
        buf.writeInt(packet.job.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(packet.job);
        buf.writeLong(packet.xpAdded);
    }


    /**
     * Handles the packet on the client side by showing the gain xp GUI
     * @param message the packet to handle
     * @param ctx the context of the packet
     */
    public static void handle(PacketAddXP message, Supplier<Context> ctx)
    {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
        {
            ClientJobsData.showAddGui(message.job, message.xpAdded);
        }
        ctx.get().setPacketHandled(true);
    }
}
