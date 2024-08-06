package starify.itemlottery.managers.drawmanager.utils.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import starify.itemlottery.ItemLottery;

public class RemoveBossbar {

    public void removeBossbar(BossBar bossBar) {
        // Remove the boss bar from the players' view
        for (Player playersRemoveBossBar : Bukkit.getOnlinePlayers()) {
            Audience audience = ItemLottery.getInstance().adventure().player(playersRemoveBossBar);
            bossBar.removeViewer(audience);
        }

    }
}
