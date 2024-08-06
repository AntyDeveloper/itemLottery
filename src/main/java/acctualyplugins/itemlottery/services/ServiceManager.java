package acctualyplugins.itemlottery.services;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceManager class for managing various services related to the item lottery.
 */
public class ServiceManager {
    /**
     * Retrieves the logs configuration file.
     *
     * @return The FileConfiguration object for the logs.
     */
    private static FileConfiguration getLogs() { return CreateLogsFile.get(); }

    /**
     * List to store log objects.
     */
    public static final ArrayList<Log> LogsList = new ArrayList<>();

    /**
     * Populates the LogsList with log entries from the configuration file.
     */
    public static void setLogsList() {
        ConfigurationSection logs = getLogs().getConfigurationSection("logs");
        if(logs != null) {
            for(String key : logs.getKeys(false)) {
                ConfigurationSection log = logs.getConfigurationSection(key);
                if(log != null) {
                    LogsList.add(new Log(log.getName(), log.getString("LotteryExecutor"), log.
                            getConfigurationSection("Item"),
                            log.getString("Winner"),
                            log.getInt("WinnersCount"), log.getBoolean("Lottery end")));
                }
            }
        }
    }

    /**
     * Map to store player cooldowns.
     */
    public static final Map<String, Long> cooldowns = new HashMap<>();

    /**
     * List to store players with tickets.
     */
    public static final ArrayList<Player> tickets = new ArrayList<>();
}