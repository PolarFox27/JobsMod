package net.polarfox27.jobs.events.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.gui.GuiGainXP;

@EventBusSubscriber
public class GuiEvents {

    /**
     * Render the Gain XP interface if needed. Is executed at every frame.
     * @param e the Render Event
     */
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
            gui.render(new MatrixStack());
        }
    }
}
