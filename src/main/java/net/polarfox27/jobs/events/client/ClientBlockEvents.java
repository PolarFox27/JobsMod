package net.polarfox27.jobs.events.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.registry.unlock.BlockBlockedRegistry;
import net.polarfox27.jobs.data.registry.unlock.BlockedData;
import net.polarfox27.jobs.data.registry.unlock.ItemBlockedRegistry;

@Mod.EventBusSubscriber
public class ClientBlockEvents {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void canBreakBlock(PlayerEvent.BreakSpeed event) {
        BlockBlockedRegistry blocked_blocks = ClientJobsData.BLOCKED_BLOCKS_REGISTRIES.get(BlockedData.Type.BREAKING);
        ItemBlockedRegistry blocked_tools = ClientJobsData.BLOCKED_ITEMS_REGISTRIES.get(BlockedData.Type.LEFT_CLICK);

        if(blocked_blocks != null && !blocked_blocks.isAllowed(ClientJobsData.playerJobs, event.getState())){
            event.setCanceled(true);
        }
        if(blocked_tools != null && blocked_tools.isBlocked(ClientJobsData.playerJobs,
                event.getPlayer().getMainHandItem())){
            event.setCanceled(true);
        }
    }
}
