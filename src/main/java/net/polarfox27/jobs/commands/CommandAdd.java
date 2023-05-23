package net.polarfox27.jobs.commands;

import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.data.PlayerData;
import net.polarfox27.jobs.util.Constants.Job;
import net.polarfox27.jobs.util.handlers.PacketHandler;
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

public class CommandAdd extends CommandBase {
    @Override
    public String getName() {
        return "jobs-add";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/jobs-add [player] [job] [xp]";
    }

    @Override
    public int getRequiredPermissionLevel() {return 2; }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length != 3)
        {
            sendError(sender);
            return;
        }
        EntityPlayerMP target = null;
        Job job = null;
        long xp = 0;
        target = server.getPlayerList().getPlayerByUsername(args[0]);
        job = Job.valueOf(args[1]);
        xp = Long.parseLong(args[2]);
        if(target == null)
        {
            sendError(sender);
            return;
        }
        PlayerData.getPlayerJobs(target).gainXP(job, xp, target);
        sender.sendMessage(new TextComponentString(xp + " xp added to " + target.getName() + " for job " + job.name()));
        PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(target), target);



    }


    private void sendError(ICommandSender sender)
    {
        sender.sendMessage(new TextComponentString(TextFormatting.RED + this.getUsage(sender)));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        List<String> completions = new ArrayList<String>();
        if(args.length == 1)
        {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers())
            {
                completions.add(p.getName());
            }
        }
        else if(args.length == 2)
        {
            for(Job j : Job.values())
            {
                if(j != Job.NONE) completions.add(j.name());
            }
        }
        return completions;
    }
}
