package acctualyplugins.itemlottery.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import acctualyplugins.itemlottery.ItemLottery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * This class handles the creation, loading, and validation of the configuration file for the ItemLottery plugin.
 */
public class CreateConfigFile {
    /**
     * The configuration object to hold the settings from the config.yml file.
     */
    static FileConfiguration config = new YamlConfiguration();

    /**
     * Creates the configuration file if it does not exist, checks the version, and validates its contents.
     */
    public void createFiles() {
        // Get config file
        File configFile = new File(ItemLottery.getInstance().getDataFolder(), "config.yml");

        // Create file if needed
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            ItemLottery.getInstance().saveResource("config.yml", false);
        }

        // Load configuration
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        // Check version and validate contents
        checkVersionAndUpdate(configFile);
        validateConfig();
    }

    /**
     * Checks if the configuration version matches the plugin's default version.
     * If not, updates the configuration with missing entries.
     *
     * @param configFile The configuration file to update.
     */
    private void checkVersionAndUpdate(File configFile) {
        InputStream defaultConfigStream = ItemLottery.getInstance().getResource("config.yml");
        if (defaultConfigStream == null) {
            throw new IllegalStateException("Default config.yml not found in the plugin jar!");
        }

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
        String defaultConfigVersion = defaultConfig.getString("config-version");
        String currentConfigVersion = config.getString("config-version");

        if (currentConfigVersion == null || !currentConfigVersion.equals(defaultConfigVersion)) {
            // Overwrite the existing config file with the default one to preserve comments
            ItemLottery.getInstance().saveResource("config.yml", true);
            try {
                config.load(configFile);
            } catch (InvalidConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Validates the configuration and adds missing entries from the default configuration.
     */


    private void validateConfig() {
        InputStream defaultConfigStream = ItemLottery.getInstance().getResource("config.yml");
        if (defaultConfigStream == null) {
            throw new IllegalStateException("Default config.yml not found in the plugin jar!");
        }

        FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
            new InputStreamReader(defaultConfigStream)
        );

        Set<String> defaultKeys = defaultConfig.getKeys(true);
        for (String key : defaultKeys) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        saveConfig(new File(ItemLottery.getInstance().getDataFolder(), "config.yml"));
    }

    /**
     * Saves the configuration to the file.
     *
     * @param configFile The file to save the configuration to.
     */
    private void saveConfig(File configFile) {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the loaded configuration.
     *
     * @return The loaded configuration.
     */
    public static FileConfiguration get() {
        return config;
    }
}