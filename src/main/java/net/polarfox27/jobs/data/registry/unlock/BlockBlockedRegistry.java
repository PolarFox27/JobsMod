package net.polarfox27.jobs.data.registry.unlock;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.BlockBlockedData;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.Type;

public class BlockBlockedRegistry extends BlockedRegistry<BlockBlockedData, BlockState> {

    /**
     * Creates a Block Blocked Registry
     * @param type the Registry type
     */
    public BlockBlockedRegistry(Type type) {
        super(type);
    }

    /**
     * Reads the Block Blocked Registry from a byte buffer
     * @param buf the buffer where to read
     */
    public BlockBlockedRegistry(FriendlyByteBuf buf){
        super(buf);
    }

    /**
     * Reads the item blocked data from the buffer.
     *
     * @param buf The buffer where to read
     * @return the read item blocked data
     */
    @Override
    public BlockedData.BlockBlockedData readData(FriendlyByteBuf buf) {
        return new BlockedData.BlockBlockedData(buf);
    }
}
