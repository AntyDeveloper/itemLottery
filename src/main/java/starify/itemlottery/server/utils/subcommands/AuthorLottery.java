package starify.itemlottery.server.utils.subcommands;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import starify.itemlottery.server.utils.Formatters;
import starify.itemlottery.server.utils.senders.Message;

public class AuthorLottery {

    private final Formatters formatters = new Formatters();
    private Message messageSend = new Message();
    public AuthorLottery(Player player) {
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "&7------------------------");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "&9Author&7: AspDev");
        messageSend.sendMessageComponent(player, "&9Website: &bhttps://starify.tech");
        messageSend.sendMessageComponent(player, "&9Discord: &bhttps://dc.starify.tech");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "&7------------------------");
        messageSend.sendMessageComponent(player, "");
    }
}
