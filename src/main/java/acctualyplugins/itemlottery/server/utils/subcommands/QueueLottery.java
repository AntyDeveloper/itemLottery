package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.TimeUtils;
import acctualyplugins.itemlottery.server.utils.handlers.PermissionsHandler;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueueLottery implements Listener {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Message message = ItemLottery.getInstance().message;
    private String editLog;

    public void skipLottery(Player player) {
        try {
            PermissionsHandler.hasPermission(player, "lottery.skip", "Permissions");
            LotteryQueue.skip();
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("LotterySkipped", "LotteryCommandArgs"));
        } catch (Exception e) {
            System.out.println(e);
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs"));
        }
    }

    public void openQueueGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Lottery Queue");

        List<ItemStack> items = LotteryQueue.getMainQueue().stream()
                .map(this::lotteryTaskToItem)
                .collect(Collectors.toList());

        for (int i = 0; i < items.size() && i < 54; i++) {
            inventory.setItem(i, items.get(i));
        }

        player.openInventory(inventory);
    }

    private ItemStack lotteryTaskToItem(LotteryTask task) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(task.getLog().getLogName());
            meta.setLore(Arrays.asList(
                    "§9LotteryExecutor: §7" + task.getLog().getPlayerName(),
                    "§9Lottery end: §7" + task.getLog().isLotteryEnd(),
                    "§9Scheduled Time: §7" + task.getLog().getTimestamp()
            ));
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Lottery Queue")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || !clickedItem.hasItemMeta()) {
                return;
            }

            String logName = clickedItem.getItemMeta().getDisplayName();

            if (event.isShiftClick() && event.isRightClick()) {
                // Remove lottery from queue
                LotteryQueue.removeLotteryFromQueue(logName);
                openQueueGui(player); // Refresh GUI
            } else if (event.isLeftClick()) {
                // Edit delay
                player.closeInventory();
                editLog = logName;
                player.sendMessage("Please enter the new delay in minutes for the lottery: " + logName);
            }
        }
    }

    @EventHandler
    public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
            int delay = Integer.parseInt(message);
            LotteryTask task = LotteryQueue.getLotteryByName(editLog);
            task.getLog().setTimestamp(TimeUtils.parseDelayToTimestamp(message));
            player.sendMessage("Lottery " + task.getLog().getLogName() + " has been rescheduled to " + delay + " minutes from now.");
            editLog = null;
    }
}