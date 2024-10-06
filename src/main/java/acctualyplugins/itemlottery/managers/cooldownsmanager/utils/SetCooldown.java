package acctualyplugins.itemlottery.managers.cooldownsmanager.utils;

import acctualyplugins.itemlottery.managers.cooldownsmanager.CooldownsManager;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.Map;

/**
 * Utility class for setting a player's cooldown.
 */
public class SetCooldown {
    private final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Sets the cooldown for the specified player.
     * Stores the current system time as the player's last usage time.
     * Saves the updated cooldowns to the configuration file.
     *
     * @param playerName The name of the player to set the cooldown for.
     */
    public void setCooldown(String playerName) {
        cooldowns.put(playerName, System.currentTimeMillis());
        new CooldownsManager();
        CooldownsManager.saveCooldowns();
    }
}