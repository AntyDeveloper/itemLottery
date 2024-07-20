package starify.itemlottery.Managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.Files.CreateLogsFile;
import starify.itemlottery.Managers.LogsManager.execute.LogExecute;

import java.util.ArrayList;
import java.util.List;

public class LogManager {
    public static ConfigurationSection lastLog;
    private FileConfiguration getLogs() { return CreateLogsFile.get(); }


    public static void createNewLog(ItemStack itemStack, Player player, int WinnerCount) {
        new LogExecute(player, WinnerCount, itemStack);
    }
    public String getLastLog() {
        return lastLog.getName();
        }
    public ConfigurationSection getLog(String logName) {
        ConfigurationSection logs = getLogs().getConfigurationSection("logs");
        assert logs != null;
        for (String key : logs.getKeys(false)) {
            if(key.equalsIgnoreCase(logName)) {
                return logs.getConfigurationSection(key);
            }
        }
        return null;
    }
    public List<String> logList() {
        ConfigurationSection logs = getLogs().getConfigurationSection("logs");
        if(logs != null) {
            return new ArrayList<>(logs.getKeys(false));
        }
        return new ArrayList<>();
    }
}
