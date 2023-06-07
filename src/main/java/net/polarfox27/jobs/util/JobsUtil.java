package net.polarfox27.jobs.util;

import com.google.common.primitives.Longs;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JobsUtil {

    /**
     * Clamps the value between the min and max and returns it.
     * @param value the value to clamp
     * @param min the minimum allowed value (included)
     * @param max the minimum allowed value (included)
     * @return the clamped value
     */
    public static long clamp(long value, long min, long max) {
        if(min == max) return min;
        if(value < min) return min;
        return Math.min(value, max);
    }

    /**
     * Clamps the value between the min and max and returns it.
     * @param value the value to clamp
     * @param min the minimum allowed value (included)
     * @param max the minimum allowed value (included)
     * @return the clamped value
     */
    public static int clamp(int value, int min, int max) {
        if(min == max) return min;
        if(value < min) return min;
        return Math.min(value, max);
    }

    /**
     * Concatenates the byte arrays into a single array
     * @param arrays the arrays to concatenate
     * @return the concatenated array
     */
    public static byte[] concat(byte[]... arrays){
        byte[] result = new byte[Arrays.stream(arrays).mapToInt((array) -> array.length).sum()];
        int i = 0;
        int index = 0;
        while(i < arrays.length){
            for(int j = 0; j < arrays[i].length; j++) {
                result[index] = arrays[i][j];
                index++;
            }
            i++;
        }
        return result;
    }

    /**
     * Concatenates the long arrays into a single array
     * @param arrays the arrays to concatenate
     * @return the concatenated array
     */
    public static long[] concat(long[]... arrays){
        long[] result = new long[Arrays.stream(arrays).mapToInt((array) -> array.length).sum()];
        int i = 0;
        int index = 0;
        while(i < arrays.length){
            for(int j = 0; j < arrays[i].length; j++) {
                result[index] = arrays[i][j];
                index++;
            }
            i++;
        }
        return result;
    }

    /**
     * Creates a sub array of the given array
     * @param array the array
     * @param index the starting index
     * @param length the length of the sub-array
     * @return the sub-array
     */
    public static byte[] subArray(byte[] array, int index, int length){
        byte[] result = new byte[length];
        System.arraycopy(array, index, result, 0, length);
        return result;
    }

    /**
     * Creates a sub array of the given array
     * @param array the array
     * @param index the starting index
     * @param length the length of the sub-array
     * @return the sub-array
     */
    public static long[] subArray(long[] array, int index, int length){
        long[] result = new long[length];
        System.arraycopy(array, index, result, 0, length);
        return result;
    }

    /**
     * converts a long array to an equivalent byte array
     * @param values the array to convert
     * @return the converted array
     */
    public static byte[] toBytes(long[] values){
        byte[] result = new byte[0];
        for (long value : values)
            result = concat(result, Longs.toByteArray(value));
        return result;
    }

    /**
     * converts a byte array back to an equivalent long array
     * @param bytes the array to convert back
     * @return the converted array
     */public static long[] fromBytes(byte[] bytes){
        byte[] array = bytes;
        long[] result = new long[0];
        while(array.length % 8 != 0)
            array = concat(array, new byte[]{0});
        for(int i = 0; i < bytes.length; i += 8)
            result = concat(result, new long[]{Longs.fromByteArray(subArray(array, i, 8))});
        return result;
    }

    /**
     * Writes a string to a buffer
     * @param text the text to write
     * @param buf the buffer where to write
     */
    public static void writeToBuf(String text, ByteBuf buf){
        buf.writeInt(text.length());
        buf.writeCharSequence(text, StandardCharsets.UTF_8);
    }

    /**
     * Reads a string from a buffer
     *
     * @param buf the buffer where the string must be read
     * @return the read string
     */
    public static String readFromBuf(ByteBuf buf){
        int size = buf.readInt();
        return buf.readCharSequence(size, StandardCharsets.UTF_8).toString();
    }

    /**
     * Creates an item stack representing the entity (used for Jobs interface)
     * @param entity the entity to represent
     * @return the item stack representing the entity
     */
    public static ItemStack createEntityItemStack(ResourceLocation entity){
        ItemStack s = new ItemStack(Item.getItemFromBlock(Blocks.BARRIER));
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("entity", entity.toString());
        s.setTagCompound(nbt);
        return s;
    }

    /**
     * Constructs an item stack.
     * @param item the item in the stack
     * @param count the amount of items in the stack
     * @param metadata the item's metadata (e.g. durability)
     * @return the item stack
     */
    public static ItemStack itemStack(Item item, int count, int metadata){
        ItemStack s = new ItemStack(item, count);
        if(metadata != 0)
            s.setItemDamage(metadata);
        return s;
    }

    /**
     * Writes a byte array to a byte buffer
     * @param array the array to write
     * @param buf the buffer where to write
     */
    public static void writeByteArray(byte[] array, ByteBuf buf){
        buf.writeInt(array.length);
        buf.writeBytes(array);
    }

    /**
     * Reads a byte array from a byte buffer
     * @param buf the buffer to read from
     * @return the read byte array
     */
    public static byte[] readByteArray(ByteBuf buf){
        int length = buf.readInt();
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

    /**
     * get the height of the entity collision box
     * @param entity the entity whose size we measure
     * @return the size of the entity
     */
    @SideOnly(Side.CLIENT)
    public static float getHeight(EntityLivingBase entity){
        if(entity == null)
            return 0.0f;
        return (float)(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY);
    }

    public static class Pair<S, T>{
        private final S first;
        private final T second;


        public Pair(S first, T second) {
            this.first = first;
            this.second = second;
        }

        public S getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }
    }
}

