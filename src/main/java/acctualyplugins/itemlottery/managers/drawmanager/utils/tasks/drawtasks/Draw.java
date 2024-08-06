package starify.itemlottery.managers.drawmanager.utils.tasks.drawtasks;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import starify.itemlottery.managers.drawmanager.utils.SelectWinners;
import starify.itemlottery.managers.drawmanager.utils.intalization.players.GetNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static starify.itemlottery.managers.drawmanager.DrawManager.remainingTime;
import static starify.itemlottery.managers.drawmanager.DrawManager.task;
import static starify.itemlottery.managers.drawmanager.utils.annoucments.WinnersAnnouncement.announceWinners;
import static starify.itemlottery.managers.drawmanager.utils.annoucments.WinnersAnnouncement.announceWinnersAnimation;
import static starify.itemlottery.managers.drawmanager.utils.handlers.HandleNotEnoughPlayers.handleNotEnoughPlayers;
import static starify.itemlottery.managers.drawmanager.utils.intalization.players.NotEnoughPlayers.notEnoughPlayers;
import static starify.itemlottery.managers.drawmanager.utils.logs.UpdateLogs.logWinners;
import static starify.itemlottery.managers.drawmanager.utils.rewards.DistributeRewards.distributeRewards;
import static starify.itemlottery.managers.drawmanager.utils.tasks.TaskManager.selectedPlayers;

public class Draw {
    public static void draw(Map<String, Object> serialized, int WinnersCount, Player lotteryMaker, BossBar bossBar) {
        remainingTime = 0;

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (notEnoughPlayers(onlinePlayers, WinnersCount)) {
            handleNotEnoughPlayers(serialized, lotteryMaker, bossBar, task);
            return;
        }

        SelectWinners.selectWinners(WinnersCount);
        String WinnersNameList = String.join(", ", GetNames.getNames(selectedPlayers));

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> announceWinnersAnimation(WinnersNameList,
                bossBar));

        long delayInSeconds = 5;
        future.thenRunAsync(() -> {
            announceWinners(WinnersNameList);
            distributeRewards(serialized, selectedPlayers, WinnersCount);
            logWinners(WinnersNameList);
        }, CompletableFuture.delayedExecutor(delayInSeconds, TimeUnit.SECONDS));
        task.cancel();
        selectedPlayers.clear();
    }

}
