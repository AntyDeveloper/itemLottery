package acctualyplugins.itemlottery.services;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceManager {
    public static final ArrayList<Log> LogsList = new ArrayList<>();

    public static void setLogsList() {
        ConfigurationSection logs = ItemLottery.getInstance().getConfig().getConfigurationSection("logs");
        if (logs != null) {
            for (String key : logs.getKeys(false)) {
                ConfigurationSection log = logs.getConfigurationSection(key);
                if (log != null) {
                    LogsList.add(new Log(
                            log.getName(),
                            log.getString("LotteryExecutor"),
                            convertConfigurationSectionToMap(log.getConfigurationSection("Item")),
                            log.getString("Winner"),
                            log.getInt("WinnersCount"),
                            log.getBoolean("LotteryEnd"),
                            log.getInt("duration"),
                            log.getInt("elapsedTime"),
                            log.getBoolean("ticketUse"),
                            log.getDouble("ticketCost"),
                            log.getLong("timestamp")
                    ));
                }
            }
        }
    }

    private static Map<String, Object> convertConfigurationSectionToMap(ConfigurationSection section) {
        Map<String, Object> map = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                map.put(key, section.get(key));
            }
        }
        return map;
    }

    public static void editElaspedTime(String logName, int elapsedTime) {
        for (Log log : LogsList) {
            if (log.getLogName().equals(logName)) {
                ConfigurationSection logs = ItemLottery.getInstance().getConfig().getConfigurationSection("logs");
                ConfigurationSection logSection = logs.getConfigurationSection(logName);
                log.setElapsedTime(elapsedTime);
                logSection.set("ElapsedTime", elapsedTime);
                break;
            }
        }
    }

    public static final Map<String, Long> cooldowns = new HashMap<>();
    public static final ArrayList<Player> tickets = new ArrayList<>();
}