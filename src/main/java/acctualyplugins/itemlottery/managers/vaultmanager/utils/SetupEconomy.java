package acctualyplugins.itemlottery.managers.vaultmanager.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import acctualyplugins.itemlottery.ItemLottery;

import static org.bukkit.Bukkit.getServer;

/**
 * Utility class for setting up the economy instance using Vault.
 */
public class SetupEconomy {

    public static Economy econ;


    /**
     * Sets up the economy instance.
     * If the Vault plugin is not found, disables the ItemLottery plugin.
     * If the economy provider is not found, disables the ItemLottery plugin.
     */
    public static void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {

            ItemLottery.getInstance().getLogger()
                    .severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(ItemLottery.getInstance());
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            ItemLottery.getInstance().getLogger()
                    .severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(ItemLottery.getInstance());
        }
        assert rsp != null;
        econ = rsp.getProvider();
        if(econ == null) {
            ItemLottery.getInstance().getLogger()
                    .severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(ItemLottery.getInstance());
        }
    }
}