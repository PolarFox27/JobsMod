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

    /**
     * Returns a new instance of the class at each execution
     * @return an instance of the class
     */
    public static JobArgumentType job() {
        return new JobArgumentType();
    }
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        JobParser parser = new JobParser(reader);
        return parser.parse().getJob();
    }

    /**
     * Gets the job value from the command
     * @param context the command context
     * @param name the name of the argument of type job
     * @return the job value that the argument represents
     */
    public static String getJob(CommandContext<?> context, String name) {
        return context.getArgument(name, String.class);
    }

    /**
     * Gets the suggestions for the job argument based on the command context
     * @param context the command context
     * @param builder the suggestion builder
     * @return the list of suggestions
     * @param <S> the type of command
     */
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
