package acctualyplugins.itemlottery.managers.logmanager;

import acctualyplugins.itemlottery.ItemLottery;
// Poprawiony import - upewnij się, że LotteryLogData jest w odpowiednim pakiecie
import acctualyplugins.itemlottery.managers.drawmanager.LotteryLogData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File logDirectory = new File(ItemLottery.getInstance().getDataFolder(), "logs");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     * Przechowuje referencję do danych ostatnio zapisanego logu.
     * UWAGA: Może zostać nadpisane, jeśli nowa loteria zostanie utworzona
     * zanim poprzednia operacja używająca tego pola się zakończy.
     * Rozważ przekazywanie LotteryLogData bezpośrednio zamiast polegania na tym polu.
     */
    public static LotteryLogData lastLog = null; // Dodane pole lastLog

    static {
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }
    }

    public static String generateLogName(Player player, long timestamp) {
        String playerName = player.getName();
        String formattedDate = dateFormat.format(new Date(timestamp));
        playerName = playerName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return playerName + "_" + formattedDate;
    }

    /**
     * Zapisuje dane logu loterii do pliku JSON i aktualizuje pole lastLog.
     * Nazwa pliku jest pobierana z obiektu LotteryLogData.
     *
     * @param logData Obiekt zawierający dane logu do zapisania.
     */
    public static void saveLogData(LotteryLogData logData) {
        if (logData == null || logData.getLogName() == null || logData.getLogName().isEmpty()) {
            ItemLottery.getInstance().getLogger().warning("Attempted to save log data with missing log name.");
            return;
        }
        File logFile = new File(logDirectory, logData.getLogName() + ".json");
        try (FileWriter writer = new FileWriter(logFile)) {
            gson.toJson(logData, writer);
            lastLog = logData; // Aktualizuj lastLog po pomyślnym zapisie
        } catch (IOException e) {
            ItemLottery.getInstance().getLogger().severe("Could not save lottery log file: " + logFile.getName() + " - " + e.getMessage());
            e.printStackTrace();
            // Rozważ, czy lastLog powinno być ustawione na null w przypadku błędu zapisu
            // lastLog = null;
        }
    }

    public static LotteryLogData loadLogData(String logName) {
        if (logName == null || logName.isEmpty()) {
            return null;
        }
        File logFile = new File(logDirectory, logName + ".json");
        if (!logFile.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(logFile)) {
            return gson.fromJson(reader, LotteryLogData.class);
        } catch (IOException e) {
            ItemLottery.getInstance().getLogger().severe("Could not load lottery log file: " + logFile.getName() + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}