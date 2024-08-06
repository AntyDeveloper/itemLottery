package acctualyplugins.itemlottery.server.utils.messages;

import de.themoep.minedown.adventure.MineDown;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Utility class for refactoring chat messages in the lottery system.
 */
public class Refactor {

    /**
     * Refactors a chat message by replacing placeholders and centering the text if needed.
     *
     * @param text The original chat message text.
     * @param player The player whose name will replace the placeholders.
     * @return The refactored chat message as a Component.
     */
    public Component ChatRefactor(String text, Player player) {
        // Replace placeholders with the player's name
        String newtext = text.replaceAll("%player%", player.getName());
        newtext = newtext.replaceAll("%players%", player.getName());
        newtext = newtext.replaceAll("<center>", "");
        Component replacedColors = new MineDown(newtext).toComponent();

        // Center the text if it contains the <center> tag
        if (text.contains("<center>")) {
            val spaces = Centering.spacePrefix(newtext);
            return new MineDown(spaces + newtext).toComponent();
        }
        return replacedColors;
    }
}