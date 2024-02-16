package net.polarfox27.jobs.commands.arguments;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class JobArgumentInfo implements ArgumentTypeInfo<JobArgumentType, JobArgumentInfo.Template> {


    @Override
    public void serializeToNetwork(@NotNull Template template, @NotNull FriendlyByteBuf friendlyByteBuf) {
    }

    @Override
    public @NotNull Template deserializeFromNetwork(@NotNull FriendlyByteBuf friendlyByteBuf) {
        return new Template();
    }

    @Override
    public @NotNull Template unpack(@NotNull JobArgumentType jobArgumentType) {
        return new Template();
    }

    @Override
    public void serializeToJson(@NotNull Template template, @NotNull JsonObject jsonObject) {

    }

    public final class Template implements ArgumentTypeInfo.Template<JobArgumentType>{

        @Override
        public @NotNull JobArgumentType instantiate(@NotNull CommandBuildContext commandBuildContext) {
            return new JobArgumentType();
        }

        @Override
        public @NotNull ArgumentTypeInfo<JobArgumentType, ?> type() {
            return JobArgumentInfo.this;
        }
    }
}
