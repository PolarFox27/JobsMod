package net.polarfox27.jobs.events.client;

import net.polarfox27.jobs.ModJobs;
import net.polarfox27.jobs.gui.GuiGainXP;
import net.polarfox27.jobs.data.ClientInfos;
import net.polarfox27.jobs.gui.screens.GuiLevelUp;
import net.polarfox27.jobs.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber
public class GuiEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void clientTick(RenderGameOverlayEvent e)
    {
        if(Minecraft.getMinecraft().player == null) return;
        if(e.getType() == RenderGameOverlayEvent.ElementType.ALL)
        {
            if(ClientInfos.addXPInfos == null) return;
            if(ClientInfos.job.getLevelByJob(ClientInfos.addXPInfos.job) >= 25) return;
            if(ClientInfos.addXPInfos.ticks <= System.currentTimeMillis())
            {
                ClientInfos.addXPInfos = null;
                return;
            }
            new GuiGainXP(ClientInfos.addXPInfos.job, ClientInfos.addXPInfos.xpAdded);
        }
    }

    @SubscribeEvent
    public static void onOpenCraftingTable(RightClickBlock e)
    {
        if(e.getWorld().getBlockState(e.getPos()).getBlock() == Blocks.CRAFTING_TABLE)
        {
            e.setCanceled(true);
            if(!e.getWorld().isRemote)
            {
                e.getEntityPlayer().openGui(ModJobs.instance, Reference.GUI_CRAFTING, e.getWorld(), e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onGuiOpen(GuiOpenEvent e)
    {
        if(!(e.getGui() instanceof GuiLevelUp))
            ClientInfos.CURRENT_REWARDS.clear();
    }

}
