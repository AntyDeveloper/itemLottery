package acctualyplugins.itemlottery.managers.drawmanager.utils.logs;

import org.bukkit.configuration.ConfigurationSection;
import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.services.ServiceManager;

/**
 * Utility class for updating the logs with the lottery draw results.
 */
public class UpdateLogs {

    /**
     * Logs the winners of the lottery draw.
     * Updates the log file with the winners' names and marks the lottery draw as completed.
     * Reloads the log file and updates the logs list in the service manager.
     *
     * @param WinnersNameList A string containing the list of winners' names.
     */
    public static void logWinners(String WinnersNameList) {
        String logName = LogManager.lastLog.getName();
        ConfigurationSection logs = CreateLogsFile.get().getConfigurationSection("logs");
        assert logs != null;
        ConfigurationSection log = logs.getConfigurationSection(logName);
        assert log != null;
        log.set("Winner", WinnersNameList);
        log.set("LotteryEnd", true);
        CreateLogsFile.save();
        CreateLogsFile.reload();
        ServiceManager.setLogsList();
    }

}