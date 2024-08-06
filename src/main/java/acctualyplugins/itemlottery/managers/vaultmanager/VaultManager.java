package starify.itemlottery.managers.vaultmanager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.RegisteredServiceProvider;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.managers.vaultmanager.utils.SetupEconomy;

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
            SetupEconomy.setupEconomy();
        }
    }
}