package starify.itemlottery.managers.logmanager.execute;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.files.CreateLogsFile;
import starify.itemlottery.services.ServiceManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class for executing log operations related to the lottery.
 */
public class LogExecute {

    /**
     * Retrieves the logs configuration file.
     *
     * @return The logs configuration file.
     */
    private FileConfiguration getLogs() {
        return CreateLogsFile.get();
    }

    /**
     * Constructor for creating a new log entry for a lottery execution.
     * Initializes the log entry with the executor's name, item details, winner count, and other relevant information.
     *
     * @param player The player who executed the lottery.
     * @param WinnerCount The number of winners in the lottery.
     * @param itemStack The item stack used in the lottery.
     */
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
        newLog.set("LotteryDraw", false);
        CreateLogsFile.save();
        CreateLogsFile.reload();
        ServiceManager.setLogsList();
    }
}