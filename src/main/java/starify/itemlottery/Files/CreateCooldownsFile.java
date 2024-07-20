package starify.itemlottery.Files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import starify.itemlottery.ItemLottery;

import java.io.File;
import java.io.IOException;

public class CreateCooldownsFile {
    private static File file;
    private static FileConfiguration config;

    //Finds or generates the custom config file
    public static void setup(){
        file = new File(ItemLottery.getInstance().getDataFolder(), "cooldown.yml");

        if(!file.exists() ) {
            file.getParentFile().mkdirs();
            ItemLottery.getInstance().saveResource("cooldown.yml", false);
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

    public static FileConfiguration get(){
        return config;
    }

    public static void save(){
        try{
            config.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static void reload(){
        config = YamlConfiguration.loadConfiguration(file);
    }
}
