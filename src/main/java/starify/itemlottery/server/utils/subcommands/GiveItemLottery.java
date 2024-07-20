package starify.itemlottery.server.utils.subcommands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.Managers.LogManager;
import starify.itemlottery.server.utils.senders.Message;

import java.util.Map;

public class GiveItemLottery {
    private final LogManager logManager = new LogManager();
    private final Message message = new Message();
    public GiveItemLottery(Player player, String logName) {
        ConfigurationSection logToGive = logManager.getLog(logName);

        ConfigurationSection itemSectionToGive = logToGive.getConfigurationSection("Item");
        Map<String, Object> displayNameToGive = itemSectionToGive.getValues(false);

        ItemStack itemToGive = ItemStack.deserialize(displayNameToGive);

        player.getInventory().addItem(itemToGive);
        message.sendMessageComponent(player, "&aItem has been added!");
    }
}
