package com.dorian2712.jobs.events.server;

import com.dorian2712.jobs.data.ClientInfos;
import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerData;
import com.dorian2712.jobs.data.GainXPUtil;
import com.dorian2712.jobs.util.Constants.Job;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@EventBusSubscriber
public class BreakBlockEvents {

    @SubscribeEvent
    public static void onBreakOreOrCrop(BreakEvent event)
    {
    	if(event.getWorld().isRemote) return;
        if(!(event.getPlayer() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        Block block = event.getState().getBlock();
        JobsInfo jobs = PlayerData.getPlayerJobs(player);

        //Ores
        if(GainXPUtil.BREAK_BLOCK_XP.containsKey(block))
        {
            long xp = GainXPUtil.BREAK_BLOCK_XP.get(block)
                    [jobs.getLevelByJob(GainXPUtil.BREAK_BLOCK_JOB.get(block))];

            if(!(block instanceof BlockStone))
                jobs.gainXP(GainXPUtil.BREAK_BLOCK_JOB.get(block), xp, player);
            else if(event.getState().getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE)
                jobs.gainXP(GainXPUtil.BREAK_BLOCK_JOB.get(block), xp, player);
        }

        //Crops
        else if(GainXPUtil.HARVEST_CROP_XP.containsKey(getItemsFromCrops().get(block)) && block instanceof BlockCrops && getItemsFromCrops().containsKey(block))
        {
            if(!((BlockCrops)block).isMaxAge(event.getState())) return;

            long xp = GainXPUtil.HARVEST_CROP_XP.get(getItemsFromCrops().get(block))
                    [jobs.getLevelByJob(GainXPUtil.HARVEST_CROP_JOB.get(getItemsFromCrops().get(block)))];
            jobs.gainXP(GainXPUtil.HARVEST_CROP_JOB.get(getItemsFromCrops().get(block)), xp, player);
        }

    }


    public static Map<Block, Item> getItemsFromCrops()
    {
        Map<Block, Item> map = new HashMap<>();
        map.put(Blocks.WHEAT, Items.WHEAT);
        map.put(Blocks.POTATOES, Items.POTATO);
        map.put(Blocks.CARROTS, Items.CARROT);
        map.put(Blocks.BEETROOTS, Items.BEETROOT);
        return map;
    }
}
