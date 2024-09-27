package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.server.utils.senders.Message;

import java.util.Arrays;
import java.util.List;

public class AuthorLottery {
    public AuthorLottery(Player player) {
        Message message = ItemLottery.getInstance().message;

        List<String> strings = Arrays.asList(
                " ",
                "<center>&9&lPlugin Author",
                "",
                "<center>&bAspDev",
                " ",
                "<center>&9&lPlugin Information",
                " ",
                "<center>&bhttps://acctualyplugins.eu",
                "<center>&bhttps://dc.acctualyplugins.eu/",
                " "
        );

        for (String string : strings) {
            message.sendMessageComponent(player, string);
        }
    }
}