package starify.itemlottery;

import dev.jorel.commandapi.CommandAPI;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import starify.itemlottery.Commands.CreateLotteryCommand;
import starify.itemlottery.Events.PlayerJoinListener;
import starify.itemlottery.Files.CreateConfigFile;
import starify.itemlottery.Files.CreateCooldownsFile;
import starify.itemlottery.Files.CreateLogsFile;
import starify.itemlottery.Managers.CooldownsManager;
import starify.itemlottery.Managers.LogManager;
import starify.itemlottery.Managers.MetricsLite;
import starify.itemlottery.Managers.UpdateManager;

import java.util.List;

public final class ItemLottery extends JavaPlugin {
    public List<String> getLogList() { return setLogList(); }
    private static ItemLottery instance;
    private final CreateConfigFile createConfig = new CreateConfigFile();
    private final CreateLogsFile createLogsFile = new CreateLogsFile();
    private final CooldownsManager cooldownsManager = new CooldownsManager();
    private final LogManager logManager = new LogManager();
    public List<String> setLogList() { return logManager.logList(); }
    private final  CreateCooldownsFile createCooldownsFile = new CreateCooldownsFile();
    public UpdateManager updateManager = new UpdateManager();

    public static ItemLottery getInstance() { return instance; }

    private BukkitAudiences adventure;


    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException(
                    "Tried to access Adventure when the plugin was disabled!"
            );
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        //create files
        createConfig.createFiles();
        createLogsFile.setup();
        createCooldownsFile.setup();

        this.adventure = BukkitAudiences.create(this);

        //register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        CommandAPI.onEnable();


        //update checker
        updateManager.checkUpdate();

        //register metrics
        MetricsLite metricsLite = new MetricsLite(this, 19956);

        //lod logs
        setLogList();

        //load cooldowns
        cooldownsManager.loadCooldowns();
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        // Plugin shutdown logic
        cooldownsManager.saveCooldowns();
    }
}
