package acctualyplugins.itemlottery.server.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static long parseDelayToTimestamp(String delay) {
        long currentTime = System.currentTimeMillis();
        long delayInMillis = 0;

        String[] parts = delay.split(" ");
        for (String part : parts) {
            char unit = part.charAt(part.length() - 1);
            int value = Integer.parseInt(part.substring(0, part.length() - 1));

            switch (unit) {
                case 's':
                    delayInMillis += TimeUnit.SECONDS.toMillis(value);
                    break;
                case 'm':
                    delayInMillis += TimeUnit.MINUTES.toMillis(value);
                    break;
                case 'h':
                    delayInMillis += TimeUnit.HOURS.toMillis(value);
                    break;
                case 'd':
                    delayInMillis += TimeUnit.DAYS.toMillis(value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid time unit: " + unit);
            }
        }

        return currentTime + delayInMillis;
    }

    public static int pareseTimeToSeconds(String time) {
        String[] parts = time.split(" ");
        int seconds = 0;
        for (String part : parts) {
            char unit = part.charAt(part.length() - 1);
            int value = Integer.parseInt(part.substring(0, part.length() - 1));

            switch (unit) {
                case 's':
                    seconds += value;
                    break;
                case 'm':
                    seconds += TimeUnit.MINUTES.toSeconds(value);
                    break;
                case 'h':
                    seconds += TimeUnit.HOURS.toSeconds(value);
                    break;
                case 'd':
                    seconds += TimeUnit.DAYS.toSeconds(value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid time unit: " + unit);
            }
        }
        return seconds;
    }
}