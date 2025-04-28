package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
// Importuj nowe/zaktualizowane klasy
import acctualyplugins.itemlottery.managers.drawmanager.LotteryLogData;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.TimeUtils;
import acctualyplugins.itemlottery.server.utils.handlers.PermissionsHandler;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor; // Import ChatColor
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*; // Import Map i UUID
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class QueueLottery implements Listener {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Message message = new Message();
    // Zmieniamy sposób śledzenia edycji - mapa UUID gracza na UUID loterii
    private final Map<UUID, UUID> editingLottery = new HashMap<>();

    public void skipLottery(Player player) {
        try {
            PermissionsHandler.hasPermission(player, "lottery.skip", "Permissions");
            LotteryQueue.skip(); // Wywołuje zaktualizowaną metodę skip
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("LotterySkipped", "LotteryCommandArgs"));
        } catch (Exception e) { // Złap konkretny wyjątek uprawnień, jeśli PermissionsHandler go rzuca
            ItemLottery.getInstance().getLogger().severe("Error skipping lottery: " + e.getMessage());
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs")); // Lub komunikat o braku uprawnień
        }
    }

    public void openQueueGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Lottery Queue");

        List<ItemStack> items = LotteryQueue.getMainQueue().stream()
                .map(this::lotteryTaskToItem) // Używamy zaktualizowanej metody
                .collect(Collectors.toList());

        for (int i = 0; i < items.size() && i < 54; i++) {
            inventory.setItem(i, items.get(i));
        }

        player.openInventory(inventory);
    }

    // Zaktualizowana metoda do tworzenia ItemStack na podstawie LotteryLogData
    private ItemStack lotteryTaskToItem(LotteryTask task) {
        LotteryLogData data = task.getLogData();
        ItemStack itemStack = new ItemStack(Material.PAPER); // Lub inny materiał
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Lottery: " + ChatColor.AQUA + data.getLogName()); // Użyj logName do wyświetlania
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "Queued By: " + ChatColor.GRAY + data.getPlayerName());
            // ... poprzednie linie lore ...

            String originalTimestamp = data.getTimestamp(); // Pobierz oryginalny timestamp
            String formattedTimestamp = originalTimestamp; // Domyślnie użyj oryginalnego na wypadek błędu

            try {
                // Definiujemy formatter dla formatu WEJŚCIOWEGO (jak jest zapisany timestamp)
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("HH:mm/dd:MM:yy");
                // Definiujemy formatter dla formatu WYJŚCIOWEGO (jak chcemy go wyświetlić)
                // Przykład: "14:35 25.12.2023"
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

                // Parsujemy string wejściowy do obiektu LocalDateTime
                LocalDateTime dateTime = LocalDateTime.parse(originalTimestamp, inputFormatter);
                // Formatujemy obiekt LocalDateTime do nowego stringa
                formattedTimestamp = dateTime.format(outputFormatter);

            } catch (DateTimeParseException e) {
                // W przypadku błędu parsowania (np. zły format w data.getTimestamp()),
                // wyświetlimy oryginalną wartość i ewentualnie zalogujemy błąd.
                ItemLottery.getInstance().getLogger().warning("Could not parse timestamp '" + originalTimestamp + "' for display: " + e.getMessage());
                // formattedTimestamp już zawiera originalTimestamp jako fallback
            } catch (Exception e) {
                // Ogólny catch dla innych nieoczekiwanych błędów
                 ItemLottery.getInstance().getLogger().severe("Unexpected error formatting timestamp '" + originalTimestamp + "': " + e.getMessage());
                 // formattedTimestamp już zawiera originalTimestamp jako fallback
            }

            // Dodajemy sformatowaną (lub oryginalną w razie błędu) datę/czas do lore
            lore.add(ChatColor.BLUE + "Scheduled Start: " + ChatColor.GRAY + formattedTimestamp);

            // ... reszta kodu dodającego lore ...
            lore.add(ChatColor.BLUE + "Duration: " + ChatColor.GRAY + data.getDuration() + "s");
            lore.add(ChatColor.BLUE + "Winners: " + ChatColor.GRAY + data.getWinnersCount());
            if (data.isTicketUse()) {
                lore.add(ChatColor.BLUE + "Type: " + ChatColor.GOLD + "Ticket (" + data.getTicketPrice() + "$)"); // Przykładowa waluta
            } else {
                lore.add(ChatColor.BLUE + "Type: " + ChatColor.GREEN + "Free");
            }
            // Dodaj ukryty identyfikator UUID do lore, aby go odczytać w evencie
            lore.add(""); // Pusta linia dla estetyki
            lore.add(ChatColor.DARK_GRAY + "ID: " + data.getQueueId().toString()); // Ukryty identyfikator

            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    // Zaktualizowany event kliknięcia
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Lottery Queue")) {
            event.setCancelled(true); // Zawsze anuluj kliknięcia w tym GUI

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasLore()) {
                return; // Puste miejsce lub przedmiot bez metadanych/lore
            }

            Player player = (Player) event.getWhoClicked();
            ItemMeta meta = clickedItem.getItemMeta();
            List<String> lore = meta.getLore();

            // Odczytaj UUID z lore (ostatnia linia)
            UUID queueId = null;
            String idPrefix = ChatColor.DARK_GRAY + "ID: ";
            if (lore != null && !lore.isEmpty()) {
                String lastLine = lore.get(lore.size() - 1);
                if (lastLine.startsWith(idPrefix)) {
                    try {
                        queueId = UUID.fromString(lastLine.substring(idPrefix.length()));
                    } catch (IllegalArgumentException e) {
                         ItemLottery.getInstance().getLogger().warning("Could not parse UUID from item lore: " + lastLine);
                         return; // Nie udało się odczytać ID
                    }
                }
            }

            if (queueId == null) {
                 ItemLottery.getInstance().getLogger().warning("Could not find queue ID in item lore.");
                 return; // Brak ID w lore
            }


            if (event.isShiftClick() && event.isRightClick()) {
                // Usuń loterię z kolejki używając UUID
                ItemLottery.getInstance().getLogger().info("Player " + player.getName() + " attempting to remove lottery " + queueId + " from queue.");
                LotteryQueue.removeLotteryFromQueue(queueId);
                openQueueGui(player); // Odśwież GUI
                 message.sendMessageComponent(player, "&aLottery removed from queue."); // Informacja zwrotna
            } else if (event.isLeftClick()) {
                // Edytuj opóźnienie/czas startu
                player.closeInventory();
                // Zapisz, że ten gracz edytuje tę konkretną loterię
                editingLottery.put(player.getUniqueId(), queueId);
                player.sendMessage(ChatColor.YELLOW + "Please enter the new delay in minutes for lottery " + queueId);
                player.sendMessage(ChatColor.GRAY + "(This will set the start time relative to now)");
            }
        }
    }

    // Zaktualizowany event czatu do obsługi edycji
    @EventHandler
    public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Sprawdź, czy ten gracz był w trybie edycji
        if (editingLottery.containsKey(playerId)) {
            event.setCancelled(true); // Anuluj wiadomość na czacie

            UUID queueIdToEdit = editingLottery.get(playerId);
            String messageText = event.getMessage();

            try {
                int delayInMinutes = Integer.parseInt(messageText);
                if (delayInMinutes < 0) {
                    player.sendMessage(ChatColor.RED + "Delay cannot be negative.");
                    editingLottery.remove(playerId); // Zakończ edycję
                    return;
                }

                // Znajdź zadanie w kolejce
                LotteryTask task = LotteryQueue.getLotteryByQueueId(queueIdToEdit);
                if (task != null) {
                    // Oblicz nowy timestamp
                    String newTimestamp = String.valueOf(TimeUtils.parseDelayToTimestamp(messageText)); // Użyj istniejącej logiki, jeśli pasuje
                    // Lub oblicz inaczej:
                    // long newStartTimeMillis = System.currentTimeMillis() + (delayInMinutes * 60 * 1000L);
                    // String newTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(newStartTimeMillis));

                    task.getLogData().setTimestamp(newTimestamp); // Zaktualizuj timestamp w danych
                    player.sendMessage(ChatColor.GREEN + "Lottery " + queueIdToEdit + " has been rescheduled to start in " + delayInMinutes + " minutes.");
                    ItemLottery.getInstance().getLogger().info("Lottery " + queueIdToEdit + " rescheduled by " + player.getName() + " to start at " + newTimestamp);

                } else {
                    player.sendMessage(ChatColor.RED + "Could not find the lottery in the queue (it might have started or been removed).");
                }

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid number format. Please enter the delay in minutes (e.g., 5).");
                 // Pozostaw gracza w trybie edycji, aby mógł spróbować ponownie? Lub zakończ:
                 // editingLottery.remove(playerId);
            } finally {
                // Zakończ tryb edycji dla tego gracza
                 editingLottery.remove(playerId);
            }
        }
        // Jeśli gracz nie był w trybie edycji, nic nie rób (pozwól wiadomości przejść na czat)
    }
}