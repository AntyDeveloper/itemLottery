// LogExecute.java
package acctualyplugins.itemlottery.managers.logmanager.execute;

import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogExecute {
    private final Log log;

    public LogExecute(Player player, int winnerCount, ItemStack itemStack, int duration, int elapsedTime, boolean ticketUse, double ticketCost, long timestamp) {
        ConfigurationSection logs = getLogs().getConfigurationSection("logs");

        if (logs == null) {
            logs = getLogs().createSection("logs");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm/dd:MM:yy");
        Date now = new Date();
        String formattedDate = dateFormat.format(now);
        ConfigurationSection newLog = logs.createSection(formattedDate);
        newLog.set("LotteryExecutor", player.getName());
        newLog.set("Item", itemStack.serialize());
        newLog.set("WinnersCount", winnerCount);
        newLog.set("Winner", "");
        newLog.set("LotteryEnd", false);
        newLog.set("Duration", duration);
        newLog.set("ElapsedTime", elapsedTime);
        newLog.set("TicketUse", ticketUse);
        newLog.set("TicketCost", ticketCost);
        newLog.set("Timestamp", timestamp); // Set the timestamp
        LogManager.lastLog = newLog;
        CreateLogsFile.save();
        CreateLogsFile.reload();
        ServiceManager.setLogsList();

        this.log = new Log(
            formattedDate,
            player.getName(),
            itemStack.serialize(),
            "",
            winnerCount,
            false,
            duration,
            elapsedTime,
            ticketUse,
            ticketCost,
            timestamp // Pass the timestamp
        );
    }

    private FileConfiguration getLogs() {
        return CreateLogsFile.get();
    }

    public Log getLog() {
        return log;
    }
}