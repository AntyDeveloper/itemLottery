package acctualyplugins.itemlottery.managers.drawmanager.utils.intalization.players;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for retrieving player names.
 */
public class GetNames {

    /**
     * Retrieves the names of the given list of players.
     *
     * @param players The list of players to get the names from.
     * @return A list of player names.
     */
    public static List<String> getNames(List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }
}