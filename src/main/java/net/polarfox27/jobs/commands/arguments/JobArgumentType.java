package net.polarfox27.jobs.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class JobArgumentType implements ArgumentType<String> {

    private static final Collection<String> EXAMPLES = Arrays.asList("miner", "farmer", "hunter");

    public static JobArgumentType job() {
        return new JobArgumentType();
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        JobParser parser = new JobParser(reader);
        return parser.parse().getJob();
    }

    public static String getJob(CommandContext<?> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(context.getInput());
        stringreader.setCursor(builder.getStart());
        JobParser parser = new JobParser(stringreader);

        try {
            parser.parse();
        } catch (CommandSyntaxException ignored) {
        }

        return parser.fillSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
