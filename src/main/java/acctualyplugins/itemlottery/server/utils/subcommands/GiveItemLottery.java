package acctualyplugins.itemlottery.server.utils.subcommands;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.senders.Message;

import java.util.Map;

/**
 * Class for handling the Give Item Lottery command.
 */
public class GiveItemLottery {
    /**
     * Instance for managing logs.
     */
    private final LogManager logManager = new LogManager();

    /**
     * Instance for sending messages to players.
     */
    private final Message message = new Message();

    /**
     * Constructor for the GiveItemLottery class.
     * Retrieves the log by name, deserializes the item stack from the log, and adds the item to the player's inventory.
     *
     * @param player The player to whom the item is being given.
     * @param logName The name of the log containing the item stack.
     */
    public GiveItemLottery(Player player, String logName) {
        // Retrieve the log by name
        Log logToGive = logManager.getLog(logName);

        // Get the item stack configuration section from the log
        ConfigurationSection itemSectionToGive = logToGive.getItemStack();
        // Get the display name values from the item stack configuration section
        Map<String, Object> displayNameToGive = itemSectionToGive.getValues(false);

        // Deserialize the item stack from the display name values
        ItemStack itemToGive = ItemStack.deserialize(displayNameToGive);

        // Add the item to the player's inventory
        player.getInventory().addItem(itemToGive);
        // Send a message to the player indicating the item has been added
        message.sendMessageComponent(player, "&aItem has been added!");
    }
}