package acctualyplugins.itemlottery.server.utils.subcommands;

// Usunięto nieużywane importy: Title, Bukkit, ConfigurationSection, Objects
import acctualyplugins.itemlottery.managers.drawmanager.LotteryLogData;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.ItemLottery;
// Usunięto DrawManager - nie jest bezpośrednio potrzebny tutaj
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.managers.logmanager.LogManager; // Potrzebny do zapisu i generowania nazwy
import acctualyplugins.itemlottery.server.utils.handlers.*; // Import zbiorczy dla uproszczenia

import java.util.Map;

// Usunięto statyczne importy - lepiej używać pełnych nazw для czytelności
// import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.getDisplayName;
// import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.updateItemStackAmount;


public class CreateLottery {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    // Usunięto pole Title - nie jest używane
    private final ItemLottery plugin = ItemLottery.getInstance(); // Przechowaj instancję dla łatwiejszego dostępu
    private final Message messageSender = new Message(); // Użyj instancji Message z pluginu


    /**
     * Tworzy nową loterię i dodaje ją do kolejki.
     *
     * @param player Gracz tworzący loterię.
     * @param time Czas trwania loterii w sekundach.
     * @param itemCount Liczba przedmiotów w loterii.
     * @param winnerCount Liczba zwycięzców.
     * @param ticketUse Czy loteria używa biletów.
     * @param ticketPrice Cena biletu (jeśli ticketUse jest true).
     */
    public void createLottery(Player player, int delay, int time, int itemCount, int winnerCount, boolean ticketUse, double ticketPrice) {
        boolean ticketSystemEnabled = plugin.getConfig().getBoolean("settings.ticketSystem", false); // Domyślnie false

        try {
            // --- Walidacja wstępna ---
            PermissionsHandler.hasPermission(player, "lottery.create", "Permissions"); // Główne uprawnienie

            // Sprawdzenie cooldownu (jeśli gracz nie ma bypassu)
            if (!player.hasPermission("lottery.bypass.cooldown")) {
                CooldownHandlers.checkPlayerCooldown(player); // Zakładamy, że rzuca CooldownException
            }

            // Sprawdzenie systemu biletów
            if (ticketUse && !ticketSystemEnabled) {
                messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("TicketSystemDisabled", "ErrorMessages"));
                return;
            }
            // Dodatkowa walidacja ceny biletu, jeśli jest używany
            if (ticketUse && ticketPrice <= 0) {
                 messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("InvalidTicketPrice", "ErrorMessages"));
                 return;
            }

            // Sprawdzenie przedmiotu w ręce
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemStackHandlers.isItemStackEmpty(itemInHand, player); // Zakładamy, że rzuca EmptyItemException
            if (itemInHand.getAmount() < itemCount) {
                 messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("NotEnoughItems", "ErrorMessages"));
                 return;
            }


            // Sprawdzenie czasu trwania i liczby zwycięzców
            BroadcastHandlers.broadcastTime(player, time); // Zakładamy, że waliduje czas i wysyła wiadomość w razie błędu
            if (winnerCount < 1) {
                messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("InvalidWinnerCount", "ErrorMessages"));
                return;
            }

             // --- Przygotowanie danych loterii ---

             // Klonowanie przedmiotu *przed* modyfikacją w ekwipunku
             ItemStack itemToLottery = itemInHand.clone();
             itemToLottery.setAmount(itemCount); // Ustawienie właściwej ilości dla loterii

             // Serializacja przedmiotu
             Map<String, Object> serializedItem = itemToLottery.serialize();

             // Wygenerowanie unikalnej nazwy logu i pobranie czasu utworzenia
             long creationTimestamp = System.currentTimeMillis();
             String logName = LogManager.generateLogName(player, creationTimestamp);

             // Pobierz lub oblicz opóźnienie startu (np. z komendy gracza)
            // Zakładając, że 'delay' przechowuje sekundy

            // Oblicz docelowy czas startu w milisekundach
            long delayMillis = delay * 1000L; // Przelicz sekundy na milisekundy
            long startTimeMillis = System.currentTimeMillis() + delayMillis; // Oblicz docelowy czas startu

            // Teraz możesz użyć startTimeMillis w konstruktorze
             LotteryLogData logData = new LotteryLogData(
                 logName,
                 player.getName(), // Zapisz nazwę gracza
                 String.valueOf(creationTimestamp), // Czas utworzenia jako string
                 time,             // Czas trwania
                 serializedItem,   // Zserializowany przedmiot
                 winnerCount,      // Liczba zwycięzców
                 ticketUse,        // Czy używa biletów
                 ticketPrice,      // Cena biletu
                 startTimeMillis   // <-- Użyj obliczonego czasu startu
             );

             // Dalsza część kodu, np. dodanie logData do kolejki
             // LotteryQueue.addLotteryToQueue(logData);

             // Stworzenie zadania loterii
             LotteryTask lotteryTask = new LotteryTask(logData);

             // Dodanie zadania do kolejki
             LotteryQueue.addLotteryToQueue(lotteryTask.getLogData()); // Ta metoda zajmie się uruchomieniem, jeśli trzeba

             // --- Czynności końcowe po sukcesie ---

             // Aktualizacja ekwipunku gracza
             int newAmount = itemInHand.getAmount() - itemCount;
             if (newAmount <= 0) {
                 player.getInventory().setItemInMainHand(null); // Usuń przedmiot
             } else {
                 itemInHand.setAmount(newAmount); // Zmniejsz ilość
             }
             // Nie ma potrzeby player.updateInventory() - Bukkit zarządza tym automatycznie przy modyfikacji przez API

             // Ustawienie cooldownu (jeśli gracz nie ma bypassu)
             if (!player.hasPermission("lottery.bypass.cooldown")) {
                 CooldownHandlers.setPlayerCooldown(player);
             }

             // Wiadomość potwierdzająca dla gracza
             messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("LotteryCreated", "SuccessMessages")); // Dodaj np. nazwę logu



        } catch (Exception e) { // Ogólny błąd
            plugin.getLogger().severe("Unexpected error creating lottery for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            messageSender.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs")); // Użyj bardziej ogólnego klucza błędu
        }
    }

    // Usunięto metodę createLotteryWithDelay
}