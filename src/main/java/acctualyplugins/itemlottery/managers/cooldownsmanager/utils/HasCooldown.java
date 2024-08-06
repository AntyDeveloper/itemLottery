package acctualyplugins.itemlottery.managers.cooldownsmanager.utils;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.Map;
import java.util.Objects;

/**
 * Utility class to check if a player has an active cooldown.
 */
public class HasCooldown {
    private final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Checks if the specified player has an active cooldown.
     *
     * @param playerName The name of the player to check.
     * @return True if the player has an active cooldown, false otherwise.
     */
    public boolean hasCooldown(String playerName) {
        int time = Objects.requireNonNull(ItemLottery.getInstance().getConfig()
                .getConfigurationSection("settings")).getInt("cooldownTime");
        long cooldownDuration = time * 1000L;
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastUsage) < cooldownDuration;
        }
        return false;
    }
}