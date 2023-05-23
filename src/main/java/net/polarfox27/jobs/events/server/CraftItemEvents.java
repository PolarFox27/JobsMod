package net.polarfox27.jobs.events.server;

import net.polarfox27.jobs.data.JobsInfo;
import net.polarfox27.jobs.data.PlayerData;
import net.polarfox27.jobs.util.Constants.Job;
import net.polarfox27.jobs.data.GainXPUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

@EventBusSubscriber
public class CraftItemEvents {


    @SubscribeEvent
    public static void onCraft(ItemCraftedEvent event)
    {
    	if(event.player.world.isRemote) return;
        int count = event.crafting.getCount();
        Item item = event.crafting.getItem();

        if(GainXPUtil.CRAFT_ITEM_JOB.containsKey(item))
        {
            Job j = GainXPUtil.CRAFT_ITEM_JOB.get(item);
            JobsInfo infos = PlayerData.getPlayerJobs(event.player);
            long xp = GainXPUtil.CRAFT_ITEM_XP.get(item)[infos.getLevelByJob(j)];

            infos.gainXP(j, xp * count, (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onSmelt(ItemSmeltedEvent event)
    {
    	if(event.player.world.isRemote) return;
        int count = event.smelting.getCount();
        Item item = event.smelting.getItem();

        if(GainXPUtil.SMELT_ITEM_JOB.containsKey(item))
        {
            Job j = GainXPUtil.SMELT_ITEM_JOB.get(item);
            JobsInfo infos = PlayerData.getPlayerJobs(event.player);
            long xp = GainXPUtil.SMELT_ITEM_XP.get(item)[infos.getLevelByJob(j)];

            infos.gainXP(j, xp * count, (EntityPlayerMP) event.player);
        }
    }

    
}
