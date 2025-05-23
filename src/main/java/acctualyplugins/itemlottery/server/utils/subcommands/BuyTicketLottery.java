package acctualyplugins.itemlottery.server.utils.subcommands;

import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager;
import acctualyplugins.itemlottery.server.utils.handlers.EconomyHandlers;
import acctualyplugins.itemlottery.server.utils.handlers.TicketHandlers;

/**
 * Class for handling the Buy Ticket Lottery command.
 */
public class BuyTicketLottery {
    /**
     * Executes the Buy Ticket Lottery command.
     * Checks the player's balance, removes the ticket price from the player's balance,
     * and checks the player's ticket status.
     *
     * @param player The player executing the command.
     */
    public void BuyTicketLotteryCommand(Player player) {
        TicketHandlers ticketHandlers = new TicketHandlers();

        EconomyHandlers economyHandlers = new EconomyHandlers();

        // Check if the player has enough balance
        economyHandlers.checkPlayerBalance(player, DrawManager.getTicketPrice());


        // Remove the ticket price from the player's balance
        economyHandlers.removePlayerBalance(player, DrawManager.getTicketPrice());

        // Check the player's ticket status
        ticketHandlers.checkTicket(player);
    }
}