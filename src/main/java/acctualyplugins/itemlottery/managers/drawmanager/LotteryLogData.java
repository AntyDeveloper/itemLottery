package acctualyplugins.itemlottery.managers.drawmanager;

import lombok.Getter; // Zakładając użycie Lombok Getter dla innych pól
import lombok.Setter;
import org.bukkit.inventory.ItemStack; // Jeśli używasz ItemStack bezpośrednio
import java.util.Map;
import java.util.UUID; // Zakładając, że używasz UUID dla queueId

@Getter
@Setter// Lombok dla uproszczenia, dodaj jeśli go używasz
public class LotteryLogData {

    private final UUID queueId; // Zakładam, że queueId jest typu UUID
    private final String logName; // Zachowane dla spójności, ale może być zbędne jeśli używasz queueId
    private final String playerName;
    @Setter
    private String timestamp; // String czy long?
    private final int duration;
    private final Map<String, Object> serializedItem; // Lub ItemStack?
    private final int winnersCount;
    private final boolean ticketUse;
    private final double ticketPrice;
    private final long scheduledStartTimeMillis; // <-- NOWE POLE

    // Zaktualizowany konstruktor
    public LotteryLogData(String logName, String playerName, String timestamp, int duration,
                          Map<String, Object> serializedItem, int winnersCount, boolean ticketUse,
                          double ticketPrice, long scheduledStartTimeMillis) { // <-- NOWY ARGUMENT
        this.queueId = UUID.randomUUID(); // Generuj ID tutaj lub przekaż
        this.logName = logName;
        this.playerName = playerName;
        this.timestamp = timestamp;
        this.duration = duration;
        this.serializedItem = serializedItem;
        this.winnersCount = winnersCount;
        this.ticketUse = ticketUse;
        this.ticketPrice = ticketPrice;
        this.scheduledStartTimeMillis = scheduledStartTimeMillis; // <-- PRZYPISANIE
    }

    // Konstruktor do wczytywania z pliku (jeśli potrzebny, też musi ustawiać scheduledStartTimeMillis)
    // public LotteryLogData(UUID queueId, String logName, ..., long scheduledStartTimeMillis) { ... }


    // Nie potrzebujesz już ręcznego gettera, jeśli używasz @Getter
    // public long getScheduledStartTimeMillis() {
    //     return scheduledStartTimeMillis;
    // }

    // ... reszta metod klasy ...
}