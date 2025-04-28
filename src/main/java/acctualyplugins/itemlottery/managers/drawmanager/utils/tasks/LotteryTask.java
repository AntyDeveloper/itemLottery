package acctualyplugins.itemlottery.managers.drawmanager.utils.tasks; // Dopasuj pakiet

import acctualyplugins.itemlottery.managers.drawmanager.LotteryLogData;
import lombok.Getter;

@Getter
public class LotteryTask {
    // Zamiast starego Log, używamy nowej klasy danych
    private final LotteryLogData logData;

    public LotteryTask(LotteryLogData logData) {
        this.logData = logData;
    }

    // Możesz dodać metody delegujące do logData dla wygody
    // np. public String getLogName() { return logData.getLogName(); }
}