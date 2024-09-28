package acctualyplugins.itemlottery.server.utils.senders;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.server.utils.Formatters;

/**
 * Utility class for sending messages to players and command senders in the lottery system.
 */
public class Message {

    /**
     * Formatter instance for formatting chat messages.
     */
    private static final Formatters formatters = ItemLottery.getInstance().getFormatters();

    /**
     * Sends a formatted message component to a command sender.
     * If the command sender is a player, the message is sent using the Adventure API.
     *
     * @param commandSender The command sender to whom the message is being sent.
     * @param MessageComponent The message component to send.
     */
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