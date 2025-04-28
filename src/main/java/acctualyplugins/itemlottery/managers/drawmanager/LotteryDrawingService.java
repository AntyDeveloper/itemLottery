package acctualyplugins.itemlottery.managers.drawmanager; // Upewnij się, że pakiet jest poprawny

import acctualyplugins.itemlottery.ItemLottery; // Potrzebne do logowania
import acctualyplugins.itemlottery.managers.drawmanager.tasks.CountDown;
import acctualyplugins.itemlottery.managers.drawmanager.utils.SelectWinners;
import acctualyplugins.itemlottery.managers.drawmanager.utils.intalization.players.GetNames;
import acctualyplugins.itemlottery.managers.drawmanager.utils.intalization.players.NotEnoughPlayers;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue.LotteryQueue;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask; // Potrzebne, jeśli przekazujemy task

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static acctualyplugins.itemlottery.managers.drawmanager.utils.annoucments.WinnersAnnouncement.announceWinners;
import static acctualyplugins.itemlottery.managers.drawmanager.utils.annoucments.WinnersAnnouncement.announceWinnersAnimation;
import static acctualyplugins.itemlottery.managers.drawmanager.utils.handlers.HandleNotEnoughPlayers.handleNotEnoughPlayers;
import static acctualyplugins.itemlottery.managers.drawmanager.utils.intalization.players.NotEnoughPlayers.notEnoughPlayers;
import static acctualyplugins.itemlottery.managers.drawmanager.utils.logs.UpdateLogs.logWinners;
import static acctualyplugins.itemlottery.managers.drawmanager.utils.rewards.DistributeRewards.distributeRewards;

public class LotteryDrawingService {

    /**
     * Wykonuje proces losowania zwycięzców loterii.
     *
     * @param serialized   Zserializowany przedmiot (nagroda).
     * @param winnersCount Oczekiwana liczba zwycięzców.
     * @param lotteryMaker Gracz, który uruchomił loterię.
     * @param bossBar      BossBar używany do ogłoszeń.
     * @param lotteryType  Typ loterii (FREE lub TICKET).
     * @param countdownTask Zadanie odliczania (do anulowania). Można rozważyć usunięcie zależności statycznej.
     */
    public static void performDraw(Map<String, Object> serialized, int winnersCount, Player lotteryMaker, BossBar bossBar, DrawManager.LotteryType lotteryType, CountDown countdownTask) {
        // Reset czasu - może lepiej robić to w DrawManager przed wywołaniem tej metody?
        // DrawManager.remainingTime = 0; // Zależność statyczna

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

        // 1. Sprawdzenie ogólnej liczby graczy online
        if (notEnoughPlayers(onlinePlayers, winnersCount)) {
            handleNotEnoughPlayers(serialized, lotteryMaker, bossBar, countdownTask); // Przekaż task do anulowania
            return;
        }

        List<Player> winners;

        // 2. Wybór zwycięzców zależnie od typu loterii
        if (lotteryType == DrawManager.LotteryType.TICKET) { // <<< POPRAWIONE PORÓWNANIE
            winners = SelectWinners.selectWinnersWithTicket(winnersCount);
            if (NotEnoughPlayers.notEnoughPlayersBuyTicket(winners, lotteryMaker, bossBar)) {
                 ItemLottery.getInstance().getLogger().info("Not enough players bought tickets for the lottery, but proceeding if any winners were selected.");
             }
        } else { // LotteryType.FREE
            winners = SelectWinners.selectWinners(winnersCount);
        }

        // 3. Sprawdzenie, czy udało się wybrać jakichkolwiek zwycięzców
        if (winners == null || winners.isEmpty()) {
            ItemLottery.getInstance().getLogger().warning("No winners could be selected for the lottery (Type: " + lotteryType + ").");

            handleNotEnoughPlayers(serialized, lotteryMaker, bossBar, countdownTask); // Używamy istniejącej na razie

            return;
        }

        // 4. Przygotowanie listy nazw zwycięzców
        String winnersNameList = String.join(", ", GetNames.getNames(winners));

        // 5. Asynchroniczna animacja ogłoszenia
        CompletableFuture<Void> animationFuture = CompletableFuture.runAsync(() ->
                announceWinnersAnimation(winnersNameList, bossBar));

        // 6. Opóźnione wykonanie głównych akcji po animacji
        long finalAnnouncementDelaySeconds = 5; // Czas trwania animacji
        animationFuture.thenRunAsync(() -> {
            announceWinners(winnersNameList);            // Ostateczne ogłoszenie
            distributeRewards(serialized, winners, winnersCount); // Rozdanie nagród
            logWinners(winnersNameList);                 // Zapisanie logów
            if (countdownTask != null) {
                countdownTask.cancel();
                LotteryQueue.checkAndStartNextLottery();// Anulowanie zadania odliczania
            } else {
                ItemLottery.getInstance().getLogger().warning("Countdown task was null during final draw steps.");
            }
            // Resetowanie stanu loterii (np. activeLottery = null) powinno być zrobione
            // w DrawManager po zakończeniu tego procesu.

        }, CompletableFuture.delayedExecutor(finalAnnouncementDelaySeconds, TimeUnit.SECONDS));
    }
}