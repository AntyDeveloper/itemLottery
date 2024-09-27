package acctualyplugins.itemlottery;

import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.server.utils.senders.Title;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import acctualyplugins.itemlottery.commands.CreateLotteryCommand;
import acctualyplugins.itemlottery.events.PlayerJoinListener;
import acctualyplugins.itemlottery.files.CreateConfigFile;
import acctualyplugins.itemlottery.files.CreateCooldownsFile;
import acctualyplugins.itemlottery.files.CreateLogsFile;
import acctualyplugins.itemlottery.managers.cooldownsmanager.utils.LoadCooldowns;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.managers.metricsmamager.MetricsLite;
import acctualyplugins.itemlottery.managers.updatemanager.UpdateManager;
import acctualyplugins.itemlottery.managers.vaultmanager.VaultManager;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.ArrayList;

/**
 * Main class for the ItemLottery plugin.
 * Handles the initialization and shutdown of the plugin.
 */
public final class ItemLottery extends JavaPlugin {
    /**
     * Singleton instance of the ItemLottery plugin.
     * -- GETTER --
     *  Retrieves the singleton instance of the ItemLottery plugin.
     *
     */
    @Getter
    public static ItemLottery instance;
    private final CreateConfigFile createConfigFile = new CreateConfigFile();
    private final CreateCooldownsFile createCooldownsFile = new CreateCooldownsFile();
    private final CreateLogsFile createLogsFile = new CreateLogsFile();

    public final Message message = new Message();

    public final Title title = new Title();

    /**
     * Retrieves the list of logs.
     *
     * @return The list of logs.
     */
    public ArrayList<Log> getLogList() { return ServiceManager.LogsList; }

    /**
     * Instance for handling Bukkit audiences.
     */
    private BukkitAudiences adventure;

    /**
     * Retrieves the Bukkit audiences instance.
     *
     * @return The Bukkit audiences instance.
     * @throws IllegalStateException if the plugin is disabled.
     */
    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException(
                    "Tried to access Adventure when the plugin was disabled!"
            );
        }
        return this.adventure;
    }

    /**
     * Called when the plugin is loaded.
     * Initializes the CommandAPI with verbose output.
     */
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(true));
    }

    /**
     * Called when the plugin is enabled.
     * Initializes various components and registers events and commands.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        // Create files
        createConfigFile.createFiles();
        createCooldownsFile.setup();
        createLogsFile.setup();

        ServiceManager.setLogsList();




        // Register vault
        new VaultManager().registerVault();

        this.adventure = BukkitAudiences.create(this);

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        CommandAPI.onEnable();

        new CreateLotteryCommand();

        // Update checker
        UpdateManager.checkUpdate();

        // Register metrics
        new MetricsLite(this, 19956);

        // Load cooldowns
        new LoadCooldowns().loadCooldowns();
    }

    /**
     * Called when the plugin is disabled.
     * Saves cooldowns and disables the CommandAPI.
     */
    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        // Plugin shutdown logic
    }
}