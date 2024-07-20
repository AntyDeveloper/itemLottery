package starify.itemlottery.Files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import starify.itemlottery.ItemLottery;

import java.io.File;
import java.io.IOException;

public class CreateConfigFile {
    static FileConfiguration config = new YamlConfiguration();
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
    public static FileConfiguration get(){
        return config;
    }

}
