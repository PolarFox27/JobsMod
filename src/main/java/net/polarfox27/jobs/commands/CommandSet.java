package net.polarfox27.jobs.commands;

import net.polarfox27.jobs.data.ServerJobsData;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandSet extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "jobs-set";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/jobs-set [player] [job] [level] [xp]\n/jobs-set [player] [job] [total xp]";
    }

    @Override
    public int getRequiredPermissionLevel() {return 2;}

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                        String[] args) throws CommandException {
        EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
        String job = args[1];
        if(target == null)
            throw new CommandException(this.getUsage(sender));
        if(args.length == 3) {
            long total = Long.parseLong(args[2]);
            setJob(target, job, total);
        }
        else if(args.length == 4) {
            int lvl = Integer.parseInt(args[2]);
            long xp = Long.parseLong(args[3]);
            long total = ServerJobsData.JOBS_LEVELS.getTotalXPForLevel(job, lvl) + xp;
            setJob(target, job, total);
        }
        else
            throw new CommandException(this.getUsage(sender));
    }

    private void setJob(EntityPlayerMP target, String j, long xp) {
        PlayerJobs jobs = PlayerData.getPlayerJobs(target);
        jobs.set(j, xp);
        PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(jobs), target);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                                          String[] args, @Nullable BlockPos targetPos) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers())
                completions.add(p.getName());
        }
        return completions;
    }
}
