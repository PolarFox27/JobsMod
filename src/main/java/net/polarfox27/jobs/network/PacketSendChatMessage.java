package net.polarfox27.jobs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class PacketSendChatMessage {
	
	private final ITextComponent message;
	public PacketSendChatMessage(ITextComponent message) {
		this.message = message;
	}
	
	
	public static void toBytes(PacketSendChatMessage packet, PacketBuffer buf) {
		buf.writeComponent(packet.message);
	}
	
	public static PacketSendChatMessage fromBytes(PacketBuffer buf) {
		return new PacketSendChatMessage(buf.readComponent());
	}
	
	public static void handle(PacketSendChatMessage packet, Supplier<Context> ctx) {
		if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			Minecraft.getInstance().gui.getChat().addMessage(packet.message);
		ctx.get().setPacketHandled(true);
	}

}
