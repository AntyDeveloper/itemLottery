package starify.itemlottery.managers.cooldownsmanager.utils;

import starify.itemlottery.ItemLottery;
import starify.itemlottery.managers.cooldownsmanager.CooldownsManager;
import starify.itemlottery.services.ServiceManager;

import java.util.Map;
import java.util.Objects;

/**
 * Utility class to get the remaining cooldown time for a player.
 */
public class GetTimeRemaining {
    /**
     * The duration of the cooldown in milliseconds.
     */
    public long cooldownDuration;

    /**
     * A map storing the cooldowns for each player.
     */
    private final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Gets the remaining cooldown time for a player.
     *
     * @param playerName The name of the player.
     * @return The remaining cooldown time as a string in the format "Xm Ys" or "Ys", or null if no cooldown is active.
     */
    public String getTimeRemaining(String playerName) {
        // Retrieve the cooldown time from the configuration
        int time = Objects.requireNonNull(ItemLottery.getInstance().getConfig()
                .getConfigurationSection("settings")).getInt("cooldownTime");
        cooldownDuration = time * 1000L;

        // Check if the player has an active cooldown
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = lastUsage + cooldownDuration - currentTime;

            // If there is remaining time, format it as "Xm Ys" or "Ys"
            if (timeRemaining >= 0) {
                int timeRemainingInSec = (int) (timeRemaining / 1000);
                if (timeRemainingInSec >= 60) {
                    int minutes = timeRemainingInSec / 60;
                    int seconds = timeRemainingInSec % 60;
                    return minutes + "m " + seconds + "s";
                } else {
                    return timeRemainingInSec + "s";
                }
            }
            // Reload cooldowns if the time has expired
            new LoadCooldowns().loadCooldowns();
        }
        return null;
    }
}