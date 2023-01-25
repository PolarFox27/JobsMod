package net.polarfox27.jobs.data.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RewardsData {

    private final Map<String, List<Reward>> REWARDS_BY_JOB = new HashMap<>();

    /**
     * Empty Constructor
     */
    public RewardsData(){
    }

    /**
     * Adds a reward for a specific job
     * @param job the job for which the reward is added
     * @param reward the reward to add
     */
    public void addRewardForJob(String job, Reward reward){
        if(!REWARDS_BY_JOB.containsKey(job))
            REWARDS_BY_JOB.put(job, new ArrayList<>());
        REWARDS_BY_JOB.get(job).add(reward);
    }

    /**
     * Gets the list of rewards the player receives when he reaches a new level for a job
     * @param job the job for which the player should receive rewards
     * @param level the level reached in the job
     * @return the list of rewards associated with that job and level
     */
    public List<ItemStack> getRewards(String job, int level){
        if(!REWARDS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return REWARDS_BY_JOB.get(job).stream()
                .filter(x -> x.level == level)
                .flatMap(x -> x.getRewards().stream())
                .collect(Collectors.toList());
    }

    /**
     * Deletes all the rewards data
     */
    public void clear(){
        this.REWARDS_BY_JOB.clear();
    }

    public static class Reward{
        private final int level;
        private final List<ItemStack> rewards;

        /**
         * Creates a Reward list for a specific level
         * @param level
         * @param rewards
         */
        public Reward(int level, List<ItemStack> rewards) {
            this.level = level;
            this.rewards = rewards;
        }

        public int getLevel() {
            return level;
        }

        public List<ItemStack> getRewards() {
            return rewards;
        }

    }
}
