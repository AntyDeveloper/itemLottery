package starify.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import starify.itemlottery.managers.drawmanager.DrawManager;
import starify.itemlottery.managers.languagemanager.GetLanguageMessage;
import starify.itemlottery.server.utils.senders.Message;

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
     * @return True if no task is running, false otherwise.
     */
    public static boolean isTaskRunning(Player player) {
        if (DrawManager.isRunning()) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("CommandRun",
                            "LotteryCommandArgs"));
            return false;
        }
        return true;
    }
}