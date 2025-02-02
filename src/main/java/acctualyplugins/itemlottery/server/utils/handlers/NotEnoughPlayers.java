package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;

/**
 * Handler class for managing player count checks in the lottery system.
 */
public class NotEnoughPlayers {

    /**
     * Language message retriever instance.
     */
    private final static GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Message sender instance.
     */
    private final static Message message = new Message();

    /**
     * Checks if there are enough online players to proceed with the lottery.
     * If there are not enough players, sends a message to the initiating player.
     *
     * @param WinnerCount The number of winners to select.
     * @param player The player initiating the check.
     * @return True if there are enough players, false otherwise.
     */
    public static boolean notEnoughPlayers(int WinnerCount, Player player, int itemAmount) {
        if(Bukkit.getOnlinePlayers().isEmpty() || WinnerCount > Bukkit.getOnlinePlayers().size()) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("NotEnoughOnlinePlayers",
                            "LotteryCommandArgs"));
            return false;
        }
        if (WinnerCount % 2 == 0 && itemAmount % 2 != 0) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("MismatchWinnerItemCount",
                            "LotteryCommandArgs"));
            return false;
        }
        return true;
    }
}