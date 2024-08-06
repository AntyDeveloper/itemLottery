package acctualyplugins.itemlottery.server.utils.subcommands;

import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.server.utils.senders.Message;

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
        messageSend.sendMessageComponent(player, "<center>&bhttps://acctualyplugins.eu");
        messageSend.sendMessageComponent(player, "<center>&bhttps://dc.acctualyplugins.eu/");
        messageSend.sendMessageComponent(player, "");
    }
}
