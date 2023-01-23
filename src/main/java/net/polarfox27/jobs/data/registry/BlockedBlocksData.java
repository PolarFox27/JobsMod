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

    public BlockedBlocksData(){
    }

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

    public void addBlockedBlockForJob(String job, int level, Block block, int metadata){
        this.addBlockedBlockForJob(job, new BlockedBlocksData.BlockedBlock(level, block));
    }

    public void addBlockedBlockForJob(String job, BlockedBlocksData.BlockedBlock block){
        if(!BLOCKS_BY_JOB.containsKey(job))
            BLOCKS_BY_JOB.put(job, new ArrayList<>());
        BLOCKS_BY_JOB.get(job).add(block);
    }

    public List<ItemStack> getUnlockedBlocks(String job, int level){
        if(!BLOCKS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return BLOCKS_BY_JOB.get(job).stream()
                .filter(x -> x.getLevel() == level)
                .map(x -> new ItemStack(x.getBlock(), 1))
                .collect(Collectors.toList());
    }

    public List<BlockedBlock> getBlockedBlocks(String job){
        if(!BLOCKS_BY_JOB.containsKey(job))
            return new ArrayList<>();
        return new ArrayList<>(BLOCKS_BY_JOB.get(job));
    }

    public int getUnlockLevel(String job, Block block){
        for(BlockedBlocksData.BlockedBlock blockedBlock : this.BLOCKS_BY_JOB.get(job))
            if(blockedBlock.block == block)
                return blockedBlock.level;
        return -1;
    }

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

    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(BLOCKS_BY_JOB.size());
        for(Map.Entry<String, List<BlockedBlocksData.BlockedBlock>> e : BLOCKS_BY_JOB.entrySet()){
            JobsUtil.writeString(e.getKey(), buf);
            buf.writeInt(e.getValue().size());
            for(BlockedBlocksData.BlockedBlock c : e.getValue())
                c.writeToBytes(buf);
        }
    }

    public void clear(){
        this.BLOCKS_BY_JOB.clear();
    }
    
    public static class BlockedBlock{
        private final int level;
        private final Block block;

        public BlockedBlock(int level, Block block) {
            this.level = level;
            this.block = block;
        }
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


        public boolean isStateBlocked(int level, BlockState state){
            return level < this.level &&
                    this.block == state.getBlock();
        }

        public void writeToBytes(PacketBuffer buf){
            buf.writeInt(level);
            buf.writeInt(Block.getId(this.block.defaultBlockState()));
        }

        public UnlockStack getUnlockStack(){
            return new UnlockStack(level, new ItemStack(block, 1), UnlockStack.Type.BREAKING);
        }
}
}
