package acctualyplugins.itemlottery.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.logmanager.objects.Log;
import acctualyplugins.itemlottery.server.utils.subcommands.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CreateLotteryCommand  {

    private static Double ticketprice = null;
    private final ArrayList<Log> logs = ItemLottery.getInstance().getLogList();

    /**
     * Converts the enchantments of an ItemStack to a string representation.
     *
     * @param itemStack The ItemStack whose enchantments are to be converted.
     * @return A string representation of the enchantments.
     */
    public static String enchantmentsToString(ItemStack itemStack) {
        StringBuilder enchantmentsString = new StringBuilder();

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if (!enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                enchantmentsString.append(enchantment.getKey().getKey()).append(":").append(level).append(", ");
            }
            enchantmentsString.setLength(enchantmentsString.length() - 2);
        }

        return enchantmentsString.toString();
    }

    /**
     * Constructor for CreateLotteryCommand.
     * Registers the lottery commands and their subcommands.
     */
    public CreateLotteryCommand() {
        new CommandAPICommand("lottery")
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(
                                new StringArgument("time"),
                                new StringArgument("price ammount"),
                                new StringArgument("winner count"),
                                new BooleanArgument("tickets to lottery").setOptional(true),
                                new StringArgument("ticket price").setOptional(true)
                        )
                        .withPermission("lottery.create")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            int time = Integer.parseInt((String) Objects.requireNonNull(args.get("time")));
                            int priceCount = Integer.parseInt((String) Objects.requireNonNull(args.get("price ammount")));
                            int winnerCount = Integer.parseInt((String) Objects.requireNonNull(args.get("winner count")));
                            Boolean ticketsToLotteryArg = (Boolean) args.get("tickets to lottery");
                            boolean ticketsToLottery = ticketsToLotteryArg != null ? ticketsToLotteryArg : false;

                            String ticketPriceArg = (String) args.get("ticket price");
                            int ticketPrice = ticketPriceArg != null ? Integer.parseInt(ticketPriceArg) : 0;

                            ticketprice = (double) ticketPrice;

                            new CreateLottery().createLotteryCommand(player, time, priceCount, winnerCount,
                                    ticketsToLottery, ticketPrice);
                        })
                )
                .withSubcommand(new CommandAPICommand("buyticket")
                        .withAliases("tbuy")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            new ButTicketLottery().BuyTicketLotteryCommand(player);
                        })
                )
                .withSubcommand(new CommandAPICommand("history")
                        .withPermission("lottery.history")
                        .withArguments(new ListArgumentBuilder<Log>("logName").allowDuplicates(false)
                                .withList(logs.stream()
                                        .sorted(Comparator.comparing((Log log) -> LocalDateTime.parse(log.getLogName(),
                                                        DateTimeFormatter.ofPattern("HH:mm/dd:MM:yy")))
                                                .reversed())
                                        .collect(Collectors.toList()))
                                .withMapper(log -> log.getLogName().toLowerCase())
                                .buildGreedy()
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            List<Log> theList = (List<Log>) args.get("logName");
                            for (Log logName : theList) {
                                new HistoryLottery().ShowHistoryLottery(player, logName.getLogName());
                            }
                        })
                )
                .withSubcommand(new CommandAPICommand("end")
                        .withSubcommand(new CommandAPICommand("force")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().forceEndLottery(player);
                                })
                        )
                        .withSubcommand(new CommandAPICommand("draw")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().drawEndLottery(player);
                                })
                        )
                )
                .withSubcommand(new CommandAPICommand("author")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            new AuthorLottery(player);
                        })
                )
                .withSubcommand(new CommandAPICommand("giveitem")
                        .withArguments(new ListArgumentBuilder<Log>("logName").allowDuplicates(false)
                                .withList(logs.stream()
                                        .sorted(Comparator.comparing((Log log) -> LocalDateTime.parse(log.getLogName(),
                                                        DateTimeFormatter.ofPattern("HH:mm/dd:MM:yy")))
                                                .reversed())
                                        .collect(Collectors.toList()))
                                .withMapper(log -> log.getLogName().toLowerCase())
                                .buildGreedy()
                        )
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            List<Log> theList = (List<Log>) args.get("logName");
                            for (Log logName : theList) {
                                new GiveItemLottery(player, logName.getLogName());
                            }
                        })
                )
                .register();
    }
}