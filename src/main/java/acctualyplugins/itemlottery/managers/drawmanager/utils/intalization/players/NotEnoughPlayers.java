package starify.itemlottery.managers.drawmanager.utils.intalization.players;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.managers.languagemanager.GetLanguageMessage;
import starify.itemlottery.server.utils.senders.Message;

import java.util.List;

/**
 * Utility class for checking if there are not enough players for the lottery draw.
 */
public class NotEnoughPlayers {

    /**
     * Language message retriever instance.
     */
    private final static GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Message sender instance.
     */
    private final static Message message = new Message();

    /**
     * Checks if there are not enough players online for the lottery draw.
     *
     * @param onlinePlayers The list of online players.
     * @param WinnersCount The number of winners to be selected.
     * @return True if there are not enough players, false otherwise.
     */
    public static boolean notEnoughPlayers(List<Player> onlinePlayers, int WinnersCount) {
        return onlinePlayers.isEmpty() || WinnersCount > onlinePlayers.size();
    }

    public static boolean notEnoughPlayersBuyTicket(List<Player> players, Player player, BossBar bossBar) {
        if(players.isEmpty()) {

            for (Player playersRemoveBossBar : Bukkit.getOnlinePlayers()) {
                Audience audience = ItemLottery.getInstance().adventure().player(playersRemoveBossBar);
                bossBar.removeViewer(audience);
            }

            message.sendMessageComponent(player,
                    getLanguageMessage.getLanguageMessage("NotEnoughOnlinePlayers",
                            "LotteryCommandArgs"));
            return false;
        }
        return true;
    }

}