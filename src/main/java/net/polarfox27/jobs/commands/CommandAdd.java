package net.polarfox27.jobs.commands;

import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.network.PacketUpdateClientJob;
import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.polarfox27.jobs.util.handlers.PacketHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandAdd extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "jobs-add";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/jobs-add [player] [job] [xp]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                        String[] args) throws CommandException {
        if(args.length != 3)
            throw new CommandException(this.getUsage(sender));
        EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
        String job = args[1];
        long xp = Long.parseLong(args[2]);
        if(target == null)
            throw new CommandException(this.getUsage(sender));

        PlayerJobs jobs =  PlayerData.getPlayerJobs(target);
        jobs.gainXP(job, xp, target);
        sender.sendMessage(new TextComponentString(xp + " xp added to " + target.getName() + " for job " + job));
        PacketHandler.INSTANCE.sendTo(new PacketUpdateClientJob(jobs), target);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                                          String[] args, @Nullable BlockPos targetPos) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers()) {
                completions.add(p.getName());
            }
        }
        return completions;
    }
}
