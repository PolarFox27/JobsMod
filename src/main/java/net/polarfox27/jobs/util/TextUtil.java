package net.polarfox27.jobs.util;

import net.minecraft.util.text.*;
import java.util.List;

public class TextUtil {


    public static final StringTextComponent NEW_LINE = new StringTextComponent("\n");
    public static final StringTextComponent EMPTY = new StringTextComponent("");

    /**
     * Colors a component using the specified formatting.
     * @param formatting the formatting to apply to the component
     * @param component the text component to modify
     * @return the formatted text component
     */
    public static IFormattableTextComponent coloredComponent(TextFormatting formatting, IFormattableTextComponent component){
        return component.setStyle(Style.EMPTY.withColor(formatting));
    }

    /**
     * Returns a colored string of the provided number
     * @param formatting the formatting/color used
     * @param number the number we want to represent
     * @return a colored string of the provided number
     */
    public static String coloredNum(TextFormatting formatting, long number){
        return formatting + Long.toString(number);
    }

    /**
     * Returns a colored string of the provided number
     * @param formatting the formatting/color used
     * @param number the number we want to represent
     * @return a colored string of the provided number
     */
    public static String coloredNum(TextFormatting formatting, int number){
        return formatting + Integer.toString(number);
    }

    /**
     * Merges 2 text components with a new line character between them
     * @param previous the first text component
     * @param newline the second text component
     * @return the 2 components merged.
     */
    public static IFormattableTextComponent appendNewLine(IFormattableTextComponent previous, IFormattableTextComponent newline){
        return previous.append(NEW_LINE).append(newline);
    }

    /**
     * Forms a new TextComponent from all the lines provided
     * @param lines the lines to merge as one text component
     * @return the formed text component
     */
    public static IFormattableTextComponent appendAllLines(List<IFormattableTextComponent> lines){
        if(lines.isEmpty())
            return EMPTY;
        IFormattableTextComponent result = lines.get(0);
        if(lines.size() == 1)
            return result;
        for(IFormattableTextComponent t : lines.subList(1, lines.size()))
            result = appendNewLine(result, t);
        return result;
    }
}
