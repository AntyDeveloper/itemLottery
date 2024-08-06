package starify.itemlottery.managers.drawmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import starify.itemlottery.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;

import static starify.itemlottery.managers.drawmanager.utils.tasks.TaskManager.selectedPlayers;

/**
 * Utility class for selecting winners for the lottery draw.
 */
public class SelectWinners {

    /**
     * Selects winners from the online players.
     * Randomly selects players until the number of selected players matches the specified winners count.
     *
     * @param WinnersCount The number of winners to be selected.
     */
    public static void selectWinners(int WinnersCount) {
        while (Bukkit.getOnlinePlayers().size() > WinnersCount) {
            int randomIndex = (int) (Math.random() * Bukkit.getOnlinePlayers().size());
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            Player selectedPlayer = onlinePlayers.get(randomIndex);

            if (!selectedPlayers.contains(selectedPlayer)) {
                selectedPlayers.add(selectedPlayer);
            }
        }
    }

    /**
     * Selects winners from the players with tickets.
     * Randomly selects players from the ticket holders until the number of selected players matches the specified winners count.
     *
     * @param WinnersCount The number of winners to be selected.
     */
    public static void selectWinnersWithTickers(int WinnersCount) {
        while (Bukkit.getOnlinePlayers().size() > WinnersCount) {
            int randomIndex = (int) (Math.random() * Bukkit.getOnlinePlayers().size());
            List<Player> onlinePlayers = new ArrayList<>(ServiceManager.tickets);
            Player selectedPlayer = onlinePlayers.get(randomIndex);

            if (!selectedPlayers.contains(selectedPlayer)) {
                selectedPlayers.add(selectedPlayer);
            }
        }
    }
}