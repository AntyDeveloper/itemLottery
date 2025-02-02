package acctualyplugins.itemlottery.managers.logmanager.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Log {
    private String logName;
    private String playerName;
    private Map<String, Object> itemStack;
    private String winners;
    private int winnerCount;
    private boolean lotteryEnd;
    private int duration;
    private int elapsedTime;
    private boolean ticketUse;
    private double ticketCost;
    private long timestamp;

    public boolean matchesFilter(String filter) {
        return logName.equals(filter);
    }
}