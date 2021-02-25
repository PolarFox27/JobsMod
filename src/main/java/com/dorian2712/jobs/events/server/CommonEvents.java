package com.dorian2712.jobs.events.server;

import com.dorian2712.jobs.data.GainXPUtil;
import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.network.PacketUpdateClientJob;
import com.dorian2712.jobs.data.PlayerList;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.handlers.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;


@EventBusSubscriber
public class CommonEvents {

    @SubscribeEvent
    public static void playerConnectedEvent(PlayerLoggedInEvent event)
    {
        if(event.player instanceof EntityPlayerMP)
        {
            String uuid = event.player.getGameProfile().getId().toString();
            if(!PlayerList.containPlayer(uuid))
                PlayerList.addNewPlayer(uuid);

            if(PlayerList.containPlayer(uuid))
            {
                PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(uuid), (EntityPlayerMP) event.player);
                GainXPUtil.sendDataToClient((EntityPlayerMP) event.player);
            }
        }
    }
}
