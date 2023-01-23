package net.polarfox27.jobs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.polarfox27.jobs.data.ClientJobsData;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PacketLevelUp{

    private String job;

    public PacketLevelUp(){}
    public PacketLevelUp(String j) {
        this.job = j;
    }

    public static PacketLevelUp fromBytes(PacketBuffer buf) {
        int length = buf.readInt();
        return new PacketLevelUp(buf.readUtf(length));
    }

    public static void toBytes(PacketLevelUp msg, PacketBuffer buf) {
        buf.writeInt(msg.job.getBytes(StandardCharsets.UTF_8).length);
        buf.writeUtf(msg.job);
    }

    public static void handle(PacketLevelUp message, Supplier<Context> ctx) {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            if(Minecraft.getInstance().player == null)
                return;
            ClientJobsData.showLevelUpGui(message.job);
        }
        ctx.get().setPacketHandled(true);
    }
}
