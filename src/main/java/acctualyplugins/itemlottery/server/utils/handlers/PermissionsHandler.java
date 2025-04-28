package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;

/**
 * Handler class for managing permissions in the lottery system.
 */
public class PermissionsHandler {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Checks if a player has a specific permission.
     * If the player does not have the permission, sends a message to the player.
     *
     * @param player The player whose permissions are being checked.
     * @param permission The permission to check.
     * @param messageId The ID of the message to send if the player lacks the permission.
     * @return True if the player has the permission, false otherwise.
     */
    public static boolean hasPermission(Player player, String permission, String messageId) {
        if(!player.hasPermission(permission)) {
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage(messageId));
            return false;
        }
        return true;
    }

    /**
     * Checks if a player can use an item based on their permissions and the item count.
     * If the player does not have the required permission or enough items, sends a message to the player.
     *
     * @param player    The player whose item use is being checked.
     * @param playerUse Whether the player is allowed to use the item.
     * @param itemStack The ItemStack to check.
     * @param itemCount The number of items required.
     */
    public static boolean playerUse(Player player, boolean playerUse, ItemStack itemStack, int itemCount) {
        if (playerUse && player.hasPermission("lottery.playeruse") || player.hasPermission("lottery.create")) {
            if (!player.hasPermission("lottery.create")) {
                if(itemStack.getAmount() < itemCount) {
                    message.sendMessageComponent(player,
                            getLanguageMessage.getLanguageMessage("NotEnoughItems",
                                    "LotteryCommandArgs"));
                    return false;
                }
            }
        } else if(!playerUse && player.hasPermission("lottery.playeruse")) {
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("NoPermission"));
            return false;
        }
        return true;
    }

    public static class PermissionException extends Exception {
        public PermissionException(String message) {
            super(message);
        }
    }

}