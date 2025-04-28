package acctualyplugins.itemlottery.managers.drawmanager.utils.annoucments;

import acctualyplugins.itemlottery.server.utils.handlers.TaskHandler;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import org.bukkit.scheduler.BukkitRunnable;

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
        // Podziel listę zwycięzców na tablicę
        final String[] winners = WinnersNameList.split(",\\s*"); // Rozdzielaj po przecinku i opcjonalnych białych znakach

        // Użyj BukkitRunnable zamiast Runnable
        new BukkitRunnable() {
            private int index = 0; // Indeks aktualnie wyświetlanego zwycięzcy

            @Override
            public void run() {
                // Sprawdź, czy wyświetlono wszystkich zwycięzców
                if (index >= winners.length) {
                    this.cancel(); // Anuluj to zadanie (BukkitRunnable)
                    return; // Zakończ wykonanie tej iteracji
                }

                // Pobierz nazwę bieżącego zwycięzcy (usuń ewentualne białe znaki na początku/końcu)
                String currentWinner = winners[index].trim();

                // Wyświetl tytuł aktualnemu zwycięzcy dla wszystkich graczy online
                String titleMessage = cc(getLanguageMessage.getLanguageMessage("Title", "Lottery")); // Pobierz główny tytuł
                String subtitleMessage = cc("&9&l &k" + currentWinner + " "); // Migający podtytuł z nazwą zwycięzcy

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    // Wyślij tytuł (sendTitle jest przestarzałe, lepiej użyć Adventure API, ale trzymamy się obecnego kodu)
                    onlinePlayer.sendTitle(titleMessage, subtitleMessage, 1, 100, 1); // fadeIn=1 tick, stay=100 ticks (5s), fadeOut=1 tick
                }

                index++; // Przejdź do następnego zwycięzcy
            }
            // Uruchom zadanie: start natychmiastowy (0L), powtarzaj co 60 ticków (3 sekundy)
        }.runTaskTimer(ItemLottery.getInstance(), 0L, 60L);
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