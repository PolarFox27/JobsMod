package net.polarfox27.jobs.util.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.network.*;

public class PacketHandler {
	
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation(ModJobs.MOD_ID, "main"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);

	/**
	 * Registers all the packets types
	 */
	public static void registerPackets() {
		INSTANCE.registerMessage(0, PacketAddXP.class, PacketAddXP::toBytes, PacketAddXP::fromBytes, PacketAddXP::handle);
		INSTANCE.registerMessage(1, PacketAskClientUpdate.class, PacketAskClientUpdate::toBytes, PacketAskClientUpdate::fromBytes, PacketAskClientUpdate::handle);
		INSTANCE.registerMessage(2, PacketLevelUp.class, PacketLevelUp::toBytes, PacketLevelUp::fromBytes, PacketLevelUp::handle);
		INSTANCE.registerMessage(3, PacketSendRewardsClient.class, PacketSendRewardsClient::toBytes, PacketSendRewardsClient::fromBytes, PacketSendRewardsClient::handle);
		INSTANCE.registerMessage(4, PacketUpdateClientJobsData.class, PacketUpdateClientJobsData::toBytes, PacketUpdateClientJobsData::fromBytes, PacketUpdateClientJobsData::handle);
		INSTANCE.registerMessage(5, PacketUpdateClientJob.class, PacketUpdateClientJob::toBytes, PacketUpdateClientJob::fromBytes, PacketUpdateClientJob::handle);
		INSTANCE.registerMessage(6, PacketSendChatMessage.class, PacketSendChatMessage::toBytes, PacketSendChatMessage::fromBytes, PacketSendChatMessage::handle);
	}

	/**
	 * Sends a char message to the client
	 * @param player the player who will receive the message
	 * @param message the message to send
	 */
	public static void sendMessageToClient(ServerPlayer player, Component message) {
		sendPacketToClient(player, new PacketSendChatMessage(message));
	}

	/**
	 * Sends a packet to the client
	 * @param player the client who will receive the packet
	 * @param packet the packet to send
	 */
	public static void sendPacketToClient(ServerPlayer player, JobsPacket packet){
		INSTANCE.sendTo(packet,
				player.connection.getConnection(),
				NetworkDirection.PLAY_TO_CLIENT);
	}

}
