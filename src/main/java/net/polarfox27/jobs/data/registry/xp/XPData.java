package net.polarfox27.jobs.data.registry.xp;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.polarfox27.jobs.util.JobsUtil;
import net.polarfox27.jobs.util.config.JsonUtil;

import java.util.Arrays;
import java.util.Random;

public abstract class  XPData {

    private final long[] xp_values;

    /**
     * Creates the XPData based on the xp values stored as an array
     * @param xp_values the xp values
     */
    public XPData(long[] xp_values) {
        this.xp_values = xp_values;
    }

    /**
     * Reads an XPData from a byte buffer
     * @param buf the buffer where to read
     */
    public XPData(ByteBuf buf) {
        this.xp_values = JobsUtil.fromBytes(JobsUtil.readByteArray(buf));
    }

    /**
     * @return the xp values
     */
    public long[] getXP_values() {
        return xp_values;
    }

    /**
     * Gets the amount of xp a player would get based on its level
     * @param level the level of the player
     * @return the amount of xp the player would receive
     */
    public long getXPByLevel(int level){
        if(level < 0 || level >= this.xp_values.length)
            return 0;
        return this.xp_values[level];
    }

    /**
     * Returns the first level higher than the given level where the player receives more than 0xp
     * @param level the level the player is currently at
     * @return Returns the level where the player receives xp, or -1 if none was found
     */
    public int unlockingLevel(int level){
        for(int i = level+1; i < this.xp_values.length; i++)
            if(this.getXPByLevel(i) > 0)
                return i;
        return -1;
    }

    /**
     * Writes the XPData to a byte buffer
     * @param buf the buffer where to write
     */
    public void writeToBytes(ByteBuf buf){
        JobsUtil.writeByteArray(JobsUtil.toBytes(xp_values), buf);
    }

    /**
     * Creates a stack representing the XPData
     * @return an Item Stack representing the XPData
     */
    public abstract ItemStack createStack();

    /**
     * @return a string representation of the XPData
     */
    @Override
    public String toString() {
        return Arrays.toString(this.xp_values);
    }

    public static class ItemXPData extends XPData{
        private final Item item;
        private final int metadata;

        /**
         * Constructs an XPData for an Item
         * @param xp_values the xp that can be earned at each level
         * @param item the item that will give the xp
         * @param metadata the item's metadata (e.g. durability)
         */
        public ItemXPData(long[] xp_values, Item item, int metadata) {
            super(xp_values);
            this.item = item;
            this.metadata = metadata;
        }

        /**
         * Reads an Item XPData from a byte buffer
         * @param buf the buffer where to read
         */
        public ItemXPData(ByteBuf buf){
            super(buf);
            this.item = Item.getItemById(buf.readInt());
            this.metadata = buf.readInt();
        }

        public Item getItem() {
            return item;
        }

        public int getMetadata() {
            return metadata;
        }

        /**
         * Checks if a stack is representing the item
         * @param stack the stack to check
         * @return true if the stack represent the Item XPData
         */
        public boolean matches(ItemStack stack){
            if(item != stack.getItem())
                return false;
            return metadata < 0 || metadata == stack.getMetadata();
        }

        /**
         * Writes the Item XPData to a byte buffer
         * @param buf the buffer where to write
         */
        @Override
        public void writeToBytes(ByteBuf buf) {
            super.writeToBytes(buf);
            buf.writeInt(Item.getIdFromItem(item));
            buf.writeInt(metadata);
        }

        /**
         * @return a stack representing the Item XP Data
         */
        @Override
        public ItemStack createStack() {
            return JobsUtil.itemStack(item, 1, metadata);
        }

        /**
         * @return a string representation of the Item XPData
         */
        @Override
        public String toString() {
            String s = JsonUtil.getRegistryName(item);
            if(metadata >= 0)
                s += " (" + metadata + ")";
            return  s + " : " + super.toString();
        }
    }


    public static class BlockXPData extends XPData{
        private final Block block;
        private final boolean isCrop;

        /**
         * Constructs an XPData for a Block
         * @param xp_values the xp that can be earned at each level
         * @param block the block that will give the xp
         */
        public BlockXPData(long[] xp_values, Block block) {
            super(xp_values);
            this.block = block;
            this.isCrop = false;
        }

        /**
         * Constructs an XPData for a Block giving the option to register a Crops Block
         * @param xp_values the xp that can be earned at each level
         * @param block the block that will give the xp
         * @param isCrop whether the block is a crop or not
         */
        public BlockXPData(long[] xp_values, Block block, boolean isCrop) {
            super(xp_values);
            this.block = block;
            this.isCrop = isCrop;
        }

        /**
         * Reads a Block XPData from a byte buffer
         * @param buf the buffer where to read
         */
        public BlockXPData(ByteBuf buf){
            super(buf);
            this.block = Block.getBlockById(buf.readInt());
            this.isCrop = buf.readBoolean();
        }

        public Block getBlock() {
            return block;
        }

        /**
         * Checks if a block state is representing the block
         * @param state the block state to check
         * @return true if the state represent the Block XPData
         */
        public boolean matches(IBlockState state){
            if(this.block != state.getBlock())
                return false;
            if(!(this.block instanceof BlockCrops) || !isCrop)
                return true;
            else{
                BlockCrops crop = (BlockCrops) this.block;
                return crop.isMaxAge(state);
            }
        }

        /**
         * Writes the Block XPData to a byte buffer
         * @param buf the buffer where to write
         */
        @Override
        public void writeToBytes(ByteBuf buf) {
            super.writeToBytes(buf);
            buf.writeInt(Block.getIdFromBlock(block));
            buf.writeBoolean(isCrop);
        }

        /**
         * @return a stack representing the Block XP Data
         */
        @Override
        public ItemStack createStack() {
            Item item = block.getItemDropped(block.getDefaultState(), new Random(), 0);
            return JobsUtil.itemStack(item, 1, -1);
        }

        /**
         * @return a string representation of the Block XPData
         */
        @Override
        public String toString() {
            String s = JsonUtil.getRegistryName(block);
            return  s + " : " + super.toString();
        }
    }


    public static class EntityXPData extends XPData{
        private final ResourceLocation entity;

        /**
         * Constructs an XPData for an Entity
         * @param xp_values the xp that can be earned at each level
         * @param entity the entity that will give the xp
         */
        public EntityXPData(long[] xp_values, ResourceLocation entity) {
            super(xp_values);
            this.entity = entity;
        }

        /**
         * Reads an Entity XPData from a byte buffer
         * @param buf the buffer where to read
         */
        public EntityXPData(ByteBuf buf){
            super(buf);
            entity = new ResourceLocation(JobsUtil.readFromBuf(buf));
        }

        public ResourceLocation getEntity() {
            return entity;
        }

        /**
         * @return the locale translated name of the Entity
         */
        @SideOnly(Side.CLIENT)
        public String getEntityName(){
            String name = entity.equals(new ResourceLocation("minecraft:player")) ?
                    "entity.minecraft.player" :
                    EntityList.getTranslationName(entity);
            return I18n.format(name != null ? name : "None");
        }

        /**
         * Checks if an entity type is representing the entity
         * @param entityLiving the entity to check
         * @return true if the type represent the Entity XPData
         */
        public boolean matches(EntityLivingBase entityLiving){
            return EntityList.isMatchingName(entityLiving, entity);
        }

        /**
         * Writes Entity the XPData to a byte buffer
         * @param buf the buffer where to write
         */
        @Override
        public void writeToBytes(ByteBuf buf) {
            super.writeToBytes(buf);
            JobsUtil.writeToBuf(entity.toString(), buf);
        }

        /**
         * @return a stack representing the Entity XP Data
         */
        @Override
        public ItemStack createStack() {
            return JobsUtil.createEntityItemStack(entity);
        }

        /**
         * @return a string representation of the Entity XPData
         */
        @Override
        public String toString() {
            String s = entity.toString();
            return  s + " : " + super.toString();
        }
    }
}
