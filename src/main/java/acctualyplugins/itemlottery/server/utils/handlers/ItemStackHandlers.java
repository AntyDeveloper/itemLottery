package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;

import java.util.Map;
import java.util.Objects;

import static acctualyplugins.itemlottery.managers.drawmanager.DrawManager.cc;

/**
 * Handler class for managing ItemStack operations in the lottery system.
 */
public class ItemStackHandlers {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Checks if the given ItemStack is empty (null or AIR).
     * If the ItemStack is empty, sends a message to the player.
     *
     * @param itemStack The ItemStack to check.
     * @param player The player holding the ItemStack.
     * @return True if the ItemStack is empty, false otherwise.
     */
    public static boolean isItemStackEmpty(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            message.sendMessageComponent(player, getLanguageMessage
                    .getLanguageMessage("ItemInHand", "LotteryCommandArgs"));
            return true;
        }
        return false;
    }

    /**
     * Updates the amount of items in the given ItemStack by subtracting the specified item count.
     *
     * @param itemStack The ItemStack to update.
     * @param itemCount The number of items to subtract from the ItemStack.
     * @return The new amount of items in the ItemStack.
     */
    public static int updateItemStackAmount(ItemStack itemStack, int itemCount) {
        int currentAmount = itemStack.getAmount();
        return currentAmount - itemCount;
    }

    /**
     * Retrieves the display name of the given ItemStack.
     * If the ItemStack has a custom display name, it is returned.
     * Otherwise, the type of the ItemStack is returned.
     * If the ItemStack is an enchanted book, the enchantments are included in the display name.
     *
     * @param itemStack The ItemStack to get the display name of.
     * @return The display name of the ItemStack.
     */
    public static String getDisplayName(ItemStack itemStack) {
        String enchants = itemStack.getType() == Material.ENCHANTED_BOOK ? enchantmentsToString(itemStack) : "";
        boolean itemTypeDisplay = ItemLottery.getInstance().getConfig().getBoolean("itemTypeDisplay");
        String name = itemStack.getType().toString();
        String displayName = itemStack.hasItemMeta() && Objects.requireNonNull(itemStack.getItemMeta()).hasDisplayName()
                ? itemStack.getItemMeta().getDisplayName()
                : name;

        if (itemTypeDisplay && itemStack.getType() != Material.ENCHANTED_BOOK) {
            displayName += "&e" + itemStack.getType();
        }

        return cc(displayName + enchants);
    }

    /**
     * Converts the enchantments of an ItemStack to a string representation.
     *
     * @param itemStack The ItemStack containing enchantments.
     * @return A string representation of the enchantments.
     */
    public static String enchantmentsToString(ItemStack itemStack) {
        StringBuilder enchants = new StringBuilder();
        for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
            enchants.append(entry.getKey().getKey().getKey()).append(" ").append(entry.getValue()).append(", ");
        }
        // Remove the trailing comma and space
        if (enchants.length() > 0) {
            enchants.setLength(enchants.length() - 2);
        }
        return enchants.toString();
    }
}