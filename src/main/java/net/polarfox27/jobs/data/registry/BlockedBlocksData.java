package net.polarfox27.jobs.data.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.polarfox27.jobs.data.capabilities.PlayerJobs;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockedBlocksData {

    private final Map<String, List<BlockedBlocksData.BlockedBlock>> BLOCKS_BY_JOB = new HashMap<>();

    /**
     * Empty Constructor
     */
    public BlockedBlocksData(){
    }

    /**
     * Reads the blocked block data from a byte buffer
     * @param buf the buffer where to read
     */
    public BlockedBlocksData(PacketBuffer buf){
        int size = buf.readInt();
        for(int i = 0; i < size; i++){
            String job = JobsUtil.readString(buf);
            int amount = buf.readInt();
            List<BlockedBlocksData.BlockedBlock> blocks = new ArrayList<>();
            for(int j = 0; j < amount; j++)
                blocks.add(new BlockedBlocksData.BlockedBlock(buf));
            BLOCKS_BY_JOB.put(job, blocks);
        }
    }

    /**
     * Adds a blocked block for a job
     * @param job the job which will unlock the block
     * @param block the Blocked Block data
     */
    public void addBlockedBlockForJob(String job, BlockedBlocksData.BlockedBlock block){
        if(!BLOCKS_BY_JOB.containsKey(job))
            BLOCKS_BY_JOB.put(job, new ArrayList<>());
        BLOCKS_BY_JOB.get(job).add(block);
    }

    /**
     * Gets a list of all blocked blocks for a job
     * @param job the job to check
     * @return the list of blocked blocks for that job
     */
    public List<BlockedBlock> getBlockedBlocks(String job){
        if(!BLOCKS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return new ArrayList<>(BLOCKS_BY_JOB.get(job));
    }

    /**
     * Checks if a player can break a block based on its jobs levels
     * @param player the player jobs data
     * @param block the block to check
     * @return true if the player can break the block, false otherwise
     */
    public boolean canBreakBlock(PlayerJobs player, Block block){
        for(Map.Entry<String, List<BlockedBlock>> e : BLOCKS_BY_JOB.entrySet()) {
            boolean canBreak = true;
            BlockedBlock b = e.getValue().stream()
                    .filter(x -> x.block == block)
                    .findFirst()
                    .orElse(null);
            if(b == null)
                continue;
            return b.getLevel() <= player.getLevelByJob(e.getKey());
        }
        return true;
    }

    /**
     * Writes the blocked block data to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(BLOCKS_BY_JOB.size());
        for(Map.Entry<String, List<BlockedBlocksData.BlockedBlock>> e : BLOCKS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(BlockedBlocksData.BlockedBlock c : e.getValue())
                c.writeToBytes(buf);
        }
    }

    /**
     * Clears all the block blocks data
     */
    public void clear(){
        this.BLOCKS_BY_JOB.clear();
    }
    
    public static class BlockedBlock{
        private final int level;
        private final Block block;

        /**
         * Creates a blocked block for a block and a level
         * @param level the level at which the block is unlocked
         * @param block the block to block (nice repetition)
         */
        public BlockedBlock(int level, Block block) {
            this.level = level;
            this.block = block;
        }

        /**
         * Reads the blocked block from a byte buffer
         * @param buf the buffer where to read
         */
        public BlockedBlock(PacketBuffer buf) {
            this.level = buf.readInt();
            this.block = Block.stateById(buf.readInt()).getBlock();
        }

        public int getLevel() {
            return level;
        }

        public Block getBlock() {
            return block;
        }

        /**
         * Writes the blocked block to a byte buffer
         * @param buf the buffer where to write
         */
        public void writeToBytes(PacketBuffer buf){
            buf.writeInt(level);
            buf.writeInt(Block.getId(this.block.defaultBlockState()));
        }

        /**
         * @return the UnlockStack representing the blocked block, to be displayed in the GUI
         */
        public UnlockStack getUnlockStack(){
            return new UnlockStack(level, new ItemStack(block, 1), UnlockStack.Type.BREAKING);
        }
}
}
