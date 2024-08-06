package acctualyplugins.itemlottery.server.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.server.utils.messages.Refactor;

/**
 * Utility class for formatting chat messages.
 */
public class Formatters {
    /**
     * Instance for refactoring chat messages.
     */
    private final Refactor refactor = new Refactor();

    /**
     * Formats a chat message for a player.
     *
     * @param text The text to format.
     * @param player The player for whom the text is being formatted.
     * @return The formatted chat message as a Component.
     */
    public Component chatFormater(String text, Player player) {
        return refactor.ChatRefactor(text, player);
    }
}