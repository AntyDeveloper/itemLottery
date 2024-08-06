package starify.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import starify.itemlottery.managers.drawmanager.DrawManager;
import starify.itemlottery.managers.languagemanager.GetLanguageMessage;
import starify.itemlottery.server.utils.senders.Message;

import java.util.List;

/**
 * Handler class for broadcasting messages related to the lottery.
 */
public class BroadcastHandlers {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Broadcasts the start message of the lottery to all online players.
     *
     * @param onlinePlayers List of online players to send the message to.
     * @param displayName The display name of the item being broadcasted.
     */
    public static void broadcastStartMessage(List<Player> onlinePlayers, String displayName, boolean ticketInUse) {
        String broadcastMessage = getLanguageMessage.getLanguageMessage("StartBroadCastMessage", "Lottery");
        String replaceMessage = broadcastMessage.replace("%item_name%", displayName);

        String ticketMessage = getLanguageMessage.getLanguageMessage("TicketCostMessage", "Lottery");
        String replaceTicketMessage = ticketMessage.replace("%cost%", DrawManager.getTicketPrice()+"");

        onlinePlayers.forEach(playerSend -> {
            if (ticketInUse) {
                message.sendMessageComponent(playerSend, replaceTicketMessage);
                return;
            }
            message.sendMessageComponent(playerSend, replaceMessage);
        });
    }

    /**
     * Broadcasts the remaining time of the lottery to a player.
     *
     * @param player The player to send the message to.
     * @param time The remaining time of the lottery.
     * @return True if the time is greater than 59 seconds, false otherwise.
     */
    public static boolean broadcastTime(Player player, int time) {
        if (time <= 59) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("Time", "LotteryCommandArgs"));
            return false;
        }
        return true;
    }
}