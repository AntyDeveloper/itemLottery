package starify.itemlottery.server.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import starify.itemlottery.server.utils.messages.Refractor;

public class Formatters {
    private final Refractor refactor = new Refractor();

    public Component chatFormater(String text, Player player) {
        return refactor.ChatRefactor(text, player);
    }
}
