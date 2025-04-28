package acctualyplugins.itemlottery;

import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.server.utils.Formatters;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.server.utils.senders.Title;
import acctualyplugins.itemlottery.server.utils.subcommands.QueueLottery;
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
    // Metoda do pobierania instancji pluginu
    /**
     * Singleton instance of the ItemLottery plugin.
     * -- GETTER --
     *  Retrieves the singleton instance of the ItemLottery plugin.
     */

    @Getter
    public static ItemLottery instance;

    // Instances for creating configuration, cooldowns, and logs files
    private final CreateConfigFile createConfigFile = new CreateConfigFile();
    private final CreateCooldownsFile createCooldownsFile = new CreateCooldownsFile();
    private final CreateLogsFile createLogsFile = new CreateLogsFile();
    @Getter
    private DrawManager drawManager;

    public LogManager logManager;

    /**
     * Retrieves the list of logs.
     *
     * @return The list of logs.
     */
    public ArrayList<Log> getLogList() {
        return ServiceManager.LogsList;
    }

    // Instance for handling Bukkit audiences
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
     * Called when the plugin is enabled.
     * Initializes various components and registers events and commands.
     */
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Register configs
        registerConfigs();

        // Register APIs
        registerApis();

        // Register commands
        commandRegistration();
        this.drawManager = new DrawManager(this); // Stwórz instancję DrawManager

        // Register events
        eventRegistration();

        LotteryQueue.loadQueueFromFile();
    }


    /**
     * Called when the plugin is loaded.
     * Initializes the CommandAPI with verbose output.
     */
    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).verboseOutput(false));
    }

    /**
     * Registers configuration files and loads cooldowns.
     */
    private void registerConfigs() {
        // Create files
        createConfigFile.createFiles();
        createCooldownsFile.setup();
        createLogsFile.setup();

        // Load cooldowns
        new LoadCooldowns().loadCooldowns();

        // Set logs list
        ServiceManager.setLogsList();
    }

    /**
     * Registers various APIs including CommandAPI, Adventure, Vault, and metrics.
     */
    private void registerApis() {
        // Register CommandAPI
        CommandAPI.onEnable();
        // Register Adventure
        this.adventure = BukkitAudiences.create(this);

        // Register Vault
        new VaultManager().registerVault();

        // Register metrics
        new MetricsLite(this, 19956);

        // Update checker
        UpdateManager.checkUpdate();
    }

    /**
     * Registers commands for the plugin.
     */
    private void commandRegistration() {
        // Register commands
        new CreateLotteryCommand();
    }

    /**
     * Registers event listeners for the plugin.
     */
    private void eventRegistration() {
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new QueueLottery(), this);
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