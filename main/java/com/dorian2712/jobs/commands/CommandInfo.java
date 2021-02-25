package com.dorian2712.jobs.commands;

import com.dorian2712.jobs.data.JobsInfo;
import com.dorian2712.jobs.data.PlayerList;
import com.dorian2712.jobs.util.Constants.Job;
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

public class CommandInfo extends CommandBase {

    @Override
    public String getName()
    {
        return "jobs-info";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/jobs-info [player]";
    }

    @Override
    public int getRequiredPermissionLevel() {return 2;}

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(sender instanceof EntityPlayerMP)
        {
            if(args.length == 0)
                printInfoOfPlayer(sender, (EntityPlayerMP)sender);
            else if(args.length == 1)
            {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
                if(target == null)
                {
                    sendError(sender);
                    return;
                }
                printInfoOfPlayer(sender, target);
            }
            else sendError(sender);
        }
        else
        {
            if(args.length != 1) sendError(sender);
            else
            {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
                if(target == null)
                {
                    sendError(sender);
                    return;
                }
                printInfoOfPlayer(sender, target);
            }
        }
    }

    private void printInfoOfPlayer(ICommandSender sender, EntityPlayerMP target)
    {
        String uuid = target.getGameProfile().getId().toString();
        JobsInfo infos = PlayerList.getJobsOfPlayer(uuid);
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Stats of " + TextFormatting.BLUE + target.getName()));

        for(int i = 0; i < 4; i++)
        {
            int lvl = infos.getLevelByJob(Job.byIndex(i));
            long xp = infos.getXPByJob(Job.byIndex(i));
            sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + Job.byIndex(i).name() + " : lvl " +
                    TextFormatting.BLUE + lvl + TextFormatting.LIGHT_PURPLE + ", xp " + TextFormatting.BLUE + xp));
        }
    }

    private void sendError(ICommandSender sender)
    {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + this.getUsage(sender)));
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
        return completions;
    }
}
