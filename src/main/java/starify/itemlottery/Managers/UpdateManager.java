package starify.itemlottery.Managers;

import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;
import starify.itemlottery.ItemLottery;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.bukkit.Bukkit.getLogger;

public class UpdateManager {
    public static boolean opMessageSend = false;

    private void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(ItemLottery.getInstance(), () -> {
            try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=" +
                    "112964" + "/~").openStream(); Scanner scann = new Scanner(is)) {
                if (scann.hasNext()) {
                    consumer.accept(scann.next());
                }
            } catch (IOException e) {
                getLogger().info("Unable to check for updates: " + e.getMessage());
            }
        });
    }

    public void checkUpdate() {
        boolean updateBoolean =  ItemLottery.getInstance().getConfig().getBoolean("UpdateChecker");
        if(updateBoolean) {
            new UpdateManager().getVersion(version -> {
                if (ItemLottery.getInstance().getDescription().getVersion().equals(version)) {
                } else {
                    getLogger().info("There is a new update available.");
                    opMessageSend = true;
                }
            });
        }
    }
}
