package acctualyplugins.itemlottery.managers.guimanager;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.guimanager.guiitems.BackItem;
import acctualyplugins.itemlottery.managers.guimanager.guiitems.ForwardItem;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.AnvilWindow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LogGui {

    private List<Item> items =
            convertLogsToItems(filterLogs(ItemLottery.getInstance().getLogList(), ""));

    public static Item logToItem(Log log) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(log.getLogName());
            meta.setLore(Arrays.asList(
                    "§9LotteryExecutor: §7" + log.getPlayerName(),
                    "§9Winner: §7" + log.getWinners(),
                    "§9Lottery end: §7" + log.isLotteryEnd(),
                    "§7[§aClick§7] §9To summon winning item!"
            ));
            itemStack.setItemMeta(meta);
        }
        return new SimpleItem(new ItemBuilder(itemStack));
    }

    Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName(""));

    public List<Log> filterLogs(List<Log> logs, String search) {
        return logs.stream()
                .filter(log -> log.matchesFilter(search))
                .collect(Collectors.toList());
    }

    public List<Item> convertLogsToItems(List<Log> logs) {
        return logs.stream()
                .map(LogGui::logToItem)
                .collect(Collectors.toList());
    }

    // create the gui
    Gui gui = PagedGui.items()
            .setStructure(
                    "# # # # # # # # #",
                    "# x x x x x x x #",
                    "# x x x x x x x #",
                    "# # # < # > # # #")
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
            .addIngredient('#', border)
            .addIngredient('<', new BackItem())
            .addIngredient('>', new ForwardItem())
            .setContent(items)
            .build();

    Gui anvilGui = Gui.normal()
            .setStructure(
                    "# #")
            .addIngredient('#', new SimpleItem(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                    .setDisplayName("You can search logs by name, lottery executor, winner.")))
            .build();

        public void openGuis(Player player) {

            AnvilWindow window = AnvilWindow.split()
                    .setViewer(player)
                    .setUpperGui(anvilGui)
                    .setLowerGui(gui)
                    .addRenameHandler(s -> {
                        items = convertLogsToItems(filterLogs(ItemLottery.getInstance().getLogList(), s));
                        gui = PagedGui.items()
                                .setStructure(
                                        "# # # # # # # # #",
                                        "# x x x x x x x #",
                                        "# x x x x x x x #",
                                        "# # # < # > # # #")
                                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
                                .addIngredient('#', border)
                                .addIngredient('<', new BackItem())
                                .addIngredient('>', new ForwardItem())
                                .setContent(items)
                                .build();
                        System.out.println(s);
                    })
                    .setTitle("Logs")
                    .build();

            window.open();
        }
}