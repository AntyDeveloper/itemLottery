package acctualyplugins.itemlottery.managers.logmanager.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a log entry for a lottery event.
 */
@Getter
@AllArgsConstructor
public class Log {

    /**
     * The name of the log entry.
     */
    private final String logName;

    /**
     * The name of the player who executed the lottery.
     */
    private final String LotteryExecutor;

    /**
     * The configuration section containing the item stack details.
     */
    private final ConfigurationSection itemStack;

    /**
     * The names of the winners.
     */
    private final String Winners;

    /**
     * The number of winners in the lottery.
     */
    private final int WinnersCount;

    /**
     * Indicates whether the lottery has ended.
     */
    private final boolean LotteryEnd;
}