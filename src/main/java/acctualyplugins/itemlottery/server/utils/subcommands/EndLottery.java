// EndLottery.java
package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.TaskManager;
import acctualyplugins.itemlottery.server.utils.handlers.PermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.drawmanager.utils.bossbar.RemoveBossbar;
import acctualyplugins.itemlottery.server.utils.handlers.TaskHandler;
import acctualyplugins.itemlottery.server.utils.handlers.DrawHandlers;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EndLottery {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Message message = ItemLottery.getInstance().message;

    public void drawEndLottery(Player player) {
        try {
            PermissionsHandler.hasPermission(player, "lottery.end", "Permissions");
            Map<String, Object> serialized = DrawManager.getDrawItem();
            int winnersCount = DrawManager.getWinnersCount();

            TaskHandler.isTaskRunning(player, false);

            DrawHandlers.selectDrawManager(serialized, DrawManager.getBossBar(), winnersCount, player, DrawManager.ticketUse);
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("ForceEnd", "LotteryCommandArgs"));

            Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::startNextLottery, 6000L); // 5 minutes delay
        } catch (Exception e) {
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs"));
        }
    }

    public void forceEndLottery(Player player) {
        TaskHandler.isTaskRunning(player, false);

        DrawManager.task.cancel();
        TaskManager.selectedPlayers.clear();
        ServiceManager.tickets.clear();

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        new RemoveBossbar().removeBossbar(DrawManager.getBossBar());

        onlinePlayers.forEach(playerSend -> {
            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage("ForeEndBroadcast", "LotteryCommandArgs"));
            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage("ForceEnd", "LotteryCommandArgs"));
        });

        Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::startNextLottery, 6000L); // 5 minutes delay
    }

}