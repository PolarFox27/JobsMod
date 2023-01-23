package net.polarfox27.jobs.data.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.polarfox27.jobs.util.JobsUtil;

import java.util.Arrays;

public abstract class  XPData {

    private final long[] xp_values;

    public XPData(long[] xp_values) {
        this.xp_values = xp_values;
    }

    public XPData(PacketBuffer buf) {
        this.xp_values = JobsUtil.fromBytes(JobsUtil.readByteArray(buf));
    }

    public long[] getXP_values() {
        return xp_values;
    }

    public long getXPByLevel(int level){
        if(level < 0 || level >= this.xp_values.length)
            return 0;
        return this.xp_values[level];
    }

    public int unlockingLevel(int level){
        for(int i = level+1; i < this.xp_values.length; i++)
            if(this.getXPByLevel(i) > 0)
                return i;
        return -1;
    }

    public void writeToBytes(PacketBuffer buf){
        JobsUtil.writeByteArray(JobsUtil.toBytes(xp_values), buf);
    }

    public abstract ItemStack createStack();

    @Override
    public String toString() {
        return Arrays.toString(this.xp_values);
    }

    public static class ItemXPData extends XPData{
        private final Item item;
        private final int metadata;

        public ItemXPData(long[] xp_values, Item item, int metadata) {
            super(xp_values);
            this.item = item;
            this.metadata = metadata;
        }

        public ItemXPData(PacketBuffer buf){
            super(buf);
            this.item = Item.byId(buf.readInt());
            this.metadata = buf.readInt();
        }

        public Item getItem() {
            return item;
        }

        public int getMetadata() {
            return metadata;
        }

        public boolean matches(ItemStack stack){
            if(item != stack.getItem())
                return false;
            return metadata < 0 || metadata == stack.getDamageValue();
        }

        @Override
        public void writeToBytes(PacketBuffer buf) {
            super.writeToBytes(buf);
            buf.writeInt(Item.getId(item));
            buf.writeInt(metadata);
        }

        @Override
        public ItemStack createStack() {
            return JobsUtil.itemStack(item, 1, metadata);
        }

        @Override
        public String toString() {
            String s = ForgeRegistries.ITEMS.getKey(item).toString();
            if(metadata >= 0)
                s += " (" + metadata + ")";
            return  s + " : " + super.toString();
        }
    }


    public static class BlockXPData extends XPData{
        private final Block block;
        private final boolean isCrop;

        public BlockXPData(long[] xp_values, Block block) {
            super(xp_values);
            this.block = block;
            this.isCrop = false;
        }

        public BlockXPData(long[] xp_values, Block block, boolean isCrop) {
            super(xp_values);
            this.block = block;
            this.isCrop = isCrop;
        }

        public BlockXPData(PacketBuffer buf){
            super(buf);
            this.block = Block.stateById(buf.readInt()).getBlock();
            this.isCrop = buf.readBoolean();
        }

        public Block getBlock() {
            return block;
        }

        public boolean matches(BlockState state){
            if(this.block != state.getBlock())
                return false;
            if(!(this.block instanceof CropsBlock) || !isCrop)
                return true;
            else{
                CropsBlock crop = (CropsBlock) this.block;
                return crop.isMaxAge(state);
            }
        }

        @Override
        public void writeToBytes(PacketBuffer buf) {
            super.writeToBytes(buf);
            buf.writeInt(Block.getId(block.defaultBlockState()));
            buf.writeBoolean(isCrop);
        }

        @Override
        public ItemStack createStack() {
            return JobsUtil.itemStack(Item.BY_BLOCK.getOrDefault(block, Items.AIR), 1, -1);
        }

        @Override
        public String toString() {
            String s = ForgeRegistries.BLOCKS.getKey(block).toString();
            return  s + " : " + super.toString();
        }
    }


    public static class EntityXPData extends XPData{
        private final EntityType<? extends Entity> entity;

        public EntityXPData(long[] xp_values, EntityType<? extends Entity> type) {
            super(xp_values);
            this.entity = type;
        }

        public EntityXPData(PacketBuffer buf){
            super(buf);
            entity = EntityType.byString(JobsUtil.readString(buf)).orElse(null);
        }

        public EntityType<? extends Entity> getEntity() {
            return entity;
        }

        @OnlyIn(Dist.CLIENT)
        public String getEntityName(){
            return I18n.get("entity.minecraft." + EntityType.getKey(entity).getPath());
        }

        public boolean matches(EntityType<? extends Entity> entity){
            return this.entity.equals(entity);
        }

        @Override
        public void writeToBytes(PacketBuffer buf) {
            super.writeToBytes(buf);
            String name = EntityType.getKey(entity).toString();
            JobsUtil.writeString(name, buf);
        }

        @Override
        public ItemStack createStack() {
            return JobsUtil.createEntityItemStack(entity);
        }

        @Override
        public String toString() {
            String s = EntityType.getKey(entity).toString();
            return  s + " : " + super.toString();
        }
    }
}
