package net.polarfox27.jobs.data.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockedCraftsData {

    private final Map<String, List<BlockedCraft>> CRAFTS_BY_JOB = new HashMap<>();

    public BlockedCraftsData(){
    }

    public BlockedCraftsData(PacketBuffer buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            int amount = buf.readInt();
            List<BlockedCraft> crafts = new ArrayList<>();
            for(int j = 0; j < amount; j++)
                crafts.add(new BlockedCraft(buf));
            CRAFTS_BY_JOB.put(job, crafts);
        }
    }

    public void addBlockedCraftForJob(String job, int level, Item craft, int metadata){
        this.addBlockedCraftForJob(job, new BlockedCraft(level, craft, metadata));
    }

    public void addBlockedCraftForJob(String job, BlockedCraft craft){
        if(!CRAFTS_BY_JOB.containsKey(job))
            CRAFTS_BY_JOB.put(job, new ArrayList<>());
        CRAFTS_BY_JOB.get(job).add(craft);
    }

    public List<ItemStack> getUnlockedCrafts(String job, int level){
        if(!CRAFTS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return CRAFTS_BY_JOB.get(job).stream()
                .filter(x -> x.getLevel() == level)
                .map(x -> JobsUtil.itemStack(x.getCraft(), 1, x.getMetadata()))
                .collect(Collectors.toList());
    }

    public List<BlockedCraft> getBlockedCrafts(String job){
        if(!CRAFTS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return new ArrayList<>(CRAFTS_BY_JOB.get(job));
    }

    public boolean canCraft(ItemStack stack, PlayerJobs player){
        for(Map.Entry<String, List<BlockedCraft>> e : CRAFTS_BY_JOB.entrySet()){
            BlockedCraft craft = e.getValue().stream().filter(x -> x.getCraft() == stack.getItem()).findFirst().orElse(null);
            if(craft == null)
                continue;
            return craft.getLevel() <= player.getLevelByJob(e.getKey());
        }
        return true;
    }

    public int getUnlockLevel(String job, ItemStack stack){
        for(BlockedCraft blockedCraft : this.CRAFTS_BY_JOB.get(job))
            if(blockedCraft.craft == stack.getItem() && blockedCraft.metadata == stack.getDamageValue())
                return blockedCraft.level;
        return -1;
    }

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(CRAFTS_BY_JOB.size());
        for(Map.Entry<String, List<BlockedCraft>> e : CRAFTS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(BlockedCraft c : e.getValue())
                c.writeToBytes(buf);
        }
    }

    public void clear(){
        this.CRAFTS_BY_JOB.clear();
    }

    public static class BlockedCraft{
        private final int level;
        private final Item craft;
        private final int metadata;

    public BlockedCraft(int level, Item craft, int metadata) {
            this.level = level;
            this.craft = craft;
            this.metadata = metadata;
        }

    public BlockedCraft(PacketBuffer buf) {
            this.level = buf.readInt();
            this.craft = Item.byId(buf.readInt());
            this.metadata = buf.readInt();
        }

        public int getLevel() {
            return level;
        }

        public Item getCraft() {
            return craft;
        }

        public int getMetadata() {
            return metadata;
        }

        public boolean isCraftBlocked(int level, ItemStack stack){
            return level < this.level &&
                    this.craft == stack.getItem() &&
                    (this.metadata == stack.getDamageValue() || this.metadata < 0);
        }

        public void writeToBytes(PacketBuffer buf){
            buf.writeInt(level);
            buf.writeInt(Item.getId(craft));
            buf.writeInt(metadata);
        }

        public UnlockStack getUnlockStack(){
            return new UnlockStack(level, JobsUtil.itemStack(craft, 1, metadata), UnlockStack.Type.CRAFTING);
        }
    }


}
