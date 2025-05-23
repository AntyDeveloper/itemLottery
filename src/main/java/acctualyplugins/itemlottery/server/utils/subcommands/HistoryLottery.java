package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.guimanager.LogGui;
import acctualyplugins.itemlottery.server.utils.handlers.PermissionsHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.Map;
import java.util.Objects;

/**
 * Class for handling the History Lottery command.
 */
public class HistoryLottery {
    /**
     * Instance for sending messages to players.
     */
    private final Message message = new Message();

    /**
     * Shows the history of a lottery based on the log name.
     * Retrieves the log, deserializes the item stack, and sends the log details to the player.
     *
     * @param player The player requesting the history.
     */
    public void ShowHistoryLottery(Player player) {
        PermissionsHandler.hasPermission(player, "lottery.end", "Permissions");


        new LogGui().openGuis(player);

//        assert itemSection != null;
//        // Get the item stack values from the configuration section
//        Map<String, Object> itemMap = itemSection.getValues(false);
//        // Deserialize the item stack from the values
//        ItemStack item = ItemStack.deserialize(itemMap);
//        // Get the display name of the item, or use the item type if no display name is present
//        String displayName = item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()
//                ? item.getItemMeta().getDisplayName()
//                : item.getType().toString();
//
//        // Send the log details to the player
//        message.sendMessageComponent(player, "");
//        message.sendMessageComponent(player, "&9LogId: &7 " + log.getLogName());
//        message.sendMessageComponent(player, "&9LotteryExecutor &7" +
//                log.getLotteryExecutor());
//        message.sendMessageComponent(player, "&9Item: &7" + displayName );
//        message.sendMessageComponent(player, "&9Winner: &7" + log.getWinners());
//        message.sendMessageComponent(player, "&9Lottery end &7" + log.isLotteryEnd());
//        message.sendMessageComponent(player, "");
//        message.sendMessageComponent(player, "[&7[&aClick&7] &9To summon winning item!]" +
//                "(run_command=/lottery giveitem "
//                + log.getLogName()
//                +")");
//        message.sendMessageComponent(player, "");
    }
}