package starify.itemlottery.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.managers.updatemanager.UpdateManager;
import starify.itemlottery.server.utils.senders.Message;

/**
 * Listener class for handling player join events.
 */
public class PlayerJoinListener implements Listener {
    private final Message message = new Message();

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
                message.sendMessageComponent(player, "");
                message.sendMessageComponent(player,"<center>&7《 &9&lUpdate &7》 ");
                message.sendMessageComponent(player,"");
                message.sendMessageComponent(player,"<center>&bNew update &l&9ItemLottery " +
                        "&r&bis available!");
                message.sendMessageComponent(player,"");
            }
        }
    }
}