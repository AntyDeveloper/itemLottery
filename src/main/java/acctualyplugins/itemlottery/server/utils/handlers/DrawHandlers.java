package starify.itemlottery.server.utils.handlers;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import starify.itemlottery.managers.drawmanager.utils.tasks.TaskManager;
import starify.itemlottery.managers.drawmanager.utils.tasks.drawtasks.Draw;
import starify.itemlottery.managers.drawmanager.utils.tasks.drawtasks.DrawWithTickets;

import java.util.Map;

/**
 * Handler class for managing the draw process in the lottery system.
 */
public class DrawHandlers {

    /**
     * Selects the appropriate draw manager based on whether tickets are used.
     *
     * @param serialized The serialized data for the draw.
     * @param bossBar The boss bar to display during the draw.
     * @param winnersCount The number of winners to select.
     * @param player The player initiating the draw.
     * @param ticketUse Whether tickets are used in the draw.
     */
    public static void selectDrawManager(Map<String, Object> serialized, BossBar bossBar, int winnersCount, Player player,
                                         boolean ticketUse) {
        if(ticketUse) {
            DrawWithTickets.drawWithTickets(serialized, winnersCount, player, bossBar);
        } else {
            Draw.draw(serialized, winnersCount, player, bossBar);
        }
    }
}