package net.polarfox27.jobs.data.registry;

import net.minecraft.item.ItemStack;

public class UnlockStack implements Comparable<UnlockStack>{

    private final int level;
    private final ItemStack stack;
    private final Type type;

    public UnlockStack(int level, ItemStack stack, Type type) {
        this.level = level;
        this.stack = stack;
        this.type = type;
    }

    @Override
    public int compareTo(UnlockStack o) {
        return Integer.compare(this.level, o.level);
    }

    public int getLevel() {
        return level;
    }

    public ItemStack getStack() {
        return stack;
    }

    public String getType() {
        return type.name().toLowerCase();
    }

    public static enum Type{
        CRAFTING,
        BREAKING;
    }
}
