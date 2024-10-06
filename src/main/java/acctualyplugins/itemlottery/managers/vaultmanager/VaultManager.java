package acctualyplugins.itemlottery.managers.vaultmanager;

import org.bukkit.configuration.ConfigurationSection;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.vaultmanager.utils.SetupEconomy;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

/**
 * Manager class for handling Vault integration.
 */
public class VaultManager {
    /**
     * Configuration section for settings.
     */
    ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");

    /**
     * Registers the Vault plugin and sets up the economy if the ticket system is enabled.
     */
    public void registerVault() {
        if(settings.getBoolean("ticketSystem")) {
            if(!SetupEconomy.setupEconomy()) {
                getServer().getPluginManager().disablePlugin(ItemLottery.getInstance());
                return;
            };

        }
    }
}