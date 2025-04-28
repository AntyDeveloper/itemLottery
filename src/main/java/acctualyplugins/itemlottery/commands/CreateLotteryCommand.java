package acctualyplugins.itemlottery.commands;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.TimeUtils;
import acctualyplugins.itemlottery.server.utils.subcommands.*;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.ListArgumentBuilder;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateLotteryCommand {
    private final ArrayList<Log> logs = ItemLottery.getInstance().getLogList();

    public CreateLotteryCommand() {
        new CommandAPICommand("lottery")
            .withSubcommand(new CommandAPICommand("create")
                .withSubcommand(new CommandAPICommand("delay")
                    .withArguments(new StringArgument("delay <example: 5h>"),
                        new StringArgument("time <example: 10m>"),
                        new StringArgument("price amount"),
                        new StringArgument("winner count"),
                        new BooleanArgument("tickets to lottery").setOptional(true),
                        new StringArgument("ticket price").setOptional(true))
                    .withPermission("lottery.create")
                    .executes((sender, args) -> {
                        Player player = (Player) sender;

                        String delay = (String) Objects.requireNonNull(args.get("delay <example: 5h>"));
                        int priceCount = Integer.parseInt((String) Objects.requireNonNull(args.get("price amount")));
                        int winnerCount = Integer.parseInt((String) Objects.requireNonNull(args.get("winner count")));
                        Boolean ticketsToLotteryArg = (Boolean) args.get("tickets to lottery");
                        boolean ticketsToLottery = ticketsToLotteryArg != null ? ticketsToLotteryArg : false;

                        String ticketPriceArg = (String) args.get("ticket price");
                        double ticketPrice = ticketPriceArg != null ? Integer.parseInt(ticketPriceArg) : 0;
                        int timeInSeconds = TimeUtils.pareseTimeToSeconds((String) args.get("time <example: 10m>"));
                        int delayInSeconds = TimeUtils.pareseTimeToSeconds(delay);
                        long timestamp = TimeUtils.parseDelayToTimestamp(delay);
                        new CreateLottery().createLottery(player, delayInSeconds, timeInSeconds, priceCount, winnerCount,
                            ticketsToLottery, ticketPrice);
                    })
                )
                    .withSubcommand(new CommandAPICommand("now")
                            .withArguments(new StringArgument("time <example: 10m>"),
                                    new StringArgument("price amount"),
                                    new StringArgument("winner count"),
                                    new BooleanArgument("tickets to lottery").setOptional(true),
                                    new StringArgument("ticket price").setOptional(true))
                            .withPermission("lottery.create")
                            .executes((sender, args) -> {
                                Player player = (Player) sender;

                                int priceCount = Integer.parseInt((String) Objects.requireNonNull(args.get("price amount")));
                                int winnerCount = Integer.parseInt((String) Objects.requireNonNull(args.get("winner count")));
                                Boolean ticketsToLotteryArg = (Boolean) args.get("tickets to lottery");
                                boolean ticketsToLottery = ticketsToLotteryArg != null ? ticketsToLotteryArg : false;

                                String ticketPriceArg = (String) args.get("ticket price");
                                double ticketPrice = ticketPriceArg != null ? Integer.parseInt(ticketPriceArg) : 0;
                                int timeInSeconds = TimeUtils.pareseTimeToSeconds((String) args.get("time <example: 10m>"));
                                new CreateLottery().createLottery(player, 0, timeInSeconds, priceCount, winnerCount,
                                        ticketsToLottery, ticketPrice);
                            }) // Zakładam, że to zamknięcie jakiegoś lambda lub anonimowej klasy// Zakładam, że to zamknięcie jakiegoś lambda lub anonimowej klasy
                    )
            )
                .withSubcommand(new CommandAPICommand("buyticket")
                        .withAliases("tbuy")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            new BuyTicketLottery().BuyTicketLotteryCommand(player);
                        })
                )
                .withSubcommand(new CommandAPICommand("history")
                        .withPermission("lottery.history")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                                new HistoryLottery().ShowHistoryLottery(player);

                        })
                )
                .withSubcommand(new CommandAPICommand("end")
                        .withSubcommand(new CommandAPICommand("force")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().cancelLottery(player);
                                })
                        )
                        .withSubcommand(new CommandAPICommand("draw")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().forceDraw(player);
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("queue")
                        .withSubcommand(new CommandAPICommand("skip")
                                .withPermission("lottery.skip")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new QueueLottery().skipLottery(player);
                                })
                        )
                        .withSubcommand(new CommandAPICommand("open")
                                .withPermission("lottery.queue")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new QueueLottery().openQueueGui(player);
                                })
                        )
                ).executes((sender, args) -> {
                    Player player = (Player) sender;
                    new QueueLottery().openQueueGui(player);
                })
                .withSubcommand(new CommandAPICommand("author")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            new AuthorLottery(player);
                        })
                )
                .withSubcommand(new CommandAPICommand("giveitem")
                        .withPermission("lottery.giveitem")
                        .withFullDescription("Command to summmon reward lottery item!")
                        .withArguments(new ListArgumentBuilder<Log>("logName").allowDuplicates(false)
                        .withList(logs.stream() // 1. Pobiera listę obiektów Log
                                .sorted(Comparator.comparing((Log log) -> LocalDateTime.parse(log.getLogName(),
                                                DateTimeFormatter.ofPattern("HH:mm/dd:MM:yy")))
                                        .reversed()) // 2. Sortuje je malejąco wg daty/czasu w nazwie
                                .collect(Collectors.toList())) // 3. Tworzy listę posortowanych Logów
                        .withMapper(log -> log.getLogName().toLowerCase()) // 4. Mapuje obiekt Log na jego nazwę (lowercase) dla PARSOWANIA i SUGESTII
                        .buildGreedy() // 5. Ustawia argument jako "greedy"
                )
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            List<Log> theList = (List<Log>) args.get("logName");
                            assert theList != null;
                            for (Log logName : theList) {
                                new GiveItemLottery(player, logName.getLogName());
                            }
                        })
                )
            .register();
    }
}