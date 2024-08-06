package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.managers.vaultmanager.utils.GetEconomy;
import acctualyplugins.itemlottery.server.utils.senders.Message;

/**
 * Handler class for managing economy-related operations in the lottery system.
 */
public class EconomyHandlers {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Checks if a player has enough balance to cover a specified price.
     * If the player does not have enough balance, sends a message to the player.
     *
     * @param player The player whose balance is being checked.
     * @param price The price to check against the player's balance.
     * @return True if the player has enough balance, false otherwise.
     */
    public boolean checkPlayerBalance(Player player, double price) {
        // Check player balance
        if (GetEconomy.getEconomy().getBalance(player) < price) {

            String messageReplace = getLanguageMessage.getLanguageMessage("NotEnoughMoney",
                            "LotteryCommandArgs")
                    .replace("%price%", price+"");

            message.sendMessageComponent(player, messageReplace);
            return false;
        }
        return true;
    }

    /**
     * Removes a specified amount from a player's balance.
     *
     * @param player The player whose balance is being deducted.
     * @param price The amount to deduct from the player's balance.
     */
    public void removePlayerBalance(Player player, double price) {
        GetEconomy.getEconomy().withdrawPlayer(player, price);
    }
}