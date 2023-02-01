package net.polarfox27.jobs.data.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public abstract class BlockedData {

    private final int level;
    private final Type type;

    /**
     * Creates a new Blocked Data unlocked at a specific level
     * @param level the level at which it will be unlocked
     * @param type the Unlock Type
     */
    protected BlockedData(int level, Type type) {
        this.level = level;
        this.type = type;
    }

    /**
     * Reads a Blocked Data from a byte buffer
     * @param buf the buffer where to read
     */
    protected BlockedData(PacketBuffer buf){
        this.level = buf.readInt();
        this.type = Type.byCode(buf.readInt());
    }

    public int getLevel() {
        return level;
    }

    public Type getType() {
        return type;
    }

    /**
     * Writes the Blocked Data to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(PacketBuffer buf){
        buf.writeInt(level);
        buf.writeInt(type.code);
    }

    /**
     * @return an Unlock Stack representing this Blocked Data
     */
    public abstract UnlockStack createUnlockStack();

    public enum Type{
        CRAFTING(0),
        BREAKING(1),
        EQUIP(2),
        LEFT_CLICK(3),
        RIGHT_CLICK(4);

        public final int code;
        Type(int code) {
            this.code = code;
        }

        /**
         * Returns the Type associated to the code value
         * @param code the code
         * @return the type associated to the code, or null if the code is invalid
         */
        public static Type byCode(int code){
            for(Type t : Type.values())
                if(t.code == code)
                    return t;
            return null;
        }
    }

    public static class BlockBlockedData extends BlockedData{

        private final Block block;

        /**
         * Creates a new Blocked Data unlocked at a specific level
         * @param level the level at which it will be unlocked
         * @param type the Unlock Type
         * @param block the block that is blocked (I love alliterations)
         */
        public BlockBlockedData(int level, Type type, Block block) {
            super(level, type);
            this.block = block;
        }

        /**
         * Reads a Blocked Data from a byte buffer
         * @param buf the buffer where to read
         */
        public BlockBlockedData(PacketBuffer buf) {
            super(buf);
            this.block = Block.stateById(buf.readInt()).getBlock();
        }

        /**
         * @return an Unlock Stack representing this Blocked Data
         */
        @Override
        public UnlockStack createUnlockStack() {
            return new UnlockStack(this.getLevel(), new ItemStack(block, 1), this.getType());
        }

        /**
         * Writes the Blocked Data to a byte buffer
         * @param buf the buffer where to write
         */
        @Override
        public void writeToBytes(PacketBuffer buf) {
            super.writeToBytes(buf);
            buf.writeInt(Block.getId(this.block.defaultBlockState()));
        }
    }

    public static class ItemBlockedData extends BlockedData{

        private final Item item;

        /**
         * Creates a new Blocked Data unlocked at a specific level
         * @param level the level at which it will be unlocked
         * @param type the Unlock Type
         * @param item the item that is blocked
         */
        public ItemBlockedData(int level, Type type, Item item) {
            super(level, type);
            this.item = item;
        }

        /**
         * Reads a Blocked Data from a byte buffer
         * @param buf the buffer where to read
         */
        public ItemBlockedData(PacketBuffer buf) {
            super(buf);
            this.item = Item.byId(buf.readInt());
        }

        /**
         * @return an Unlock Stack representing this Blocked Data
         */
        @Override
        public UnlockStack createUnlockStack() {
            return new UnlockStack(this.getLevel(), new ItemStack(item, 1), this.getType());
        }

        /**
         * Writes the Blocked Data to a byte buffer
         * @param buf the buffer where to write
         */
        @Override
        public void writeToBytes(PacketBuffer buf) {
            super.writeToBytes(buf);
            buf.writeInt(Item.getId(item));
        }
    }
}
