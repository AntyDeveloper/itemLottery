package acctualyplugins.itemlottery.server.utils.subcommands;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.drawmanager.utils.bossbar.RemoveBossbar;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.TaskManager;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.handlers.DrawHandlers;
import acctualyplugins.itemlottery.server.utils.handlers.TaskHandler;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class for handling the End Lottery command.
 */
public class EndLottery {
    /**
     * Instance for retrieving language messages.
     */
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Instance for managing the boss bar display.
     */
    private final BossBar bossBar = DrawManager.getBossBar();

    /**
     * Instance for sending messages to players.
     */
    private final Message message = new Message();


    /**
     * Ends the lottery draw and announces the end to the player.
     *
     * @param player The player executing the command.
     */
    public void drawEndLottery(Player player) {
        try {
            Map<String, Object> serialized = DrawManager.getDrawItem();
            int winnersCount = DrawManager.getWinnersCount();

           TaskHandler.isTaskRunning(player, false);

            DrawHandlers.selectDrawManager(serialized, bossBar, winnersCount, player, DrawManager.ticketUse);
            message.sendMessageComponent(player, getLanguageMessage
                    .getLanguageMessage("ForceEnd", "LotteryCommandArgs"));
        } catch (Exception e) {
            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs"));
        }
    }

    /**
     * Forces the end of the lottery, cancels tasks, and clears relevant data.
     *
     * @param player The player executing the command.
     */
    public void forceEndLottery(Player player) {
        TaskHandler.isTaskRunning(player, false);

        DrawManager.task.cancel();
        TaskManager.selectedPlayers.clear();
        ServiceManager.tickets.clear();

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        new RemoveBossbar().removeBossbar(bossBar);


        onlinePlayers.forEach(playerSend -> {

            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage(
                    "ForeEndBroadcast", "LotteryCommandArgs"));
            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage(
                    "ForceEnd", "LotteryCommandArgs"));
        });
    }
}