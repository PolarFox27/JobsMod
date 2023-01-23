package net.polarfox27.jobs.events.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.GuiGainXP;
import net.polarfox27.jobs.gui.containers.ContainerCraft;
import net.polarfox27.jobs.gui.screens.GuiLevelUp;

@EventBusSubscriber
public class GuiEvents {


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientTick(RenderGameOverlayEvent e) {
        if(Minecraft.getInstance().player == null)
            return;
        if(e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ClientJobsData.addXPInfos.update();
            if(!ClientJobsData.addXPInfos.shouldShow())
                return;
            Pair<String, Long> toShow = ClientJobsData.addXPInfos.showJobAtTime();
            if(ClientJobsData.playerJobs.isMax(toShow.getFirst()))
                return;
            GuiGainXP gui = new GuiGainXP(toShow.getFirst(), toShow.getSecond());
            gui.render(new MatrixStack(), 0.0f);
        }
    }

    @SubscribeEvent
    public void onOpenCraftingTable(RightClickBlock e) {
        if(e.getWorld().getBlockState(e.getPos()).getBlock() == Blocks.CRAFTING_TABLE) {
            e.setCanceled(true);
            if(!e.getWorld().isClientSide) {
                openUpdatedCraftingTable(e);
            }
        }
    }


    private void openUpdatedCraftingTable(RightClickBlock e){
        e.getPlayer().openMenu(new INamedContainerProvider() {

            @Override
            public Container createMenu(int index, PlayerInventory inventory, PlayerEntity player) {
                return new ContainerCraft(index, inventory, IWorldPosCallable.create(e.getWorld(), e.getPos()));
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("container.crafting");
            }
        });
    }


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onGuiOpen(GuiOpenEvent e) {
        if(!(e.getGui() instanceof GuiLevelUp))
            ClientJobsData.CURRENT_REWARDS.clear();
    }

}
