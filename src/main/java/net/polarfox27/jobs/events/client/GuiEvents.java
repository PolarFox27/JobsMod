package net.polarfox27.jobs.events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.GuiGainXP;
import net.polarfox27.jobs.util.JobsUtil.Pair;


@EventBusSubscriber
public class GuiEvents {

    /**
     * Render the Gain XP interface if needed. Is executed at every frame.
     * @param e the Render Event
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void clientTick(RenderGameOverlayEvent e) {
        if(Minecraft.getMinecraft().player == null)
            return;
        if(e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ClientJobsData.addXPInfos.update();
            if(!ClientJobsData.addXPInfos.shouldShow())
                return;
            Pair<String, Long> toShow = ClientJobsData.addXPInfos.showJobAtTime();
            if(ClientJobsData.playerJobs.isMax(toShow.getFirst()))
                return;
            new GuiGainXP(toShow.getFirst(), toShow.getSecond()).drawScreen();
        }
    }

    /**
     * Cancels the opening of the regular crafting interface and opens the custom one from the Jobs mod.
     * @param e the Click Event
     */
    @SubscribeEvent
    public static void onOpenCraftingTable(RightClickBlock e)
    {
        if(e.getWorld().getBlockState(e.getPos()).getBlock() == Blocks.CRAFTING_TABLE)
        {
            e.setCanceled(true);
            if(!e.getWorld().isRemote)
            {
                e.getEntityPlayer().openGui(ModJobs.instance, ModJobs.ID_GUI_CRAFTING, e.getWorld(),
                        e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
            }
        }
    }
}
