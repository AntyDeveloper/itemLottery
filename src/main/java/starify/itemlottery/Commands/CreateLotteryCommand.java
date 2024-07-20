package starify.itemlottery.Commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import starify.itemlottery.ItemLottery;
import starify.itemlottery.Managers.GetLanguageMessage;
import starify.itemlottery.Managers.LogManager;
import starify.itemlottery.server.utils.subcommands.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.Bukkit.getLogger;

public class CreateLotteryCommand  {
    private GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private String cc(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }
    private final LogManager logManager = new LogManager();
    private final String prefix = getLanguageMessage.getLanguageMessage("Prefix");

    private final List<String> logs = ItemLottery.getInstance().getLogList();

    public static String enchantmentsToString(ItemStack itemStack) {
        StringBuilder enchantmentsString = new StringBuilder();

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if (!enchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                enchantmentsString.append(enchantment.getKey().getKey()).append(":").append(level).append(", ");
            }
            enchantmentsString.setLength(enchantmentsString.length() - 2); // Remove the trailing comma and space
        }

        return enchantmentsString.toString();
    }


    public CreateLotteryCommand() {

        new CommandAPICommand("lottery")
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(
                                new StringArgument("time"),
                                new StringArgument("price count"),
                                new StringArgument("winner count")
                        )
                        .withPermission("lottery.create")
                        .executes((sender, args) -> {
                            Player player = (Player) sender;

                            int time = Integer.parseInt((String) Objects.requireNonNull(args.get("time")));
                            int priceCount = Integer.parseInt((String) Objects.requireNonNull(args.get("price count")));
                            int winnerCount = Integer.parseInt((String) Objects.requireNonNull(args.get("winner count")));

                            new CreateLottery().createLotteryCommand(player, time, priceCount, winnerCount);
                        })
                )
                .withSubcommand(new CommandAPICommand("history"))
                .withPermission("lottery.history")
                .withArguments(new StringArgument("logName")
                        .replaceSuggestions(ArgumentSuggestions.strings(logs))
                )
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    String logName = (String) args.get(0);
                    new HistoryLottery().ShowHistoryLottery(player, logName);
                })
                .withSubcommand(new CommandAPICommand("end")
                        .withSubcommand(new CommandAPICommand("force")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().ForceEndLottery(player);
                                })
                        )
                        .withSubcommand(new CommandAPICommand("draw")
                                .withPermission("lottery.end")
                                .executes((sender, args) -> {
                                    Player player = (Player) sender;
                                    new EndLottery().DrawEndLottery(player);
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
                        .executes((sender, args) -> {
                            Player player = (Player) sender;
                            new GiveItemLottery(player, (String) args.get(0));
                        }
                ))
                .register();
    }
}
