package com.dorian2712.jobs.commands;

import com.dorian2712.jobs.network.PacketUpdateClientJob;
import com.dorian2712.jobs.data.PlayerList;
import com.dorian2712.jobs.util.Constants;
import com.dorian2712.jobs.util.Constants.Job;
import com.dorian2712.jobs.util.handlers.PacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandSet extends CommandBase {
    @Override
    public String getName()
    {
        return "jobs-set";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/jobs-set [player] [job] [level] [xp]";
    }

    @Override
    public int getRequiredPermissionLevel() {return 2;}

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP target = null;
        if(args.length == 3)
        {
            target = server.getPlayerList().getPlayerByUsername(args[0]);
            Job job = Job.valueOf(args[1]);
            long total = Long.parseLong(args[2]);
            if(target == null)
            {
                sendError(sender);
                return;
            }
            setJob(target, job, total);
        }
        else if(args.length == 4)
        {
            target = server.getPlayerList().getPlayerByUsername(args[0]);
            Job job = Job.valueOf(args[1]);
            int lvl = Integer.parseInt(args[2]);
            long xp = Long.parseLong(args[3]);
            long total = Constants.TOTAL_XP_BY_LEVEL[lvl] + xp;
            if(target == null)
            {
                sendError(sender);
                return;
            }
            setJob(target, job, total);
        }
        else
        {
            sendError(sender);
            return;
        }
    }


    private void sendError(ICommandSender sender)
    {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + this.getUsage(sender)));
    }

    private void setJob(EntityPlayerMP target, Job j, long xp)
    {
        String uuid = target.getGameProfile().getId().toString();
        PlayerList.getJobsOfPlayer(uuid).set(j, xp);
        PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(uuid), target);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        List<String> completions = new ArrayList<>();
        if(args.length == 1)
        {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers())
                completions.add(p.getName());
        }
        else if(args.length == 2)
        {
            for(Job j : Job.values())
                if(j != Job.NONE)
                    completions.add(j.name());
        }

        return completions;
    }
}
