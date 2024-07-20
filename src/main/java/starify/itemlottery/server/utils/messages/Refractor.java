package starify.itemlottery.server.utils.messages;

import de.themoep.minedown.adventure.MineDown;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Refractor {
    public Component ChatRefactor(String text, Player player) {
        String newtext = text.replaceAll("%player%", player.getName());
        newtext = newtext.replaceAll("%players%", player.getName());
        newtext = newtext.replaceAll("<center>", "");
        Component replacedColors = new MineDown(newtext).toComponent();

        if (text.contains("<center>")) {
            val spaces = Centering.spacePrefix(newtext);
            return new MineDown(spaces + newtext).toComponent();
        }
        return replacedColors;
    }
}
