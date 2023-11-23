package net.polarfox27.jobs.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

import java.util.List;

public class TextUtil {


    public static final TextComponent NEW_LINE = new TextComponent("\n");
    public static final TextComponent EMPTY = new TextComponent("");

    /**
     * Colors a component using the specified formatting.
     * @param formatting the formatting to apply to the component
     * @param component the text component to modify
     * @return the formatted text component
     */
    public static MutableComponent coloredComponent(ChatFormatting formatting, MutableComponent component){
        return component.setStyle(Style.EMPTY.withColor(TextColor.fromLegacyFormat(formatting)));
    }

    /**
     * Returns a colored string of the provided number
     * @param formatting the formatting/color used
     * @param number the number we want to represent
     * @return a colored string of the provided number
     */
    public static String coloredNum(ChatFormatting formatting, long number){
        return formatting + Long.toString(number);
    }

    /**
     * Returns a colored string of the provided number
     * @param formatting the formatting/color used
     * @param number the number we want to represent
     * @return a colored string of the provided number
     */
    public static String coloredNum(ChatFormatting formatting, int number){
        return formatting + Integer.toString(number);
    }

    /**
     * Merges 2 text components with a new line character between them
     * @param previous the first text component
     * @param newline the second text component
     * @return the 2 components merged.
     */
    public static MutableComponent appendNewLine(MutableComponent previous, MutableComponent newline){
        return previous.append(NEW_LINE).append(newline);
    }

    /**
     * Forms a new TextComponent from all the lines provided
     * @param lines the lines to merge as one text component
     * @return the formed text component
     */
    public static MutableComponent appendAllLines(List<MutableComponent> lines){
        if(lines.isEmpty())
            return EMPTY;
        MutableComponent result = lines.get(0);
        if(lines.size() == 1)
            return result;
        for(MutableComponent t : lines.subList(1, lines.size()))
            result = appendNewLine(result, t);
        return result;
    }
}
