package net.polarfox27.jobs.commands;

import net.polarfox27.jobs.data.capabilities.PlayerData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandInfo extends CommandBase {

    @Nonnull
    @Override
    public String getName() {
        return "jobs-info";
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/jobs-info [target]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                        @Nonnull String[] args) throws CommandException {
        if(sender instanceof EntityPlayerMP) {
            if(args.length == 0)
                printInfoOfPlayer(sender, (EntityPlayerMP)sender);
            else if(args.length == 1) {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
                if(target == null)
                    throw new CommandException(this.getUsage(sender));
                printInfoOfPlayer(sender, target);
            }
            throw new CommandException(this.getUsage(sender));
        }
        else {
            if(args.length != 1)
                throw new CommandException(this.getUsage(sender));
            else {
                EntityPlayerMP target = server.getPlayerList().getPlayerByUsername(args[0]);
                if(target == null)
                    throw new CommandException(this.getUsage(sender));

                printInfoOfPlayer(sender, target);
            }
        }
    }

    private void printInfoOfPlayer(ICommandSender sender, EntityPlayerMP target) {
        PlayerJobs infos = PlayerData.getPlayerJobs(target);

        if(sender.getCommandSenderEntity() != null && sender.getCommandSenderEntity().equals(target))
            sender.sendMessage(new TextComponentString(TextFormatting.BLUE + "Your Stats"));
        else
            sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "Stats of " +
                            TextFormatting.BLUE + target.getName()));

        for(String job : infos.getJobs()) {
            int lvl = infos.getLevelByJob(job);
            long xp = infos.getXPByJob(job);
            sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + job + " : lvl " +
                            TextFormatting.BLUE + lvl + TextFormatting.LIGHT_PURPLE + ", xp " + TextFormatting.BLUE + xp));
        }
    }

    @Nonnull
    @Override
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender,
                                          String[] args, @Nullable BlockPos targetPos) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1)
        {
            for(EntityPlayerMP p : server.getPlayerList().getPlayers())
                completions.add(p.getName());
        }
        return completions;
    }
}
