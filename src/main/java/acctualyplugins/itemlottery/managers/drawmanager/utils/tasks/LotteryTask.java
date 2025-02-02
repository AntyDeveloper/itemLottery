package acctualyplugins.itemlottery.managers.drawmanager.utils.tasks;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.services.ServiceManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class LotteryTask {
    private final Map<String, Object> serialized;
    private final int drawTime;
    private final int winnersCount;
    @Getter
    private final Player player;
    private final boolean ticketUse;
    private final double ticketPrice;
    private final Runnable onComplete;
    @Getter
    private final Log log;

    public LotteryTask(Map<String, Object> serialized, int drawTime, int winnersCount, Player player, boolean ticketUse, double ticketPrice, Runnable onComplete, Log log) {
        this.serialized = serialized;
        this.drawTime = drawTime;
        this.winnersCount = winnersCount;
        this.player = player;
        this.ticketUse = ticketUse;
        this.ticketPrice = ticketPrice;
        this.onComplete = onComplete;
        this.log = log;
    }

    public void start(Runnable onComplete) {
        DrawManager.drawItem(serialized, drawTime, winnersCount, player, ticketUse, ticketPrice);
        // Additional logic to handle after the task is complete
        Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), onComplete, drawTime * 20L);

        // Task to update the log every 10 seconds
        Bukkit.getScheduler().runTaskTimer(ItemLottery.getInstance(), () -> {
            ServiceManager.editElaspedTime(log.getLogName(), drawTime - 10);
            CreateLogsFile.save();
        }, 200, 200); // 200 ticks = 10 seconds
    }
}