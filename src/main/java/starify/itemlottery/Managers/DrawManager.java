package starify.itemlottery.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import starify.itemlottery.Files.CreateLogsFile;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.server.utils.subcommands.CreateLottery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DrawManager {
    public static BukkitTask task;
    private static int remainingTime;
    private static BossBar bossBar;
    private static int WinnersCounter;
    public static String cc(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }
    private static GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private static final CreateLogsFile createLogsFile = new CreateLogsFile();
    private static Map<String, Object> serializedToGet;
    private static String prefix = getLanguageMessage.getLanguageMessage("Prefix");



    public static ItemStack deserializeItemStack(Map<String, Object> serialized) {

        return ItemStack.deserialize(serialized);
    }

    public void drawItem(Map<String, Object> serialized, int drawTime, int WinnersCount, Player player) {
        bossBar = Bukkit.createBossBar(cc("&aLottery started!"), BarColor.BLUE, BarStyle.SOLID);
        remainingTime = drawTime;
        serializedToGet = serialized;
        WinnersCounter = WinnersCount;
       task = Bukkit.getScheduler().runTaskTimer(ItemLottery.getInstance(), () -> countdownTask(serialized,
               WinnersCount, player), 0, 20);
    }


    public static void Draw(Map<String, Object> serialized, int WinnersCount, Player lotteryMaker) {
        remainingTime = 0;

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (notEnoughPlayers(onlinePlayers, WinnersCount)) {
            handleNotEnoughPlayers(serialized, lotteryMaker);
            return;
        }

        List<Player> selectedPlayers = selectWinners(onlinePlayers, WinnersCount);
        String WinnersNameList = String.join(", ", getNames(selectedPlayers));

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> announceWinners(WinnersNameList));

        long delayInSeconds = 5;
        future.thenRunAsync(() -> {
            if (notEnoughPlayers(onlinePlayers, WinnersCount)) {
                handleNotEnoughPlayers(serialized, lotteryMaker);
                return;
            }

            announceWinners(WinnersNameList);
            distributeRewards(serialized, selectedPlayers);
            logWinners(WinnersNameList);
        }, CompletableFuture.delayedExecutor(delayInSeconds, TimeUnit.SECONDS));
        task.cancel();
        CreateLottery.run = false;
    }

    private static boolean notEnoughPlayers(List<Player> onlinePlayers, int WinnersCount) {
        return onlinePlayers.isEmpty() || WinnersCount < onlinePlayers.size();
    }

    private static void handleNotEnoughPlayers(Map<String, Object> serialized, Player lotteryMaker) {
        bossBar.removeAll();
        Bukkit.broadcastMessage(cc(getLanguageMessage.getLanguageMessage("NotEnoughOnlinePlayers", "Lottery")));
        ItemStack deserializedItem = deserializeItemStack(serialized);
        lotteryMaker.getInventory().addItem(deserializedItem);
        CreateLottery.run = false;
        task.cancel();
    }

    private static List<Player> selectWinners(List<Player> onlinePlayers, int WinnersCount) {
        List<Player> selectedPlayers = new ArrayList<>();
        while (selectedPlayers.size() < WinnersCount) {
            int randomIndex = (int) (Math.random() * onlinePlayers.size());
            Player selectedPlayer = onlinePlayers.get(randomIndex);

            if (!selectedPlayers.contains(selectedPlayer)) {
                selectedPlayers.add(selectedPlayer);
            }
        }
        return selectedPlayers;
    }

    private static List<String> getNames(List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }

    private static void announceWinners(String WinnersNameList) {
        bossBar.removeAll();
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendTitle(cc(getLanguageMessage.getLanguageMessage("Title", "Lottery")), cc("&9&l§k" + WinnersNameList + " "), 1, 100, 1);
        }
    }

    private static void distributeRewards(Map<String, Object> serialized, List<Player> winners) {
        ItemStack deserializedItem = deserializeItemStack(serialized);
        int rewardCountOnePlayer = deserializedItem.getAmount() / WinnersCounter;
        for (Player player : winners) {
            deserializedItem.setAmount(rewardCountOnePlayer);
            player.getInventory().addItem(deserializedItem);
            player.sendMessage(cc(getLanguageMessage.getLanguageMessage("WinMessage", "Draw")));
        }
    }

    private static void logWinners(String WinnersNameList) {
        String logName = LogManager.lastLog.getName();
        ConfigurationSection logs = createLogsFile.get().getConfigurationSection("logs");
        assert logs != null;
        ConfigurationSection log = logs.getConfigurationSection(logName);
        log.set("Winner", WinnersNameList);
        log.set("Lottery end", true);
        createLogsFile.save();
        createLogsFile.reload();
    }

    private void countdownTask(Map<String, Object> serialized, int WinnersCount, Player player) {
        int test = (int) (remainingTime - bossBar.getProgress() * remainingTime);
        double progress = (double) test / remainingTime;

        if (remainingTime > 0) {
            for (Player players : Bukkit.getOnlinePlayers()) {
                String BosBarTitle = getLanguageMessage.getLanguageMessage("BosBarTitle",
                        "Lottery");
                String ReplaceBosBarTitle;
                if (remainingTime >= 60) {
                    int minutes = remainingTime / 60;
                    int seconds = remainingTime % 60;
                    ReplaceBosBarTitle = BosBarTitle.replace("%time%", minutes + "m " + seconds + "s");
                } else {
                    ReplaceBosBarTitle = BosBarTitle.replace("%time%", remainingTime + "s");
                }
                bossBar.setTitle(cc(ReplaceBosBarTitle));
                bossBar.addPlayer(players);
                bossBar.setProgress(progress);
            }
            remainingTime--;
        } else {
            Draw(serialized, WinnersCount, player);
            CreateLottery.run = false;
        }
        }
        public static Map<String, Object> getDrawItem() {
           return serializedToGet;
        }
        public static BukkitTask getTask() {
        return  task;
        }

        public static boolean isRunning() {
        return remainingTime <= 0;
        }
        public static BossBar getBossBar() {
        return bossBar;
        }

        public static int getWinnersCount() {return WinnersCounter; }
    }




