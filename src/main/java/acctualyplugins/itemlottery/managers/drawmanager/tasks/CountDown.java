package acctualyplugins.itemlottery.managers.drawmanager.tasks;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.handlers.DrawHandlers;
import acctualyplugins.itemlottery.server.utils.messages.Refactor;

import java.util.Map;

import static acctualyplugins.itemlottery.managers.drawmanager.DrawManager.*;

/**
 * Class responsible for managing the countdown task for the lottery draw.
 */
public class CountDown {

    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Refactor refactor = new Refactor();

    private int messageCounter = 0;

    /**
     * Executes the countdown task for the lottery draw.
     * Updates the boss bar with the remaining time and handles ticket purchase messages.
     * When the countdown reaches zero, it triggers the draw handlers.
     *
     * @param serialized   The serialized data for the draw.
     * @param WinnersCount The number of winners to be selected.
     * @param player       The player who initiated the draw.
     * @param TicketUse    Whether tickets are used in the draw.
     * @param TicketPrice  The price of a ticket.
     */
    public void countdownTask(Map<String, Object> serialized, int WinnersCount, Player player,
                              boolean TicketUse, double TicketPrice) {
        int test = (int) (remainingTime - bossBar.progress() * remainingTime);
        float progress = (float) test / remainingTime;
        int messageInterval = 15;

        if (remainingTime > 0) {
            for (Player playerSendBar : Bukkit.getOnlinePlayers()) {
                String BosBarTitle;

                BosBarTitle = getLanguageMessage.getLanguageMessage("BosBarTitle",
                        "Lottery");

                if (remainingTime >= 3600) {
                    int hours = remainingTime / 3600;
                    int minutes = (remainingTime % 3600) / 60;
                    int seconds = remainingTime % 60;
                    BosBarTitle = BosBarTitle.replace("%time%", hours + "h " + minutes + "m " + seconds + "s");
                } else if (remainingTime >= 60) {
                    int minutes = remainingTime / 60;
                    int seconds = remainingTime % 60;
                    BosBarTitle = BosBarTitle.replace("%time%", minutes + "m " + seconds + "s");
                } else {
                    BosBarTitle = BosBarTitle.replace("%time%", remainingTime + "s");
                }
                
                if (TicketUse && messageCounter >= messageInterval) {
                    BosBarTitle = "&9&lBuy a ticket for &f&l" + TicketPrice + "$ &8/&9lottery buy";
                    if(messageCounter > messageInterval + 3) {
                        messageCounter = 0;
                    }
                }

                Audience audience = ItemLottery.getInstance().adventure().player(playerSendBar);
                bossBar.name(refactor.ChatRefactor(BosBarTitle, playerSendBar));
                bossBar.addViewer(audience);
                bossBar.progress(progress);
            }
            remainingTime--;
            messageCounter++;
        } else {
            DrawHandlers.selectDrawManager(serialized, bossBar, WinnersCount, player, TicketUse);
        }
    }
}