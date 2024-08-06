package starify.itemlottery.managers.updatemanager;

import org.bukkit.Bukkit;
import org.bukkit.util.Consumer;
import starify.itemlottery.ItemLottery;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static org.bukkit.Bukkit.getLogger;

/**
 * Manager class for handling update checks for the ItemLottery plugin.
 */
public class UpdateManager {
    /**
     * Flag indicating whether an update message has been sent to operators.
     */
    public static boolean opMessageSend = false;

    /**
     * Retrieves the latest version of the plugin from the SpigotMC API.
     * Runs the version check asynchronously.
     *
     * @param consumer A consumer to handle the retrieved version string.
     */
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

    /**
     * Checks for updates to the ItemLottery plugin.
     * If an update is available, logs a message and sets the opMessageSend flag to true.
     */
    public static void checkUpdate() {
        boolean updateBoolean =  ItemLottery.getInstance().getConfig().getBoolean("UpdateChecker");
        if(updateBoolean) {
            new UpdateManager().getVersion(version -> {
                if (!ItemLottery.getInstance().getDescription().getVersion().equals(version)) {
                    getLogger().info("There is a new update available.");
                    opMessageSend = true;
                }
            });
        }
    }
}