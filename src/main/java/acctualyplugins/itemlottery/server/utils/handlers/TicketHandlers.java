package starify.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import starify.itemlottery.managers.languagemanager.GetLanguageMessage;
import starify.itemlottery.server.utils.senders.Message;
import starify.itemlottery.services.ServiceManager;

/**
 * Handler class for managing ticket-related operations in the lottery system.
 */
public class TicketHandlers {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Adds a ticket to the player.
     *
     * @param player The player to whom the ticket is being added.
     * @param price The price of the ticket.
     */
    public void addTicket(Player player) {
        ServiceManager.tickets.add(player);
        // Add ticket to player
    }

    /**
     * Removes a ticket from the player.
     *
     * @param player The player from whom the ticket is being removed.
     */
    public void removeTicket(Player player) {
        ServiceManager.tickets.remove(player);
        // Remove ticket from player
    }

    /**
     * Checks if the ticket system is enabled and if the player can use tickets.
     * If the ticket system is disabled, sends a message to the player.
     *
     * @param player The player whose ticket system status is being checked.
     * @param TicketUse Whether the player is allowed to use tickets.
     * @param ticketSystem Whether the ticket system is enabled.
     * @return True if the ticket system is enabled and the player can use tickets, false otherwise.
     */
    public boolean isTicketSystem(Player player, boolean TicketUse, boolean ticketSystem) {
        if(TicketUse && !ticketSystem) {
            message.sendMessageComponent(player, getLanguageMessage
                    .getLanguageMessage("TicketSystemDisabled",
                            "LotteryCommandArgs"));
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has a ticket.
     * If the player has a ticket, sends a message to the player.
     * If the player does not have a ticket, adds a ticket to the player.
     *
     * @param player The player whose ticket status is being checked.
     * @param price The price of the ticket.
     */
    public void checkTicket(Player player) {
        if(ServiceManager.tickets.contains(player)) {
            // Player has ticket
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("TicketBought",
                    "LotteryCommandArgs"));
        } else {
            addTicket(player);
        }
    }
}