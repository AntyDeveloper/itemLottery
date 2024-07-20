package starify.itemlottery.server.utils.subcommands;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.Managers.LogManager;
import starify.itemlottery.server.utils.Formatters;
import starify.itemlottery.server.utils.senders.Message;

import java.util.Map;
import java.util.Objects;

import static starify.itemlottery.Managers.DrawManager.cc;

public class HistoryLottery {
    private final Formatters formatters = new Formatters();

    private Message messageSend = new Message();

    public void ShowHistoryLottery(Player player, String logName) {


        ConfigurationSection log = new LogManager().getLog(logName);

        ConfigurationSection itemSection = log.getConfigurationSection("Item");
        assert itemSection != null;
        Map<String, Object> itemMap = itemSection.getValues(false);
        ItemStack item = ItemStack.deserialize(itemMap);
        String displayName = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
                ? item.getItemMeta().getDisplayName()
                : item.getType().toString();


        messageSend.sendMessageComponent(player, "&7----------------------------------");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "&9LogId: &7 " + log.getName());
        messageSend.sendMessageComponent(player, "&9LotteryExecutor &7" +
                log.getString("LotteryExecutor"));
        messageSend.sendMessageComponent(player, "&9Item: &7" + displayName );
        messageSend.sendMessageComponent(player, "&9Winner: &7" + log.getString("Winner"));
        messageSend.sendMessageComponent(player, "&9Lottery end &7" + log.getString("Lottery end"));
        messageSend.sendMessageComponent(player, "<click:run_command:/lottery devitemgive " + log.getName()
                +">&7[&aClick&7] &9To summon winning item!");
        messageSend.sendMessageComponent(player, "");
        messageSend.sendMessageComponent(player, "&7----------------------------------");

    }
}
