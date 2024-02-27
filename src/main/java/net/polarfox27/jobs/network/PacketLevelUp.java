package net.polarfox27.jobs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PacketLevelUp implements JobsPacket{

    private final String job;
    private final int previousLevel;

    /**
     * Constructs a packet
     * @param j the job contained in the packet
     */
    public PacketLevelUp(String j, int previousLevel) {
        this.job = j;
        this.previousLevel = previousLevel;
    }

    /**
     * Reads the packet from the buffer
     * @param buf the buffer to read
     * @return the packet read
     */
    public static PacketLevelUp fromBytes(FriendlyByteBuf buf) {
        int previous = buf.readInt();
        int length = buf.readInt();
        return new PacketLevelUp(buf.readUtf(length), previous);
    }

    /**
     * Writes the packet to the buffer
     * @param packet the packet to write
     * @param buf the buffer where to write
     */
    public static void toBytes(PacketLevelUp packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.previousLevel);
        buf.writeInt(packet.job.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(packet.job);
    }

    /**
     * Handles the packet on the client side by showing the Level Up GUI
     * @param message the packet to handle
     * @param ctx the context of the packet
     */
    public static void handle(PacketLevelUp message, Supplier<Context> ctx) {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            if(Minecraft.getInstance().player == null)
                return;
            Minecraft.getInstance().execute(() ->
                    ClientJobsData.showLevelUpGui(message.job, message.previousLevel));
        }
        ctx.get().setPacketHandled(true);
    }
}
