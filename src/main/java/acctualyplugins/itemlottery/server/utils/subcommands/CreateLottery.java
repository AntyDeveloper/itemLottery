package acctualyplugins.itemlottery.server.utils.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.managers.logmanager.LogManager;
import acctualyplugins.itemlottery.server.utils.handlers.*;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.server.utils.senders.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.getDisplayName;
import static acctualyplugins.itemlottery.server.utils.handlers.ItemStackHandlers.updateItemStackAmount;

/**
 * Class for handling the Create Lottery command.
 */
public class CreateLottery {
    /**
     * Instance for retrieving language messages.
     */
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Instance for managing the draw of items.
     */
    private final DrawManager drawItem = new DrawManager();

    /**
     * Instance for sending titles to players.
     */
    private final Title title = ItemLottery.getInstance().title;

    /**
     * Executes the Create Lottery command.
     *
     * @param player The player executing the command.
     * @param time The duration of the lottery.
     * @param itemCount The number of items in the lottery.
     * @param winnerCount The number of winners in the lottery.
     * @param ticketUse Whether tickets are used in the lottery.
     * @param ticketPrice The price of the ticket.
     */
    public void createLotteryCommand(Player player, int time, int itemCount, int winnerCount, boolean ticketUse,
                                     double ticketPrice) {
        ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");
        assert settings != null;
        boolean playerUse = settings.getBoolean("playerLotteryUse");
        boolean ticketSystem = settings.getBoolean("ticketSystem");
        try {
            if (!player.hasPermission("lottery.bypass"))
            { CooldownHandlers.checkPlayerCooldown(player); }

            TicketHandlers ticketHandlers = new TicketHandlers();

            if(!ticketHandlers.isTicketSystem(player, ticketUse, ticketSystem)) {
                return;
            };

            if(TaskHandler.isTaskRunning(player)) {
                return;
            };

            if(!NotEnoughPlayers.notEnoughPlayers(winnerCount, player)) {
                return;
            };

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            ItemStack itemStackStatic = itemStack.clone();
            ItemStackHandlers.isItemStackEmpty(itemStack, player);

            PermissionsHandler.playerUse(player, playerUse, itemStack, itemCount);

            BroadcastHandlers.broadcastTime(player, time);

            int newAmount = updateItemStackAmount(itemStack, itemCount);
            String displayName = getDisplayName(itemStack);

            Objects.requireNonNull(itemStack.getItemMeta()).setDisplayName(displayName);
            itemStack.setAmount(itemCount);

            CooldownHandlers.setPlayerCooldown(player);

            Map<String, Object> serialized = itemStackStatic.serialize();
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

            onlinePlayers.forEach(playerSend ->
                    title.showMyTitleWithDurations(playerSend,
                            getLanguageMessage.getLanguageMessage("StartTitle", "Lottery"),
                            getLanguageMessage.getLanguageMessage("StartSubTitle",
                                    "Lottery") + " &7by &f" + playerSend.getName())

            );


            player.getInventory().getItemInMainHand().setAmount(newAmount);
            drawItem.drawItem(serialized, time, winnerCount, player, ticketUse, ticketPrice);
            LogManager.createNewLog(itemStackStatic, player, winnerCount);
            CooldownHandlers.setPlayerCooldown(player);
            BroadcastHandlers.broadcastStartMessage(onlinePlayers, displayName, ticketUse);

        } catch (Exception e) {
            ItemLottery.getInstance().getLogger().info(e.toString());
        }
    }
}