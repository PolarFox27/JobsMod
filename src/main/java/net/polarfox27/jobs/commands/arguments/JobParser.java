package net.polarfox27.jobs.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.polarfox27.jobs.data.ClientJobsData;
import net.polarfox27.jobs.data.ServerJobsData;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class JobParser {
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_JOB = new DynamicCommandExceptionType(
            (object) -> new TranslatableComponent("argument.job.invalid", object));
    private static final BiFunction<SuggestionsBuilder, Set<String>, CompletableFuture<Suggestions>> SUGGEST_NOTHING =
            (builder, collection) -> builder.buildFuture();
    private final StringReader reader;
    private String job;
    private int start;
    private BiFunction<SuggestionsBuilder, Set<String>, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    /**
     * Creates a Job Parser from a String reader that contains the command typed
     * @param stringReader the reader representing the command
     */
    public JobParser(StringReader stringReader) {
        this.reader = stringReader;
    }

    public String getJob() {
        return job;
    }

    /**
     * Gets the set of all available jobs, the implementation is different if it is run on the client or the server
     * @return a set of all available jobs
     */
    public Set<String> getAllJobs(){
        Dist environment = DistExecutor.unsafeRunForDist(() -> () -> Dist.CLIENT, () -> () -> Dist.DEDICATED_SERVER);
        if (environment == Dist.CLIENT) {
            return ClientJobsData.JOBS_LEVELS.getJobs();
        } else if (environment == Dist.DEDICATED_SERVER) {
            return ServerJobsData.JOBS_LEVELS.getJobs();
        }
        else return new HashSet<>();
    }

    /**
     * Reads the job from the string reader
     * @throws CommandSyntaxException if the read job is not in the list of possible jobs.
     */
    public void readJob() throws CommandSyntaxException {
        this.start = this.reader.getCursor();

        while(reader.canRead() && Character.isLetter(reader.peek())) {
            reader.skip();
        }

        String reading = reader.getString().substring(this.start, reader.getCursor());
        this.job = null;
        for(String s : getAllJobs()){
            if (s.toLowerCase().startsWith(reading.toLowerCase())) {
                this.job = reading;
                break;
            }
        }
        if(this.job == null) {
            throw ERROR_UNKNOWN_JOB.createWithContext(this.reader, reading);
        }
    }

    /**
     * Parse the job from the string reader that represents the command typed.
     * @return this after having parsed the job argument from the reader
     * @throws CommandSyntaxException if there is a syntax error in the command
     */
    public JobParser parse() throws CommandSyntaxException {
        this.readJob();
        this.suggestions = this::suggestJob;

        return this;
    }

    /**
     * Adds the provided list of jobs to the suggestion list
     * @param builder the suggestion builder
     * @param jobs the jobs to add to the suggestions
     * @return the suggestions
     */
    private CompletableFuture<Suggestions> suggestJob(SuggestionsBuilder builder, Set<String> jobs) {
        return SharedSuggestionProvider.suggest(jobs, builder);
    }

    /**
     * Fills the suggestions for the job argument
     * @param builder the suggestion builder
     * @return the completable suggestions for the argument
     */
    public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
        Set<String> suggestedJobs = new HashSet<>();
        if(job != null){
            for(String j : getAllJobs()) {
                if (j.toLowerCase().startsWith(this.job.toLowerCase())) {
                    suggestedJobs.add(j);
                }
            }
        }
        return this.suggestions.apply(builder.createOffset(this.start), suggestedJobs);
    }
}
