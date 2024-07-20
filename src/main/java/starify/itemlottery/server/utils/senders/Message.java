package starify.itemlottery.server.utils.senders;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.server.utils.Formatters;

public class Message {

    private static final Formatters formatters = new Formatters();

    public void sendMessageComponent(
            final CommandSender commandSender,
            @NonNull String MessageComponent
    ) {
        if (commandSender instanceof Player) {
            Player target = (Player) commandSender;
            Component ComponentTranslated = formatters.chatFormater(
                    MessageComponent,
                    target
            );
            ItemLottery.getInstance()
                    .adventure()
                    .player(target)
                    .sendMessage(ComponentTranslated);
        }
    }
}
