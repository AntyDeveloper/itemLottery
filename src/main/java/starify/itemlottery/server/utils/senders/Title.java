package starify.itemlottery.server.utils.senders;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.server.utils.Formatters;

import java.time.Duration;

public class Title {
    private static final Formatters formatters = new Formatters();

    public void showMyTitleWithDurations(
            Player target,
            String titleName,
            String subTitle
    ) {
        Component TitleComponentTranslated = formatters.chatFormater(titleName, target);
        Component subTitleComponentTranslated = formatters.chatFormater(subTitle, target);

        final net.kyori.adventure.title.Title.Times times =
                net.kyori.adventure.title.Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofMillis(3000),
                        Duration.ofMillis(1000)
                );
        // Using the times object this title will use 500ms to fade in, stay on screen for 3000ms and then fade out for 1000ms
        final net.kyori.adventure.title.Title title =
                net.kyori.adventure.title.Title.title(
                        TitleComponentTranslated,
                        subTitleComponentTranslated,
                        times
                );
        // Send the title, you can also use Audience#clearTitle() to remove the title at any time
        ItemLottery.getInstance().adventure().player(target).showTitle(title);
    }
}
