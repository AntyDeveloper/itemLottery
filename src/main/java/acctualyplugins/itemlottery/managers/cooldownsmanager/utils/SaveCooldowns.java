package starify.itemlottery.managers.cooldownsmanager.utils;

import org.bukkit.configuration.ConfigurationSection;
import starify.itemlottery.files.CreateCooldownsFile;
import starify.itemlottery.services.ServiceManager;

import java.util.Map;

/**
 * Utility class for saving player cooldowns to the configuration file.
 */
public class SaveCooldowns {
    private static final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Saves the cooldowns to the configuration file.
     * Iterates through the cooldowns map and updates the configuration with the player's last usage time.
     * If a player's section does not exist in the configuration, it creates a new section for the player.
     */
    public static void saveCooldowns() {
        ConfigurationSection cooldownsSection = CreateCooldownsFile.get().getConfigurationSection("cooldowns");
        if (cooldownsSection == null) {
            cooldownsSection = CreateCooldownsFile.get().createSection("cooldowns");
        }

        for (String playerName : cooldowns.keySet()) {
            long lastUsage = cooldowns.get(playerName);
            ConfigurationSection playerSection = cooldownsSection.getConfigurationSection(playerName);
            if (playerSection == null) {
                playerSection = cooldownsSection.createSection(playerName);
            }
            playerSection.set("cooldown", lastUsage);
        }
        CreateCooldownsFile.save();
    }
}