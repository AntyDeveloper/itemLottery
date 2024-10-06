package acctualyplugins.itemlottery.events;

import acctualyplugins.itemlottery.ItemLottery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import acctualyplugins.itemlottery.managers.updatemanager.UpdateManager;
import acctualyplugins.itemlottery.server.utils.senders.Message;

import java.util.Arrays;
import java.util.List;

/**
 * Listener class for handling player join events.
 */
public class PlayerJoinListener implements Listener {
    private final Message message = ItemLottery.message;

    /**
     * Event handler for when a player joins the server.
     * If the player is an operator, it checks for updates and sends a message if an update is available.
     *
     * @param event The PlayerJoinEvent triggered when a player joins the server.
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(player.isOp()) {
            UpdateManager.checkUpdate();
            if(UpdateManager.opMessageSend) {
                List<String> strings = Arrays.asList(
                        " ",
                        "<center>&7《 &9&lUpdate &7》 ",
                        " ",
                        "<center>&fNew update &l&9ItemLottery &r&bis available!",
                        " "
                );

                for (String string : strings) {
                    message.sendMessageComponent(player, string);
                }
            }
        }
    }
}