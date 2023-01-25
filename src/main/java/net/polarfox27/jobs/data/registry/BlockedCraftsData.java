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

    /**
     * Empty Constructor
     */
    public BlockedCraftsData(){
    }

    /**
     * Reads the blocked crafts data from a byte buffer
     * @param buf the buffer where to read
     */
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

    /**
     * Adds a blocked craft for a specific job
     * @param job the job which will unlock the craft
     * @param craft the craft to block
     */
    public void addBlockedCraftForJob(String job, BlockedCraft craft){
        if(!CRAFTS_BY_JOB.containsKey(job))
            CRAFTS_BY_JOB.put(job, new ArrayList<>());
        CRAFTS_BY_JOB.get(job).add(craft);
    }

    /**
     * @param job the job to check
     * @return the list of blocked crafts for that job
     */
    public List<BlockedCraft> getBlockedCrafts(String job){
        if(!CRAFTS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return new ArrayList<>(CRAFTS_BY_JOB.get(job));
    }

    /**
     * Checks if a player can craft an item stack based on its jobs levels
     * @param stack the stack to check
     * @param player the player jobs data
     * @return true if the player can craft the stack, otherwise false
     */
    public boolean canCraft(ItemStack stack, PlayerJobs player){
        for(Map.Entry<String, List<BlockedCraft>> e : CRAFTS_BY_JOB.entrySet()){
            BlockedCraft craft = e.getValue().stream().filter(x -> x.getCraft() == stack.getItem()).findFirst().orElse(null);
            if(craft == null)
                continue;
            return craft.getLevel() <= player.getLevelByJob(e.getKey());
        }
        return true;
    }

    /**
     * Writes the blocked crafts data to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(CRAFTS_BY_JOB.size());
        for(Map.Entry<String, List<BlockedCraft>> e : CRAFTS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(BlockedCraft c : e.getValue())
                c.writeToBytes(buf);
        }
    }

    /**
     * Clears all the blocked crafts data
     */
    public void clear(){
        this.CRAFTS_BY_JOB.clear();
    }

    public static class BlockedCraft{
        private final int level;
        private final Item craft;
        private final int metadata;

        /**
         * Creates a blocked craft based on the item, the metadata and the level at which it will be unlocked
         * @param level the level at which the craft is unlocked
         * @param craft the blocked item
         * @param metadata the metadata of the blocked item
         */
        public BlockedCraft(int level, Item craft, int metadata) {
            this.level = level;
            this.craft = craft;
            this.metadata = metadata;
        }

        /**
         * Reads the blocked craft from a byte buffer
         * @param buf the buffer where to read
         */
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

        /**
         * Writes the blocked craft to a byte buffer
         * @param buf the buffer where to write
         */
        public void writeToBytes(PacketBuffer buf){
            buf.writeInt(level);
            buf.writeInt(Item.getId(craft));
            buf.writeInt(metadata);
        }

        /**
         * @return the UnlockStack representing the blocked craft, to be displayed in the GUI
         */
        public UnlockStack getUnlockStack(){
            return new UnlockStack(level, JobsUtil.itemStack(craft, 1, metadata), UnlockStack.Type.CRAFTING);
        }
    }


}
