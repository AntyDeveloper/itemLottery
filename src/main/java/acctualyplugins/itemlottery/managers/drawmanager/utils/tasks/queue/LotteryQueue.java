package acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.queue;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.drawmanager.utils.tasks.LotteryTask;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class LotteryQueue {
    @Getter
    private static final Queue<LotteryTask> mainQueue = new LinkedList<>();
    private static final Queue<LotteryTask> auxiliaryQueue = new LinkedList<>();
    private static boolean isRunning = false;
    private static final int MAX_QUEUE_SIZE = 15;

    private static final Message message = new Message();
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    public static void addLotteryToQueue(LotteryTask lotteryTask) {
        long currentTime = System.currentTimeMillis();
        long scheduledTime = lotteryTask.getLog().getTimestamp();

        if (scheduledTime > currentTime) {
            auxiliaryQueue.add(lotteryTask);
            scheduleAuxiliaryQueueCheck();
        } else {
            addToMainQueue(lotteryTask);
        }
    }

    private static void addToMainQueue(LotteryTask lotteryTask) {
        if (mainQueue.size() >= MAX_QUEUE_SIZE) {
            Player player = lotteryTask.getPlayer();
            Bukkit.getScheduler().runTask(ItemLottery.getInstance(), () -> {
                String msg = getLanguageMessage.getLanguageMessage("QueueFull");
                message.sendMessageComponent(player, msg);
            });
            return;
        }

        mainQueue.add(lotteryTask);
        if (!isRunning) {
            startNextLottery();
        } else {
            Player player = lotteryTask.getPlayer();
            Bukkit.getScheduler().runTask(ItemLottery.getInstance(), () -> {
                String msg = getLanguageMessage.getLanguageMessage("AddedToQueue");
                message.sendMessageComponent(player, msg);
            });
        }
    }

    private static void scheduleAuxiliaryQueueCheck() {
        Bukkit.getScheduler().runTaskTimer(ItemLottery.getInstance(), () -> {
            long currentTime = System.currentTimeMillis();
            Iterator<LotteryTask> iterator = auxiliaryQueue.iterator();

            while (iterator.hasNext()) {
                LotteryTask task = iterator.next();
                long timeDifference = task.getLog().getTimestamp() - currentTime;

                if (timeDifference <= 5 * 60 * 1000) { // 5 minutes before scheduled time
                    iterator.remove();
                    addToMainQueue(task);
                }
            }
        }, 0L, 20L); // Check every second
    }

    public static LotteryTask getLotteryByName(String logName) {
        for (LotteryTask task : mainQueue) {
            if (task.getLog().getLogName().equals(logName)) {
                return task;
            }
        }
        for (LotteryTask task : auxiliaryQueue) {
            if (task.getLog().getLogName().equals(logName)) {
                return task;
            }
        }
        return null; // Return null if no lottery with the given name is found
    }

    public static void startNextLottery() {
        if (mainQueue.isEmpty()) {
            isRunning = false;
            return;
        }

        LotteryTask nextLottery = mainQueue.peek();
        long currentTime = System.currentTimeMillis();
        long timeDifference = nextLottery.getLog().getTimestamp() - currentTime;

        if (timeDifference > 0) {
            Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::startNextLottery, timeDifference / 50); // Wait until the scheduled time
        } else {
            mainQueue.poll();
            isRunning = true;
            nextLottery.start(() -> {
                isRunning = false;
                if (!mainQueue.isEmpty()) {
                    Bukkit.getScheduler().runTaskLater(ItemLottery.getInstance(), LotteryQueue::startNextLottery, 15 * 20); // 15-second delay
                } else {
                    isRunning = false;
                }
            });
        }
    }

    public static void removeLotteryFromQueue(String logName) {
        mainQueue.removeIf(task -> task.getLog().getLogName().equals(logName));
        auxiliaryQueue.removeIf(task -> task.getLog().getLogName().equals(logName));
    }

    public static void clearQueue() {
        mainQueue.clear();
        auxiliaryQueue.clear();
    }

    public static void editLotteryDelay(String logName, int newDelay) {
        LotteryTask task = getLotteryByName(logName);
        if (task != null) {
            task.getLog().setTimestamp(System.currentTimeMillis() + newDelay * 1000L);
            mainQueue.remove(task);
            auxiliaryQueue.remove(task);
            addLotteryToQueue(task);
        }
    }

    public static void checkLogsOnStartup() {
        List<Log> logs = ItemLottery.getInstance().getLogList();
        logs.sort(Comparator.comparing(Log::getLogName)); // Assuming log name contains the creation time

        for (Log log : logs) {
            if (!log.isLotteryEnd()) {
                int remainingDuration = log.getDuration() - log.getElapsedTime();
                LotteryTask lotteryTask = new LotteryTask(
                        log.getItemStack(), // Use the itemStack directly
                        remainingDuration,
                        log.getWinnerCount(),
                        Bukkit.getPlayer(log.getPlayerName()),
                        log.isTicketUse(),
                        log.getTicketCost(),
                        () -> {
                            isRunning = false;
                            startNextLottery();
                        },
                        log // Pass the log reference here
                );
                addLotteryToQueue(lotteryTask);
            }
        }
    }

    public static void skip() {
        if (!mainQueue.isEmpty()) {
            mainQueue.poll();
            if (!isRunning) {
                startNextLottery();
            }
        }
    }
}