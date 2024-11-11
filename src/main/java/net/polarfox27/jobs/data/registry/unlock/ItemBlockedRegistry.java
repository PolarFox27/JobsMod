package net.polarfox27.jobs.data.registry.unlock;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.ItemBlockedData;
import net.polarfox27.jobs.data.registry.unlock.BlockedData.Type;

public class ItemBlockedRegistry extends BlockedRegistry<ItemBlockedData, ItemStack> {

    /**
     * Creates an Item Blocked Registry
     * @param type the Registry type
     */
    public ItemBlockedRegistry(Type type) {
        super(type);
    }

    /**
     * Reads the Item Blocked Registry from a byte buffer
     * @param buf the buffer where to read
     */
    public ItemBlockedRegistry(FriendlyByteBuf buf){
        super(buf);
    }

    /**
     * Reads the item blocked data from the buffer.
     *
     * @param buf The buffer where to read
     * @return the read item blocked data
     */
    @Override
    public ItemBlockedData readData(FriendlyByteBuf buf) {
        return new ItemBlockedData(buf);
    }
}
