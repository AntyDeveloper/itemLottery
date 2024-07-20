package starify.itemlottery.Events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import starify.itemlottery.ItemLottery;

public class PlayerJoinListener implements Listener {
    private String cc(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if(player.isOp()) {
            ItemLottery.getInstance().updateManager.checkUpdate();
            if(ItemLottery.getInstance().updateManager.opMessageSend) {
                player.sendMessage("");
                player.sendMessage(cc("                        &7《 &9&lUpdate &7》 "));
                player.sendMessage("");
                player.sendMessage(cc("              &bNew update &l&9ItemLottery &r&bis available!"));
                player.sendMessage("");
            }

        }
    }
}
