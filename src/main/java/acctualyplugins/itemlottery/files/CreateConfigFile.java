package acctualyplugins.itemlottery.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import acctualyplugins.itemlottery.ItemLottery;

import java.io.File;
import java.io.IOException;

/**
 * This class handles the creation and loading of the configuration file for the ItemLottery plugin.
 */
public class CreateConfigFile {
    /**
     * The configuration object to hold the settings from the config.yml file.
     */
    static FileConfiguration config = new YamlConfiguration();

    /**
     * Creates the configuration file if it does not exist and loads its contents.
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