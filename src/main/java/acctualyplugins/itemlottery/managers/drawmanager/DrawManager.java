package acctualyplugins.itemlottery.managers.drawmanager;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.tasks.CountDown; // Upewnij się, że CountDown jest poprawnie zaimplementowany
import acctualyplugins.itemlottery.server.utils.messages.Refactor;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack; // Zmieniono z Map na ItemStack
import org.bukkit.scheduler.BukkitTask;

import java.util.Map; // Zachowane dla sygnatury drawItem, ale wewnętrznie użyjemy ItemStack

/**
 * Manager class for handling the lottery draw process.
 * Allows only one active lottery at a time due to static access methods.
 */
public class DrawManager {

    // Statyczne narzędzia - pozostają
    public static final Refactor REFACTOR = new Refactor();
    private static final Message message = new Message();

    // --- Pola instancji do przechowywania stanu AKTYWNEJ loterii ---
    // Dostęp do nich będzie uzyskiwany przez statyczne metody za pośrednictwem instancji DrawManager
    @Getter(AccessLevel.PRIVATE) // Używamy getterów/setterów instancji
    @Setter(AccessLevel.PRIVATE)
    private BukkitTask activeTask = null;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private int currentRemainingTime = -1; // Czas dla aktywnej loterii

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private BossBar activeBossBar = null;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private ItemStack activeDrawItem = null; // Używamy ItemStack

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private int activeWinnersCount = 0;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private Player activeLotteryMaker = null;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private boolean activeTicketUse = false;

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private double activeTicketPrice = 0.0;

    // Pole instancji pluginu - potrzebne do uzyskania instancji DrawManager
    private final ItemLottery plugin;

    // Konstruktor - wywoływany raz w głównej klasie pluginu
    public DrawManager(ItemLottery plugin) {
        this.plugin = plugin;
    }

    // Metoda pomocnicza do uzyskania instancji DrawManager (zakładając, że jest w ItemLottery)
    private static DrawManager getInstance() {
        return ItemLottery.instance.getDrawManager(); // Musisz dodać metodę getDrawManager() w ItemLottery
    }

    /**
     * Translates alternate color codes. (Pozostaje statyczna)
     */
    public static String cc(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Starts the lottery draw. (Pozostaje statyczna)
     * Initializes the boss bar, sets the remaining time, and schedules the countdown task.
     *
     * @param serialized   The serialized data of the lottery item (konwertowane na ItemStack).
     * @param drawTime     The duration of the draw in seconds.
     * @param winnersCount Number of winners.
     * @param player       The player who initiated the lottery.
     * @param ticketUse    Whether the lottery uses tickets.
     * @param ticketPrice  The price of a ticket (if applicable).
     */
    public static void drawItem(Map<String, Object> serialized, int drawTime, int winnersCount, Player player,
                                boolean ticketUse, double ticketPrice) {

        DrawManager instance = getInstance(); // Pobierz instancję

        // Sprawdzenie, czy inna loteria już nie trwa (używa metody statycznej isRunning)
        if (isRunning()) {
            message.sendMessageComponent(player, "&cAnother lottery is already in progress!");
            return;
        }

        // Konwersja Map<String, Object> na ItemStack (zakładając standardową serializację Bukkit)
        ItemStack drawItem = ItemStack.deserialize(serialized);
        if (drawItem == null) {
            instance.plugin.getLogger().severe("Failed to deserialize item for lottery!");
            message.sendMessageComponent(player, "&cError starting lottery: Could not read item data.");
            return;
        }

        // Tworzenie BossBar
        BossBar bossBar = BossBar.bossBar(REFACTOR.chatRefactor("&aLottery started!", player),
                1, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        // Pokaż bossbar wszystkim graczom
        instance.plugin.adventure().players().forEachAudience(audience -> bossBar.addViewer(audience));

        // Ustaw stan w *instancji* DrawManager
        instance.setActiveBossBar(bossBar);
        instance.setCurrentRemainingTime(drawTime);
        instance.setActiveDrawItem(drawItem);
        instance.setActiveWinnersCount(winnersCount);
        instance.setActiveTicketUse(ticketUse);
        instance.setActiveTicketPrice(ticketPrice);
        instance.setActiveLotteryMaker(player);

        final String currentLotteryId = "active_lottery"; // Stałe ID dla jedynej aktywnej loterii

        CountDown lotteryRunnable = new CountDown(
                instance.plugin,          // 1. ItemLottery plugin
                instance,                 // 2. DrawManager drawManager (przekazujemy instancję)
                currentLotteryId,         // 3. String lotteryId (używamy stałego ID)
                bossBar,                  // 4. BossBar bossBar
                drawTime,                 // 5. long durationSeconds (int jest automatycznie konwertowany na long)
                player,                   // 6. Player lotteryMaker
                winnersCount,             // 7. int winnersCount
                drawItem,                 // 8. org.bukkit.inventory.ItemStack drawItem
                ticketUse                 // 9. boolean ticketUse
        );



        // Uruchom zadanie i zapisz BukkitTask w *instancji*
        BukkitTask scheduledTask = lotteryRunnable.runTaskTimer(instance.plugin, 0L, 20L);
        instance.setActiveTask(scheduledTask);

        // Logika logowania (jeśli potrzebna)
        instance.plugin.getLogger().info("Started lottery initiated by " + player.getName());
    }

    /**
     * Resets the lottery state. (Pozostaje statyczna, operuje na instancji)
     * Called after the lottery ends or is cancelled.
     */
    public static void resetLotteryState() {
        DrawManager instance = getInstance();
        instance.setActiveTask(null); // Czyści referencję do zadania w instancji
        instance.setCurrentRemainingTime(-1); // Resetuje czas w instancji

        BossBar bb = instance.getActiveBossBar(); // Pobierz bossbar z instancji
        if (bb != null) {
            try {
                instance.plugin.adventure().players().forEachAudience(audience -> {
                        bb.removeViewer(audience);
                });
            } catch (Exception e) {
                instance.plugin.getLogger().warning("Error removing BossBar viewers during reset: " + e.getMessage());
            }
            instance.setActiveBossBar(null); // Wyczyść referencję w instancji
        }

        instance.setActiveTicketPrice(0.0);
        instance.setActiveTicketUse(false);
        instance.setActiveWinnersCount(0);
        instance.setActiveDrawItem(null);
        instance.setActiveLotteryMaker(null);

        instance.plugin.getLogger().info("Lottery state has been reset.");
    }


    /**
     * Stops the current lottery if active. (Pozostaje statyczna)
     */
    public static void stopLottery() {
        DrawManager instance = getInstance();
        BukkitTask task = instance.getActiveTask(); // Pobierz zadanie z instancji

        if (task != null && !task.isCancelled()) { // Sprawdź, czy zadanie z instancji jest aktywne
            try {
                task.cancel(); // Anuluj zadanie zapisane w instancji
            } catch (IllegalStateException e) {
                // Ignoruj, jeśli już anulowane
            }
            // Reset stanu (wywołuje statyczną metrodę, która operuje na instancji)
            resetLotteryState();

            // Rozgłaszanie wiadomości - pozostaje bez zmian, używa statycznego 'message'
            Bukkit.getOnlinePlayers().forEach(player -> {
                message.sendMessageComponent(player, String.valueOf(Component.text(cc("&cLottery has been cancelled.")))); // Użyj sendMessageComponent z graczem
            } );
            instance.plugin.getLogger().info("Lottery has been cancelled by command/stop.");

        } else {
            // Można dodać informację, że nie ma aktywnej loterii do zatrzymania
            // message.sendMessage(commandSender, "&cNo lottery is currently running.");
            instance.plugin.getLogger().info("Attempted to stop lottery, but none was active.");
        }
    }


    /**
     * Checks if the lottery draw is currently running. (Pozostaje statyczna)
     * Checks the state in the DrawManager instance.
     *
     * @return True if the draw is running, false otherwise.
     */
    public static boolean isRunning() {
        DrawManager instance = getInstance();
        // Loteria jest aktywna, jeśli zadanie istnieje i nie jest anulowane
        BukkitTask task = instance.getActiveTask();
        return task != null && !task.isCancelled();
        // Alternatywnie można sprawdzać czas: return instance.getCurrentRemainingTime() > 0;
    }

    // --- Gettery statyczne - odczytują stan z instancji DrawManager ---

    /**
     * Gets the remaining time for the active lottery. (Pozostaje statyczny)
     *
     * @return Remaining time in seconds, or -1 if not running.
     */
    // Lombok wygeneruje static getRemainingTime() - to może nie zadziałać jak chcemy
    // Lepiej zrobić to ręcznie:
    public static int getRemainingTime() {
        return getInstance().getCurrentRemainingTime();
    }

    public static void setRemainingTime(int remainingTime) {
        getInstance().setCurrentRemainingTime(remainingTime);
    }

    /**
     * Gets the BossBar for the active lottery. (Pozostaje statyczny)
     *
     * @return The active BossBar, or null if not running.
     */
    public static BossBar getBossBar() {
        return getInstance().getActiveBossBar();
    }

    /**
     * Gets the item being drawn in the active lottery. (Pozostaje statyczny)
     * Zwraca Mapę dla zachowania kompatybilności API, ale wewnętrznie używa ItemStack.
     *
     * @return Serialized map of the item, or null if not running.
     */
    public static Map<String, Object> getDrawItem() {
        ItemStack item = getInstance().getActiveDrawItem();
        return item != null ? item.serialize() : null;
    }

    /**
     * Gets the number of winners for the active lottery. (Pozostaje statyczny)
     *
     * @return Number of winners.
     */
    public static int getWinnersCount() {
        return getInstance().getActiveWinnersCount();
    }

    /**
     * Gets the player who started the active lottery. (Pozostaje statyczny)
     *
     * @return The Player object or null.
     */
    // Lombok wygeneruje static getLotteryMaker()
    public static Player getLotteryMaker() {
        return getInstance().getActiveLotteryMaker();
    }


    // Lombok wygeneruje static isTicketUse()
    public static boolean isTicketUse() {
        return getInstance().isActiveTicketUse();
    }

    // Lombok wygeneruje static getTicketPrice()
    public static double getTicketPrice() {
        return getInstance().getActiveTicketPrice();
    }



    // Enum LotteryType - może pozostać tutaj
    public enum LotteryType {
        FREE, TICKET
    }

}