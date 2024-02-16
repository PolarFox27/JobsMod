package net.polarfox27.jobs.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class PacketSendChatMessage implements JobsPacket {
	
	private final Component message;

	/**
	 * Constructs a packet containing a message
	 * @param message the message contained
	 */
	public PacketSendChatMessage(Component message) {
		this.message = message;
	}


	/**
	 * Writes the packet to the buffer
	 * @param packet the packet to write
	 * @param buf the buffer where to write
	 */
	public static void toBytes(PacketSendChatMessage packet, FriendlyByteBuf buf) {
		buf.writeComponent(packet.message);
	}

	/**
	 * Reads the packet from the buffer
	 * @param buf the buffer to read
	 * @return the packet read
	 */
	public static PacketSendChatMessage fromBytes(FriendlyByteBuf buf) {
		return new PacketSendChatMessage(buf.readComponent());
	}

	/**
	 * Handles the packet on the client side by sending a chat message locally
	 * @param message the packet to handle
	 * @param ctx the context of the packet
	 */
	public static void handle(PacketSendChatMessage message, Supplier<Context> ctx) {
		if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
			Minecraft.getInstance().gui.getChat().addMessage(message.message);
		ctx.get().setPacketHandled(true);
	}

}
