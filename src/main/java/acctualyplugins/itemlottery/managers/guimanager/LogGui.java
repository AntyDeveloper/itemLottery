package acctualyplugins.itemlottery.managers.guimanager;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.guimanager.guiitems.BackItem;
import acctualyplugins.itemlottery.managers.guimanager.guiitems.ForwardItem;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import org.bukkit.ChatColor; // Import ChatColor
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
// import xyz.xenondevs.invui.inventory.Inventory; // Nie używane bezpośrednio
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.*;
import java.util.stream.Collectors;

public class LogGui {

    // Nie inicjalizujemy tutaj, zrobimy to w openGuis
    // private List<Item> items;

    // Przeniesienie metody statycznej wyżej dla lepszej organizacji
    public static Item logToItem(Log log) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + log.getLogName()); // Użycie ChatColor
            // Użycie ArrayList dla łatwiejszego dodawania linii
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "Lottery Executor: " + ChatColor.GRAY + log.getPlayerName());
            // Sprawdzenie czy lista zwycięzców nie jest null lub pusta
            if (log.getWinners() != null && !log.getWinners().isEmpty()) {
                lore.add(ChatColor.BLUE + "Winner(s): " + ChatColor.GRAY + String.join(", ", log.getWinners()));
            } else {
                lore.add(ChatColor.BLUE + "Winner(s): " + ChatColor.GRAY + "None");
            }
            lore.add(ChatColor.BLUE + "Lottery Ended: " + (log.isLotteryEnd() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
            // TODO: Dodać obsługę kliknięcia, jeśli potrzebna (patrz komentarz w SimpleItem poniżej)
            lore.add(""); // Pusta linia dla odstępu
            lore.add(ChatColor.GRAY + "[" + ChatColor.GREEN + "Click" + ChatColor.GRAY + "] " + ChatColor.AQUA + "To summon winning item!"); // Zmieniono kolor
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        // Dodaj ClickHandler jeśli kliknięcie ma coś robić:
        // return new SimpleItem(new ItemBuilder(itemStack), click -> { /* ... kod akcji ... */ });
        // Jeśli nie ma akcji, zostawiamy SimpleItem bez handlera:
        return new SimpleItem(new ItemBuilder(itemStack), click -> {
            // click.getEvent().setCancelled(true); // Dobrze jest anulować event, aby gracz nie podniósł papierka
            Player player = click.getPlayer();
            ItemStack winningItem = null; // Inicjujemy jako null

            // 1. Pobierz zserializowane dane z logu
            Object serializedData = log.getItemStack(); // Zakładamy, że ta metoda zwraca Map<String, Object>

            // 2. Sprawdź, czy dane są typu Map
            if (serializedData instanceof Map) {
                try {
                    // 3. Rzutuj na Map<String, Object> (z tłumieniem ostrzeżenia, jeśli konieczne)
                    @SuppressWarnings("unchecked")
                    Map<String, Object> itemMap = (Map<String, Object>) serializedData;

                    // 4. Zdeserializuj mapę do obiektu ItemStack
                    winningItem = ItemStack.deserialize(itemMap);

                } catch (Exception e) { // Obsługa błędów deserializacji
                    player.sendMessage(ChatColor.RED + "Error deserializing the item for this log entry.");
                    // Logowanie błędu po stronie serwera jest zalecane
                    ItemLottery.getInstance().getLogger().warning("Failed to deserialize item map from log '" + log.getLogName() + "': " + e.getMessage());
                    // e.printStackTrace(); // Opcjonalnie dla debugowania
                }
            } else if (serializedData != null) {
                // Obsługa sytuacji, gdy dane nie są mapą
                ItemLottery.getInstance().getLogger().warning("Unexpected data type for serialized item in log '" + log.getLogName() + "': Expected Map, got " + serializedData.getClass().getName());
                player.sendMessage(ChatColor.RED + "Unexpected error retrieving item data.");
            }

            // 5. Jeśli deserializacja się powiodła i przedmiot jest prawidłowy, daj go graczowi
            if (winningItem != null && winningItem.getType() != Material.AIR) {
                player.getInventory().addItem(winningItem.clone()); // Używamy clone()
                player.sendMessage(ChatColor.GREEN + "You received the item: " + winningItem.getType());
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
            } else if (serializedData != null && winningItem == null) {
                // Błąd deserializacji został już zakomunikowany graczowi w bloku catch
            } else {
                // Dane były puste lub nieprawidłowe od początku
                player.sendMessage(ChatColor.RED + "Could not retrieve the item for this log entry (no data found or invalid).");
            }
        });

    }

    // Definicja obramowania
    private final Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(" ")); // Usunięto domyślny displayname

    // Metody filtrowania i konwersji (bez zmian logiki)
    public List<Log> filterLogs(List<Log> logs, String search) {
        // Dodane sprawdzenie null dla listy wejściowej dla większej spójności
        if (logs == null) {
            return Collections.emptyList();
        }
        if (search == null || search.trim().isEmpty()) {
            return logs; // Zwróć wszystkie, jeśli wyszukiwanie jest puste
        }
        String lowerCaseSearch = search.toLowerCase(); // Optymalizacja - konwersja raz
        return logs.stream()
                // Upewnij się, że metoda matchesFilter w klasie Log robi to, czego oczekujesz
                .filter(log -> log != null && log.matchesFilter(lowerCaseSearch)) // Dodano sprawdzenie log != null dla pewności
                .collect(Collectors.toList());
    }

    // Metoda convertLogsToItems pozostaje bez zmian, jest już poprawna
    public List<Item> convertLogsToItems(List<Log> logs) {
        if (logs == null) {
            return Collections.emptyList(); // Zabezpieczenie przed null
        }
        return logs.stream()
                .filter(Objects::nonNull) // Dodatkowe zabezpieczenie przed nullami w liście
                .map(LogGui::logToItem) // Zakładając, że logToItem jest statyczna lub dostępna w tej klasie
                .collect(Collectors.toList());
    }


    public void openGuis(Player player) {

        List<Log> allLogs = ItemLottery.getInstance().getLogList();
        if (allLogs == null) {
            allLogs = Collections.emptyList();
            player.sendMessage(ChatColor.RED + "Error loading lottery logs.");
            return;
        }

        List<Item> currentItems = convertLogsToItems(allLogs);

        // Zbuduj GUI strony
        // Używamy final, aby mieć pewność, że lambda odnosi się do tej konkretnej instancji
        final PagedGui<@NotNull Item> pagedGui = PagedGui.items()
                .setStructure(
                        // Zmieniony wzór dla GUI 6x9 (54 sloty)
                        // Maksymalizuje liczbę miejsc 'x'
                        "x x x x x x x x x", // Rząd 1: 9 miejsc na przedmioty
                        "x x x x x x x x x", // Rząd 2: 9 miejsc na przedmioty
                        "x x x x x x x x x", // Rząd 3: 9 miejsc na przedmioty
                        "x x x x x x x x x", // Rząd 4: 9 miejsc na przedmioty
                        "x x x x x x x x x", // Rząd 5: 9 miejsc na przedmioty
                        "# # # < # > # # #"  // Rząd 6: Przyciski nawigacji otoczone ramką
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('#', border)
                .addIngredient('<', new BackItem())
                .addIngredient('>', new ForwardItem())
                .setContent(currentItems) // Ustaw początkową zawartość
                .build();


        Gui anvilGui = Gui.normal()
                .setStructure(
                        "i i")
                .addIngredient('i', new SimpleItem(new ItemBuilder(Material.PAPER)
                        .setDisplayName(ChatColor.AQUA + "Search Logs") // DisplayName często nadal akceptuje String
                        .addLoreLines(// Konwertujemy String z kodami § na Component
                                ChatColor.GRAY + "Type name, executor, or winner below.")

                         // Koniec List.of() i setLore()
                )) // Koniec new SimpleItem()
                .build();


        // Zbuduj okno AnvilWindow
        List<Log> finalAllLogs = allLogs;
        AnvilWindow window = AnvilWindow.split()
                .setViewer(player)
                 .setUpperGui(anvilGui) // Usunięto tę linię
                .setLowerGui(pagedGui)   // Ustaw tylko dolne GUI
                .addRenameHandler(searchString -> { // Handler zmiany nazwy pozostaje
                    List<Log> filteredLogs = filterLogs(finalAllLogs, searchString);
                    List<Item> newItems = convertLogsToItems(filteredLogs);
                    pagedGui.setContent(newItems); // Aktualizuj zawartość istniejącego pagedGui
                })
                .setTitle("Lottery Logs") // Zachowaj tytuł
                .build();


        window.open();
    }
}