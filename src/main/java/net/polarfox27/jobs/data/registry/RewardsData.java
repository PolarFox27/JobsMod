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

    public RewardsData(){
    }

    public RewardsData(PacketBuffer buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            int amount = buf.readInt();
            List<Reward> rewards = new ArrayList<>();
            for(int j = 0; j < amount; j++)
                rewards.add(new Reward(buf));
            REWARDS_BY_JOB.put(job, rewards);
        }
    }

    public void addRewardForJob(String job, int level, List<ItemStack> stacks){
        this.addRewardForJob(job, new Reward(level, stacks));
    }

    public void addRewardForJob(String job, Reward reward){
        if(!REWARDS_BY_JOB.containsKey(job))
            REWARDS_BY_JOB.put(job, new ArrayList<>());
        REWARDS_BY_JOB.get(job).add(reward);
    }

    public List<ItemStack> getRewards(String job, int level){
        if(!REWARDS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return REWARDS_BY_JOB.get(job).stream()
                .filter(x -> x.level == level)
                .flatMap(x -> x.getRewards().stream())
                .collect(Collectors.toList());
    }

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(REWARDS_BY_JOB.size());
        for(Map.Entry<String, List<Reward>> e : REWARDS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(Reward r : e.getValue())
                r.writeToBytes(buf);
        }
    }

    public void clear(){
        this.REWARDS_BY_JOB.clear();
    }

    public static class Reward{
        private final int level;
        private final List<ItemStack> rewards;

        public Reward(int level, List<ItemStack> rewards) {
            this.level = level;
            this.rewards = rewards;
        }

        public Reward(PacketBuffer buf){
            this.level = buf.readInt();
            this.rewards = new ArrayList<>();
            int size = buf.readInt();
            for(int i = 0; i < size; i++){
                Item item = Item.byId(buf.readInt());
                int count = buf.readInt();
                this.rewards.add(JobsUtil.itemStack(item, count, 0));
            }
        }

        public int getLevel() {
            return level;
        }

        public List<ItemStack> getRewards() {
            return rewards;
        }

        public void writeToBytes(PacketBuffer buf){
            buf.writeInt(level);
            buf.writeInt(rewards.size());
            for(ItemStack s : rewards){
                buf.writeInt(Item.getId(s.getItem()));
                buf.writeInt(s.getCount());
            }
        }
    }


}
