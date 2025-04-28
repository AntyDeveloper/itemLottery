package acctualyplugins.itemlottery.managers.drawmanager.tasks;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager; // Potrzebne dla REFACTOR i typu loterii?
import acctualyplugins.itemlottery.managers.drawmanager.LotteryDrawingService;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component; // Potrzebny do BossBar.name()
import org.bukkit.entity.Player; // Potrzebny do refaktoryzacji czatu
import org.bukkit.scheduler.BukkitRunnable;

public class CountDown extends BukkitRunnable {

    private final ItemLottery plugin;
    private final DrawManager drawManager; // lub LotteryDrawingService
    private final String lotteryId; // Np. logName lub UUID
    private final BossBar bossBar;
    private final long endTimeMillis;
    private final Player lotteryMaker; // Potrzebny do refaktoryzacji czatu
    private final int winnersCount; // Potrzebny do performDraw
    private final org.bukkit.inventory.ItemStack drawItem; // Potrzebny do performDraw
    private final boolean ticketUse; // Potrzebny do określenia typu

    public CountDown(ItemLottery plugin, DrawManager drawManager, String lotteryId, BossBar bossBar, long durationSeconds, Player lotteryMaker, int winnersCount, org.bukkit.inventory.ItemStack drawItem, boolean ticketUse) {
        this.plugin = plugin;
        this.drawManager = drawManager;
        this.lotteryId = lotteryId;
        this.bossBar = bossBar;
        this.endTimeMillis = System.currentTimeMillis() + (durationSeconds * 1000);
        this.lotteryMaker = lotteryMaker;
        this.winnersCount = winnersCount;
        this.drawItem = drawItem;
        this.ticketUse = ticketUse;
    }

    @Override
    public void run() {
        long remainingMillis = endTimeMillis - System.currentTimeMillis();
        long remainingSeconds = remainingMillis / 1000;

        // Sprawdź, czy loteria nie została anulowana zewnętrznie (opcjonalne, zależy od logiki DrawManager)
        // if (!drawManager.isLotteryActive(lotteryId)) { // Przykładowa metoda
        //     this.cancel();
        //     if (bossBar != null) {
        //         drawManager.removeBossBar(lotteryId, bossBar); // Posprzątaj bossbar
        //     }
        //     return;
        // }

        if (remainingMillis <= 0) {
            // CZAS SIĘ SKOŃCZYŁ
            this.cancel(); // Zatrzymaj ten konkretny timer

            // Sprawdzenie stanu przed losowaniem
            if (drawItem == null || lotteryMaker == null || bossBar == null) {
                ItemLottery.getInstance().getLogger().severe("Lottery " + lotteryId + " ended but state is inconsistent! Cannot perform draw.");
                // Tutaj logika resetowania może być bardziej skomplikowana,
                // bo musimy wiedzieć, CO resetować dla TEJ loterii.
                // DrawManager powinien mieć metodę do czyszczenia po konkretnej loterii.
                // drawManager.cleanupFailedLottery(lotteryId, bossBar);
                return;
            }

            // Określ typ
            DrawManager.LotteryType type = ticketUse ? DrawManager.LotteryType.TICKET : DrawManager.LotteryType.FREE;
            ItemLottery.getInstance().getLogger().info("Countdown for lottery " + lotteryId + " finished. Initiating draw (Type: " + type + ")");

            // Wywołaj usługę losowania - przekazujemy dane tej konkretnej loterii
            // Zakładamy, że performDraw ukryje/usunie bossBar i anuluje zadanie (chociaż już je anulowaliśmy `this.cancel()`)
            LotteryDrawingService.performDraw(
                    DrawManager.getDrawItem(),
                    winnersCount,
                    lotteryMaker,
                    bossBar,
                    type,
                    this // Przekazanie samego siebie jest rzadkie, zazwyczaj performDraw nie potrzebuje taska
                    // Można usunąć ostatni argument z performDraw, jeśli nie jest potrzebny
            );

            // Resetowanie stanu w DrawManager powinno być wykonane przez performDraw
            // lub wywołane przez nie po zakończeniu. Nie resetuj tutaj globalnego stanu!
            // drawManager.handleLotteryCompletion(lotteryId); // Przykładowa metoda

        } else {
            // CZAS NADAL PŁYNIE - aktualizuj BossBar
            if (bossBar != null) {
                // Użyj DrawManager.REFACTOR lub przenieś logikę refaktoryzacji
                Component newName = DrawManager.REFACTOR.chatRefactor(
                        "&aLottery ends in: &e" + remainingSeconds + "s", lotteryMaker);
                bossBar.name(newName);

                // Aktualizuj postęp na bossbarze (opcjonalnie)
                float progress = Math.max(0.0f, Math.min(1.0f, (float) remainingMillis / (float) (getInitialDurationMillis())));
                bossBar.progress(progress);
            }
        }
    }

    // Metoda pomocnicza do obliczenia początkowego czasu trwania w milisekundach
    private long getInitialDurationMillis() {

        return 60 * 1000L; // Przykładowo 60 sekund
    }

}