package acctualyplugins.itemlottery.managers.drawmanager.utils.annoucments;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;

import static acctualyplugins.itemlottery.managers.drawmanager.DrawManager.cc;

/**
 * Utility class for announcing the winners of the lottery draw.
 */
public class WinnersAnnouncement {
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Announces the winners to all online players.
     * Displays a title with the winners' names.
     *
     * @param WinnersNameList A string containing the list of winners' names.
     */
    public static void announceWinners(String WinnersNameList) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            players.sendTitle(cc(getLanguageMessage.getLanguageMessage("Title", "Lottery"))
                    , cc("&9&l" + WinnersNameList + " "), 1, 100, 1);
        }
    }

    /**
     * Announces the winners to all online players with an animation.
     * Removes the boss bar from the players' view and displays a title with the winners' names.
     *
     * @param WinnersNameList A string containing the list of winners' names.
     * @param bossBar The boss bar to be removed from the players' view.
     */
    public static void announceWinnersAnimation(String WinnersNameList, net.kyori.adventure.bossbar.BossBar bossBar) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            Audience audience = ItemLottery.getInstance().adventure().player(players);
            bossBar.removeViewer(audience);
            players.sendTitle(cc(getLanguageMessage.getLanguageMessage("Title", "Lottery"))
                    , cc("&9&l &k" + WinnersNameList + " "), 1, 100, 1);
        }
    }
}