package starify.itemlottery.Managers;

import org.bukkit.configuration.ConfigurationSection;
import starify.itemlottery.Files.CreateCooldownsFile;
import starify.itemlottery.ItemLottery;

import java.util.HashMap;
import java.util.Map;

public class CooldownsManager {
    private Map<String, Long> cooldowns = new HashMap<>();
    private CreateCooldownsFile createCooldownsFile = new CreateCooldownsFile();

    private int time;
    private  long cooldownDuration;
    public boolean hasCooldown(String playerName) {
        time = ItemLottery.getInstance().getConfig().getConfigurationSection("settings").getInt("cooldownTime");
        cooldownDuration = time * 1000;
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis();
            return (currentTime - lastUsage) < cooldownDuration;
        }
        return false;
    }
    public String getTimeRemaining(String playerName) {
        time = ItemLottery.getInstance().getConfig().getConfigurationSection("settings").getInt("cooldownTime");
        cooldownDuration = time * 1000L;
        if (cooldowns.containsKey(playerName)) {
            long lastUsage = cooldowns.get(playerName);
            long currentTime = System.currentTimeMillis();
            long timeRemaining = lastUsage + cooldownDuration - currentTime;
            int timeRemainingInSec = (int) (timeRemaining / 1000);
            String output;
            if (timeRemainingInSec >= 60) {
                int minutes = timeRemainingInSec / 60;
                int seconds = timeRemainingInSec % 60;
                output =  minutes + "m " + seconds + "s";
            } else {
                output = timeRemainingInSec + "s";
            }

            // Jeśli pozostały czas jest większy lub równy zero, to zwracamy pozostały czas w milisekundach.
            if (timeRemaining >= 0) {
                return output;
            }
        }
        // Jeśli gracz nie ma opóźnienia lub opóźnienie minęło, zwracamy 0.
        return "null";
    }
    public void loadCooldowns() {
        ConfigurationSection cooldownsSection;
                cooldownsSection = CreateCooldownsFile.get().getConfigurationSection("cooldowns");
        if(cooldownsSection == null) {
             cooldownsSection = CreateCooldownsFile.get().createSection("cooldowns");
            for (String playerName : cooldownsSection.getKeys(false)) {
                ConfigurationSection playerSection = cooldownsSection.getConfigurationSection(playerName);
                if(playerSection != null) {
                    try {
                        long lastUsage = playerSection.getLong("cooldown");
                        cooldowns.put(playerName, lastUsage);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        } else {
            for (String playerName : cooldownsSection.getKeys(false)) {
                ConfigurationSection playerSection = cooldownsSection.getConfigurationSection(playerName);
                if (playerSection != null) {
                    try {
                        long lastUsage = playerSection.getLong("cooldown");
                        cooldowns.put(playerName, lastUsage);
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
    }

    public void setCooldown(String playerName) {

        cooldowns.put(playerName, System.currentTimeMillis());

        // Zapisujemy opóźnienia do pliku
        saveCooldowns();
    }

    public void saveCooldowns() {
        for (String playerName : cooldowns.keySet()) {
            long lastUsage = cooldowns.get(playerName);
            ConfigurationSection cooldownsSection = CreateCooldownsFile.get().getConfigurationSection("cooldowns");
            if (cooldownsSection == null) {
                cooldownsSection = CreateCooldownsFile.get().createSection("cooldowns");
            }

            ConfigurationSection playerSection = cooldownsSection.getConfigurationSection(playerName);
            if (playerSection == null) {
                playerSection = cooldownsSection.createSection(playerName);
            }

            playerSection.set("cooldown", lastUsage);
            CreateCooldownsFile.save();

        }
        CreateCooldownsFile.save();
    }
}
