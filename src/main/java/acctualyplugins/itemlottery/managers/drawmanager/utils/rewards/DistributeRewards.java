package acctualyplugins.itemlottery.managers.drawmanager.utils.rewards;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for distributing rewards to the winners of the lottery draw.
 */
public class DistributeRewards {
    private static final Message message = new Message();
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Distributes the rewards to the selected players.
     * Deserializes the item, calculates the reward count for each player, sends a win message, and adds the item to the player's inventory.
     * Logs the item name and handles any exceptions that occur during the process.
     *
     * @param serialized The serialized data of the reward item.
     * @param selectedPlayers The list of players who won the lottery.
     * @param WinnersCounter The number of winners.
     */
    public static void distributeRewards(Map<String, Object> serialized, List<Player> selectedPlayers,
                                         int WinnersCounter) {
        try {
            ItemStack deserializedItem = ItemStack.deserialize(serialized);
            int rewardCountOnePlayer = deserializedItem.getAmount() / WinnersCounter;
            for (Player player : selectedPlayers) {
                message.sendMessageComponent(player, getLanguageMessage
                        .getLanguageMessage("WinMessage", "Lottery"));
                ItemLottery.getInstance().getLogger()
                        .info(Objects.requireNonNull(deserializedItem.getItemMeta()).getDisplayName());
                deserializedItem.setAmount(rewardCountOnePlayer);
                player.getInventory().addItem(deserializedItem);
            }
        } catch (Exception e) {
            ItemLottery.getInstance().getLogger().info(e.toString());
        }
    }
}