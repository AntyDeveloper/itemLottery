package acctualyplugins.itemlottery.managers.drawmanager.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.services.ServiceManager;

import java.util.ArrayList;
import java.util.List;

import static acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.TaskManager.selectedPlayers;


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
    public static List<Player> selectWinners(int WinnersCount) {
        selectedPlayers.clear();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < WinnersCount && !onlinePlayers.isEmpty(); i++) {
            int randomIndex = (int) (Math.random() * onlinePlayers.size());
            Player selectedPlayer = onlinePlayers.get(randomIndex);

            if (!selectedPlayers.contains(selectedPlayer)) {
                selectedPlayers.add(selectedPlayer);
            }
        }
        return selectedPlayers;
    }

    /**
     * Selects winners from the players with tickets.
     * Randomly selects players from the ticket holders until the number of selected players matches the specified winners count.
     *
     * @param WinnersCount The number of winners to be selected.
     */
    public static List<Player> selectWinnersWithTicket(int WinnersCount) {
        selectedPlayers.clear();
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (int i = 0; i < WinnersCount && !onlinePlayers.isEmpty(); i++) {
            int randomIndex = (int) (Math.random() * onlinePlayers.size());
            Player selectedPlayer = onlinePlayers.get(randomIndex);

            if (!selectedPlayers.contains(selectedPlayer)) {
                selectedPlayers.add(selectedPlayer);
            }
        }
        return selectedPlayers;
    }

}