package acctualyplugins.itemlottery.managers.cooldownsmanager;

import lombok.Getter;
import acctualyplugins.itemlottery.managers.cooldownsmanager.utils.GetTimeRemaining;
import acctualyplugins.itemlottery.managers.cooldownsmanager.utils.HasCooldown;
import acctualyplugins.itemlottery.managers.cooldownsmanager.utils.LoadCooldowns;
import acctualyplugins.itemlottery.managers.cooldownsmanager.utils.SaveCooldowns;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.Map;

/**
 * Manages player cooldowns for the ItemLottery plugin.
 * Provides methods to load, save, set, check, and remove cooldowns.
 */
@Getter
public class CooldownsManager {
    private final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Loads the cooldowns from the configuration file.
     * Uses the LoadCooldowns utility class to perform the loading.
     */
    public void loadCooldowns() {
        new LoadCooldowns().loadCooldowns();
    }

    /**
     * Saves the cooldowns to the configuration file.
     * Uses the SaveCooldowns utility class to perform the saving.
     */
    public static void saveCooldowns() {
        SaveCooldowns.saveCooldowns();
    }

    /**
     * Sets the cooldown for the specified player.
     * Stores the current system time as the player's last usage time.
     * Saves the updated cooldowns to the configuration file.
     *
     * @param playerName The name of the player to set the cooldown for.
     */
    public void setCooldown(String playerName) {
        cooldowns.put(playerName, System.currentTimeMillis());
        saveCooldowns();
    }

    /**
     * Checks if the specified player has an active cooldown.
     * Uses the HasCooldown utility class to perform the check.
     *
     * @param playerName The name of the player to check.
     * @return True if the player has an active cooldown, false otherwise.
     */
    public boolean hasCooldown(String playerName) {
        return new HasCooldown().hasCooldown(playerName);
    }

    /**
     * Removes the cooldown for the specified player.
     * Reloads and saves the cooldowns after removal.
     *
     * @param playerName The name of the player to remove the cooldown for.
     */
    public void removeCooldown(String playerName) {
        cooldowns.remove(playerName);
        loadCooldowns();
        saveCooldowns();
    }

    /**
     * Gets the remaining cooldown time for the specified player.
     * Uses the GetTimeRemaining utility class to get the remaining time.
     *
     * @param playerName The name of the player to get the remaining time for.
     * @return The remaining cooldown time as a String.
     */
    public String getRemainingTime(String playerName) {
        return new GetTimeRemaining().getTimeRemaining(playerName);
    }
}