package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;

/**
 * Handler class for managing task-related operations in the lottery system.
 */
public class TaskHandler {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Checks if a task is currently running.
     * If a task is running, sends a message to the player.
     *
     * @param player The player initiating the check.
     */
    public static void isTaskRunning(Player player, boolean status) {
        if (status)
            if (DrawManager.isRunning()) {
                message.sendMessageComponent(player,
                        getLanguageMessage.getLanguageMessage("CommandRun",
                                "LotteryCommandArgs"));
                return;
            }
        if(!status) {
        if (!DrawManager.isRunning()) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("CommandNotRun",
                            "LotteryCommandArgs"));
            return;
        }
        return;
    }

    }
}