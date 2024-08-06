package starify.itemlottery.server.utils.subcommands;

import org.bukkit.entity.Player;
import starify.itemlottery.managers.drawmanager.DrawManager;
import starify.itemlottery.server.utils.handlers.EconomyHandlers;
import starify.itemlottery.server.utils.handlers.TicketHandlers;

/**
 * Class for handling the Buy Ticket Lottery command.
 */
public class ButTicketLottery {
    /**
     * Executes the Buy Ticket Lottery command.
     * Checks the player's balance, removes the ticket price from the player's balance,
     * and checks the player's ticket status.
     *
     * @param player The player executing the command.
     * @param TicketPrice The price of the ticket.
     */
    public void BuyTicketLotteryCommand(Player player) {
        TicketHandlers ticketHandlers = new TicketHandlers();

        // Check the player's ticket status
        ticketHandlers.checkTicket(player);

        EconomyHandlers economyHandlers = new EconomyHandlers();

        // Check if the player has enough balance
        economyHandlers.checkPlayerBalance(player, DrawManager.getTicketPrice());
        // Remove the ticket price from the player's balance
        economyHandlers.removePlayerBalance(player, DrawManager.getTicketPrice());
    }
}