package starify.itemlottery.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import starify.itemlottery.ItemLottery;

import java.io.File;
import java.io.IOException;

/**
 * This class handles the creation, loading, saving, and reloading of the logs configuration file for the ItemLottery plugin.
 */
public class CreateLogsFile {
    private static File file;
    private static FileConfiguration config;

    /**
     * Finds or generates the custom logs configuration file.
     * If the file does not exist, it creates the file and its parent directories.
     * Loads the configuration from the file.
     */
    public void setup(){
        file = new File(ItemLottery.getInstance().getDataFolder(), "logs.yml");

        if(!file.exists() ) {
            file.getParentFile().mkdirs();
            ItemLottery.getInstance().saveResource("logs.yml", false);
        }

        // Setup configuration
        config = new YamlConfiguration();

        // Load configuration
        try {
            config.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the loaded logs configuration.
     *
     * @return The loaded logs configuration.
     */
    public static FileConfiguration get(){
        return config;
    }

    /**
     * Saves the logs configuration to the file.
     * If an IOException occurs, it prints an error message to the console.
     */
    public static void save(){
        try{
            config.save(file);
        }catch (IOException e){
            ItemLottery.getInstance().getLogger().warning("Couldn't save file");
        }
    }

    /**
     * Reloads the logs configuration from the file.
     */
    public static void reload(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}