package acctualyplugins.itemlottery.managers.drawmanager;

import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.tasks.CountDown;
import acctualyplugins.itemlottery.server.utils.messages.Refactor;

import java.util.Map;

/**
 * Manager class for handling the lottery draw process.
 */
public class DrawManager {
    public static BukkitTask task;
    @Getter
    public static int remainingTime;
    @Getter
    public static BossBar bossBar;
    @Getter
    public static double ticketPrice;
    @Getter
    public static boolean ticketUse;
    private static int winnersCounter;
    private static Map<String, Object> serializedToGet;
    private static final CountDown countDown = new CountDown();
    private static final Refactor REFACTOR = new Refactor();

    /**
     * Translates alternate color codes in the given text.
     *
     * @param text The text to be translated.
     * @return The translated text with color codes.
     */
    public static String cc(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Starts the lottery draw with the specified parameters.
     * Initializes the boss bar, sets the remaining time, and schedules the countdown task.
     *
     * @param serialized The serialized data of the lottery item.
     * @param drawTime The duration of the draw in seconds.
     * @param player The player who initiated the lottery.

     */
    public static void drawItem(Map<String, Object> serialized, int drawTime, int winnersCount, Player player,
                                boolean ticketUse, double ticketPrice) {

        bossBar = BossBar.bossBar(REFACTOR.chatRefactor("&aLottery started!", player),
                1, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

        remainingTime = drawTime;
        serializedToGet = serialized;
        DrawManager.winnersCounter = winnersCount;
        DrawManager.ticketPrice = ticketPrice;
        task = Bukkit.getScheduler().runTaskTimer(ItemLottery.getInstance(),
                () -> countDown.countdownTask(serialized,
                        winnersCount, player, ticketUse, DrawManager.ticketPrice), 0, 20);

        // Task to update the log every 10 seconds
        Bukkit.getScheduler().runTaskTimer(ItemLottery.getInstance(), () -> {

            LogManager.lastLog.set("ElapsedTime", drawTime - 10);
            CreateLogsFile.save();
        }, 200, 200); // 200 ticks = 10 seconds
    }
    /**
     * Gets the serialized data of the lottery item.
     *
     * @return The serialized data of the lottery item.
     */
    public static Map<String, Object> getDrawItem() {
        return serializedToGet;
    }

    /**
     * Checks if the lottery draw is currently running.
     *
     * @return True if the draw is running, false otherwise.
     */
    public static boolean isRunning() {
        return task != null && !task.isCancelled();
    }

    /**
     * Gets the number of winners to be selected.
     *
     * @return The number of winners.
     */
    public static int getWinnersCount() {
        return winnersCounter;
    }
}