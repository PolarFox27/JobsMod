package com.dorian2712.jobs.events.client;

import com.dorian2712.jobs.ModJobs;
import com.dorian2712.jobs.gui.GuiGainXP;
import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.gui.screens.GuiLevelUp;
import com.dorian2712.jobs.util.Reference;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
