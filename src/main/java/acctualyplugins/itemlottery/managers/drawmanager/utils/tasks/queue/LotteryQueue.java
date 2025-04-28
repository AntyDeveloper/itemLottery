package acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue; // Dopasuj pakiet

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.drawmanager.LotteryLogData;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID; // Import UUID
import java.io.File; // Potrzebne importy do obsługi plików
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Map; // Potrzebne importy do obsługi plików
import org.bukkit.scheduler.BukkitTask;

public class LotteryQueue {
    // Używamy teraz UUID jako klucza? Lub nadal logName? Bezpieczniej UUID.
    private static final Queue<LotteryTask> mainQueue = new LinkedList<>();
    private static BukkitTask scheduledCheckTask = null; // Do śledzenia zaplanowanego zadania

    // Metoda do dodawania (wymaga teraz więcej danych)
    public static void addLotteryToQueue(LotteryLogData logData) {
        mainQueue.offer(new LotteryTask(logData));
        ItemLottery.getInstance().getLogger().info("Lottery " + logData.getQueueId() + " added to queue.");
        checkAndStartNextLottery(); // Spróbuj uruchomić, jeśli nic nie działa
    }

    public static List<LotteryTask> getMainQueue() {
        return new LinkedList<>(mainQueue); // Zwróć kopię
    }

    // Metoda do usuwania loterii z kolejki (używa UUID)
    public static void removeLotteryFromQueue(UUID queueId) {
        boolean removed = mainQueue.removeIf(task -> task.getLogData().getQueueId().equals(queueId));
        if (removed) {
            ItemLottery.getInstance().getLogger().info("Lottery " + queueId + " removed from queue.");
        }
    }

    // Metoda do pobierania loterii po UUID (zamiast nazwy)
    public static LotteryTask getLotteryByQueueId(UUID queueId) {
        return mainQueue.stream()
                .filter(task -> task.getLogData().getQueueId().equals(queueId))
                .findFirst()
                .orElse(null);
    }


    // Metoda do pomijania - zatrzymuje bieżącą i startuje następną
    public static void skip() {
        ItemLottery.getInstance().getLogger().info("Attempting to skip lottery...");
        if (DrawManager.isRunning()) {
            ItemLottery.getInstance().getLogger().info("Stopping current lottery to skip...");
            DrawManager.stopLottery(); // Zatrzymaj bieżącą loterię
            // UWAGA: stopLottery może już wywoływać checkAndStartNextLottery po resecie stanu.
            // Jeśli tak, to poniższe wywołanie jest redundantne lub nawet szkodliwe.
            // Trzeba sprawdzić logikę w stopLottery/resetLotteryState.
            // Dla bezpieczeństwa, wywołajmy z małym opóźnieniem, zakładając, że stopLottery nie startuje kolejnej.
            Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::checkAndStartNextLottery, 5L); // Opóźnienie 5 ticków
        } else {
            ItemLottery.getInstance().getLogger().info("No lottery running, starting next from queue if available.");
            checkAndStartNextLottery(); // Jeśli nic nie działało, po prostu wystartuj następną
        }
    }

    // Sprawdza, czy można uruchomić kolejną loterię i ją uruchamia
    public static synchronized void checkAndStartNextLottery() { // Użyj synchronized lub innego mechanizmu blokującego
        // Anuluj poprzednie zaplanowane zadanie, jeśli istniało,
        // ponieważ teraz wykonujemy sprawdzenie ręcznie.
        if (scheduledCheckTask != null && !scheduledCheckTask.isCancelled()) {
            scheduledCheckTask.cancel();
        }
        scheduledCheckTask = null;

        // Upewnij się, że nie ma już aktywnej loterii
        if (DrawManager.isRunning()) {
            ItemLottery.getInstance().getLogger().fine("Lottery already running, queue check skipped.");
            return;
        }

        LotteryTask nextTask = mainQueue.peek(); // Spójrz, nie usuwaj jeszcze

        if (nextTask != null) {
            LotteryLogData data = nextTask.getLogData();
            long scheduledTime = data.getScheduledStartTimeMillis(); // Zakładamy, że ta metoda istnieje
            long currentTime = System.currentTimeMillis();

            if (currentTime >= scheduledTime) {
                // Czas nadszedł, uruchom loterię
                mainQueue.poll(); // Teraz usuń z kolejki
                ItemLottery.getInstance().getLogger().info("Starting lottery " + data.getQueueId() + " from queue.");

                Player maker = Bukkit.getPlayerExact(data.getPlayerName());
                if (maker == null) {
                    ItemLottery.getInstance().getLogger().warning("Player " + data.getPlayerName() + " for queued lottery " + data.getQueueId() + " is offline. Lottery will start without maker context.");
                }

                DrawManager.drawItem(
                    data.getSerializedItem(),
                    data.getDuration(),
                    data.getWinnersCount(),
                    maker,
                    data.isTicketUse(),
                    data.getTicketPrice()
                );
                 // Po uruchomieniu, od razu sprawdź, czy kolejna nie powinna ruszyć (jeśli ma czas startu 0 lub przeszły)
                 // To może być ryzykowne, jeśli start loterii jest asynchroniczny. Bezpieczniej jest polegać na triggerNextLotteryCheck.
                 // checkAndStartNextLottery(); // Można rozważyć, ale ostrożnie

            } else {
                // Loteria jest zaplanowana na przyszłość
                long delayMillis = scheduledTime - currentTime;
                long delayTicks = Math.max(1, delayMillis / 50); // Konwersja na ticki (min 1 tick)

                ItemLottery.getInstance().getLogger().info("Lottery " + data.getQueueId() + " is scheduled for later. Checking again in " + delayTicks + " ticks.");

                // Zaplanuj ponowne sprawdzenie dokładnie wtedy, kiedy loteria ma się zacząć
                scheduledCheckTask = Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::checkAndStartNextLottery, delayTicks);
            }
        } else {
            ItemLottery.getInstance().getLogger().info("Lottery queue is empty. No lottery started.");
        }
    }

     // Ta metoda powinna być wywoływana po zakończeniu/anulowaniu loterii w DrawManager
     // np. w DrawManager.resetLotteryState() LUB po jego wywołaniu
     public static void triggerNextLotteryCheck() {
         // Bezpośrednie wywołanie jest bezpieczniejsze po zakończeniu loterii
         Bukkit.getScheduler().runTask(ItemLottery.getInstance(), LotteryQueue::checkAndStartNextLottery);
     }


// ... inne importy (np. do deserializacji ItemStack)

public static void loadQueueFromFile() {
    File queueFile = new File(ItemLottery.getInstance().getDataFolder(), "lottery_queue.yml");
    if (!queueFile.exists()) {
        ItemLottery.getInstance().getLogger().info("No saved lottery queue found (lottery_queue.yml).");
        return;
    }

    FileConfiguration queueConfig = YamlConfiguration.loadConfiguration(queueFile);
    if (queueConfig.isConfigurationSection("queue")) {
        int loadedCount = 0;
        // Przejdź przez zapisane loterie (zakładając, że są zapisane pod kluczami 0, 1, 2...)
        for (String key : queueConfig.getConfigurationSection("queue").getKeys(false)) {
            String path = "queue." + key + ".";
            try {
                // Wczytaj wszystkie dane z pliku konfiguracyjnego
                String logName = queueConfig.getString(path + "logName");
                String playerName = queueConfig.getString(path + "playerName");
                String timestamp = queueConfig.getString(path + "timestamp");
                int duration = queueConfig.getInt(path + "duration");
                // Deserializacja przedmiotu - upewnij się, że to działa poprawnie
                Map<String, Object> serializedItem = (Map<String, Object>) queueConfig.getConfigurationSection(path + "item").getValues(true);
                int winnersCount = queueConfig.getInt(path + "winnersCount");
                boolean ticketUse = queueConfig.getBoolean(path + "ticketUse");
                double ticketPrice = queueConfig.getDouble(path + "ticketPrice");
                // Wczytaj zaplanowany czas startu (zakładając, że jest zapisany jako long)
                // Użyj wartości domyślnej (np. 0 lub System.currentTimeMillis()), jeśli klucz nie istnieje
                long scheduledStartTimeMillis = queueConfig.getLong(path + "scheduledStartTimeMillis", System.currentTimeMillis()); // Wczytaj czas startu

                // Stwórz obiekt LotteryLogData z dodatkowym argumentem
                LotteryLogData data = new LotteryLogData(logName, playerName, timestamp, duration,
                                                         serializedItem, winnersCount, ticketUse, ticketPrice,
                                                         scheduledStartTimeMillis); // <-- Dodano argument czasu startu

                // Dodaj do kolejki (bez uruchamiania następnej od razu)
                 mainQueue.offer(new LotteryTask(data));
                 loadedCount++;

            } catch (Exception e) {
                ItemLottery.getInstance().getLogger().severe("Failed to load queued lottery entry " + key + ": " + e.getMessage());
                e.printStackTrace(); // Loguj błąd dla debugowania
            }
        }
         ItemLottery.getInstance().getLogger().info("Loaded " + loadedCount + " lotteries from queue file.");
    } else {
         ItemLottery.getInstance().getLogger().info("Queue file exists but contains no 'queue' section.");
    }

     // Po wczytaniu wszystkiego, spróbuj uruchomić pierwszą loterię
     // Ważne: Wywołaj to PO wczytaniu, aby nie uruchamiać loterii w trakcie ładowania
     checkAndStartNextLottery();
}

// Potrzebna byłaby też metoda saveQueueToFile() wywoływana w onDisable
}