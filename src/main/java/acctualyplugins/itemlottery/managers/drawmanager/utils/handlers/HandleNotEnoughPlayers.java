package acctualyplugins.itemlottery.managers.drawmanager.utils.handlers;

import acctualyplugins.itemlottery.ItemLottery;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import acctualyplugins.itemlottery.managers.drawmanager.utils.bossbar.RemoveBossbar;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;

import java.util.Map;

import static acctualyplugins.itemlottery.managers.drawmanager.DrawManager.cc;

/**
 * Utility class for handling the scenario when there are not enough players for the lottery draw.
 */
public class HandleNotEnoughPlayers {

    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Handles the situation when there are not enough players online for the lottery draw.
     * Hides the boss bar from all viewers, broadcasts a message to all players, returns the lottery item to the player's inventory, and cancels the task.
     *
     * @param serialized   The serialized data of the lottery item.
     * @param lotteryMaker The player who initiated the lottery.
     * @param bossBar      The boss bar to be hidden from the players' view.
     * @param task         The task to be canceled.
     */
    public static boolean handleNotEnoughPlayers(Map<String, Object> serialized, Player lotteryMaker, BossBar bossBar,
                                              BukkitTask task) {
        if(!Bukkit.getOnlinePlayers().isEmpty()) {
            new RemoveBossbar().removeBossbar(bossBar);
            Bukkit.broadcastMessage(cc(getLanguageMessage.getLanguageMessage("NotEnoughOnlinePlayers"
                    , "Lottery")));
            ItemStack deserializedItem = ItemStack.deserialize(serialized);
            lotteryMaker.getInventory().addItem(deserializedItem);

            task.cancel();
            ItemLottery.getInstance().adventure().players().clearTitle();
            return false;
        }
        return true;
    }
}