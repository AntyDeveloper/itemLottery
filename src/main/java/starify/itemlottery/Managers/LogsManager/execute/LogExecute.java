package starify.itemlottery.Managers.LogsManager.execute;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.Files.CreateLogsFile;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogExecute {
    private FileConfiguration getLogs() { return CreateLogsFile.get(); }

    public LogExecute(Player player, int WinnerCount, ItemStack itemStack) {
    ConfigurationSection logs = getLogs().getConfigurationSection("logs");

        if(logs == null) {
            logs = getLogs().createSection("logs");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm/dd:MM:yy");
        Date now = new Date();
        String formattedDate = dateFormat.format(now);
        ConfigurationSection newLog = logs.createSection(formattedDate);
        newLog.set("LotteryExecutor", player.getName());
        newLog.set("Item", itemStack.serialize());
        newLog.set("WinnersCount", WinnerCount);
        newLog.set("Winner", "");
        newLog.set("Lottery end", false);
        CreateLogsFile.save();
        CreateLogsFile.reload();

    }
}
