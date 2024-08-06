package acctualyplugins.itemlottery.managers.cooldownsmanager.utils;

import org.bukkit.configuration.ConfigurationSection;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.files.CreateCooldownsFile;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.Map;
import java.util.Objects;

/**
 * Utility class for loading player cooldowns from the configuration file.
 */
public class LoadCooldowns {
    private final Map<String, Long> cooldowns = ServiceManager.cooldowns;

    /**
     * Loads the cooldowns from the configuration file.
     * If a player's cooldown has expired, it removes the player from the configuration and memory.
     * Otherwise, it loads the player's last usage time into memory.
     */
    public void loadCooldowns() {
        int time = Objects.requireNonNull(ItemLottery.getInstance().getConfig()
                .getConfigurationSection("settings")).getInt("cooldownTime");
        ConfigurationSection cooldownsSection = CreateCooldownsFile.get().getConfigurationSection("cooldowns");
        if (cooldownsSection == null) {
            cooldownsSection = CreateCooldownsFile.get().createSection("cooldowns");
        }
        long currentTime = System.currentTimeMillis();
        for (String playerName : cooldownsSection.getKeys(false)) {
            ConfigurationSection playerSection = cooldownsSection.getConfigurationSection(playerName);
            if (playerSection == null) {
                playerSection = cooldownsSection.createSection(playerName);
            }
            try {
                long lastUsage = playerSection.getLong("cooldown");
                long cooldownDuration = time * 1000L;
                if (currentTime - lastUsage >= cooldownDuration) {
                    cooldownsSection.set(playerName, null); // Remove from configuration
                    cooldowns.remove(playerName); // Remove from memory
                } else {
                    cooldowns.put(playerName, lastUsage);
                }
            } catch (Exception e) {
                // Log the exception if needed
            }
        }
        CreateCooldownsFile.save();
    }
}