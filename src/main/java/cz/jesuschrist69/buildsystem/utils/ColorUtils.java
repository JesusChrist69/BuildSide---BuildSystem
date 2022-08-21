package cz.jesuschrist69.buildsystem.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ColorUtils {

    /**
     * This method takes a string, and replaces all instances of '&' with the Minecraft color code character
     *
     * @param text The text to colorize.
     * @return The text with the color codes replaced with the actual color.
     */
    public String colorize(@NotNull String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
