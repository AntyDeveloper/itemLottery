package starify.itemlottery.managers.logmanager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.managers.logmanager.execute.LogExecute;
import starify.itemlottery.managers.logmanager.objects.Log;

import java.util.List;

/**
 * Manager class for handling log operations related to the lottery.
 */
public class LogManager {
    /**
     * The last log entry created.
     */
    public static ConfigurationSection lastLog;

    /**
     * Creates a new log entry for a lottery execution.
     *
     * @param itemStack The item stack used in the lottery.
     * @param player The player who executed the lottery.
     * @param WinnerCount The number of winners in the lottery.
     */
    public static void createNewLog(ItemStack itemStack, Player player, int WinnerCount) {
        new LogExecute(player, WinnerCount, itemStack);
    }

    /**
     * Retrieves a log entry by its name.
     *
     * @param logName The name of the log entry to retrieve.
     * @return The log entry corresponding to the given name, or null if not found.
     */
    public Log getLog(String logName) {
        List<Log> logs = ItemLottery.getInstance().getLogList();

        for (Log log : logs) {
            if(log.getLogName().equalsIgnoreCase(logName)) {
                return log;
            }
        }
        return null;
    }
}