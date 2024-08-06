package starify.itemlottery.server.utils.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import starify.itemlottery.server.utils.Formatters;
import starify.itemlottery.server.utils.senders.Message;

public class AuthorLottery {
    private final static Message messageSend = new Message();
    public AuthorLottery(Player player) {
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "<center>&9&lPlugin Author");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "<center>&bAspDev");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "<center>&9&lPlugin Information");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "<center>&bhttps://starify.tech");
        messageSend.sendMessageComponent(player, "<center>&bhttps://dc.starify.tech");
        messageSend.sendMessageComponent(player, "");
    }
}
