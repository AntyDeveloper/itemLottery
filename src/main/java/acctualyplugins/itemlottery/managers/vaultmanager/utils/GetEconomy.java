package starify.itemlottery.managers.vaultmanager.utils;

import net.milkbowl.vault.economy.Economy;


/**
 * Utility class for retrieving the economy instance.
 */
public class GetEconomy {

    /**
     * Retrieves the economy instance.
     *
     * @return The economy instance.
     */
    public static Economy getEconomy() {
        return SetupEconomy.econ;
    }
}