// CreateLottery.java
package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.senders.Title;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.server.utils.handlers.*;

import java.util.Map;
import java.util.Objects;

import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.getDisplayName;
import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.updateItemStackAmount;

public class CreateLottery {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Title title = ItemLottery.getInstance().title;

    public void createLotteryCommand(Player player, int time, int itemCount, int winnerCount, boolean ticketUse,
                                     double ticketPrice, long timestamp) {
        ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");
        assert settings != null;
        boolean playerUse = settings.getBoolean("playerLotteryUse");
        boolean ticketSystem = settings.getBoolean("ticketSystem");

        try {
            if (!player.hasPermission("lottery.bypass")) {
                CooldownHandlers.checkPlayerCooldown(player);
            }

            TicketHandlers ticketHandlers = new TicketHandlers();

            if (!ticketHandlers.isTicketSystem(player, ticketUse, ticketSystem)) return;

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemStack itemStackStatic = itemStack.clone();

            if (!NotEnoughPlayers.notEnoughPlayers(winnerCount, player, itemStack.getAmount())) {
                return;
            }

            ItemStackHandlers.isItemStackEmpty(itemStack, player);

            PermissionsHandler.playerUse(player, playerUse, itemStack, itemCount);

            BroadcastHandlers.broadcastTime(player, time);

            int newAmount = updateItemStackAmount(itemStack, itemCount);
            String displayName = getDisplayName(itemStack);

            Objects.requireNonNull(itemStack.getItemMeta()).setDisplayName(displayName);
            itemStack.setAmount(itemCount);

            CooldownHandlers.setPlayerCooldown(player);

            Map<String, Object> serialized = itemStackStatic.serialize();
            Log log = LogManager.createNewLog(itemStackStatic, player, winnerCount, time, 0, ticketUse, ticketPrice, timestamp);
            LotteryTask lotteryTask = new LotteryTask(serialized, time, winnerCount, player, ticketUse, ticketPrice, () -> {
                // Define what should happen when the lottery is complete
            }, log);

            player.getInventory().getItemInMainHand().setAmount(newAmount);
            LotteryQueue.addLotteryToQueue(lotteryTask);
            CooldownHandlers.setPlayerCooldown(player);

        } catch (Exception e) {
            ItemLottery.getInstance().getLogger().info(e.toString());
        }
    }

    public void createLotteryWithDelay(Player player, int delayInSeconds, int time, int itemCount, int winnerCount, boolean ticketUse, double ticketPrice) {
        long timestamp = System.currentTimeMillis() + delayInSeconds * 1000L;
        createLotteryCommand(player, time, itemCount, winnerCount, ticketUse, ticketPrice, timestamp);
    }

}