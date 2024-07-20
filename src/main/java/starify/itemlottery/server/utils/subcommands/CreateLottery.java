package starify.itemlottery.server.utils.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import starify.itemlottery.ItemLottery;
import starify.itemlottery.Managers.CooldownsManager;
import starify.itemlottery.Managers.DrawManager;
import starify.itemlottery.Managers.GetLanguageMessage;
import starify.itemlottery.Managers.LogManager;
import starify.itemlottery.server.utils.Formatters;
import starify.itemlottery.server.utils.senders.Message;
import starify.itemlottery.server.utils.senders.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static starify.itemlottery.Commands.CreateLotteryCommand.enchantmentsToString;
import static starify.itemlottery.Managers.DrawManager.cc;


public class CreateLottery {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final CooldownsManager cooldownsManager = new CooldownsManager();
    public static Boolean run = false;
    private final DrawManager drawItem = new DrawManager();

    private final Message message = new Message();

    private final Title title = new Title();



    public void createLotteryCommand(Player player, int time, int itemCount, int WinnerCount) {
        ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");
        assert settings != null;
        boolean playerUse = settings.getBoolean("playerLotteryUse");

        if (playerUse) {

            if (!player.hasPermission("lottery.bypass")) {
                if (cooldownsManager.hasCooldown(player.getName())) {
                    String cooldown = cooldownsManager.getTimeRemaining(player.getName());
                    String messageReplace = getLanguageMessage.getLanguageMessage("Cooldown")
                            .replace("%time%", cooldown);
                    message.sendMessageComponent(player,
                            messageReplace);
                    return;
                }
            }


            ItemStack itemStack = player.getItemInUse();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                message.sendMessageComponent(player,
                        getLanguageMessage.getLanguageMessage("ItemInHand",
                                "LotteryCommandArgs"));
                return;
            }


            if (Bukkit.getOnlinePlayers().isEmpty() || WinnerCount > Bukkit.getOnlinePlayers().size()) {
                message.sendMessageComponent(player,
                        getLanguageMessage.getLanguageMessage("NotEnoughOnlinePlayers",
                                "LotteryCommandArgs"));
                return;
            }

            try {
                if (time <= 59) {
                    message.sendMessageComponent(player,
                            getLanguageMessage.getLanguageMessage("Time", "LotteryCommandArgs"));                    return;
                }

                int currentAmount = itemStack.getAmount();
                int newAmount = currentAmount - itemCount;

                String enchants = itemStack.getType() == Material.ENCHANTED_BOOK ? enchantmentsToString(itemStack) : "";
                boolean itemTypeDisplay = ItemLottery.getInstance().getConfig().getBoolean("itemTypeDisplay");
                String name = itemStack.getType().toString();
                String displayName = itemStack.hasItemMeta() && Objects.requireNonNull(itemStack.getItemMeta()).hasDisplayName()
                        ? itemStack.getItemMeta().getDisplayName()
                        : name;

                if (itemTypeDisplay && itemStack.getType() != Material.ENCHANTED_BOOK) {
                    displayName += "&e" + itemStack.getType();
                }

                Objects.requireNonNull(itemStack.getItemMeta()).setDisplayName(cc(displayName + enchants));

                if (run) {
                    message.sendMessageComponent(player,
                            getLanguageMessage
                                    .getLanguageMessage("CommandRun", "LotteryCommandArgs"));
                    return;
                }

                if (!player.hasPermission("lottery.bypass")) {
                    cooldownsManager.setCooldown(player.getName());
                    cooldownsManager.saveCooldowns();
                }

                itemStack.setAmount(itemCount);
                Map<String, Object> serialized = itemStack.serialize();
                List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                onlinePlayers.forEach(playerSend ->
                        title.showMyTitleWithDurations(playerSend,
                        getLanguageMessage.getLanguageMessage("StartTitle", "Lottery"),
                                getLanguageMessage.getLanguageMessage("StartSubTitle",
                                        "Lottery") + " &7by &f" + playerSend.getName()));



                drawItem.drawItem(serialized,time, WinnerCount, player);
                LogManager.createNewLog(itemStack, player, WinnerCount);


                player.getInventory().getItemInMainHand().setAmount(newAmount);

                String broadcastMessage = getLanguageMessage.getLanguageMessage("StartBroadCastMessage",
                        "Lottery");
                String replaceMessage = broadcastMessage.replace("%item_name%", displayName);

                onlinePlayers.forEach(playerSend -> {
                    message.sendMessageComponent(playerSend, replaceMessage);
                });
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
