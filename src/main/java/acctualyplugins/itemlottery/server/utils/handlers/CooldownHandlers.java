package acctualyplugins.itemlottery.server.utils.handlers;

import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.managers.cooldownsmanager.CooldownsManager;
import acctualyplugins.itemlottery.server.utils.senders.Message;

/**
 * Handler class for managing player cooldowns in the lottery system.
 */
public class CooldownHandlers {
    /**
     * Message sender instance.
     */
    private static final Message message = new Message();

    /**
     * Language message retriever instance.
     */
    private static final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

    /**
     * Cooldowns manager instance.
     */
    private static final CooldownsManager cooldownsManager = new CooldownsManager();

    /**
     * Sets a cooldown for a player if they do not have the bypass permission.
     * Saves the cooldowns after setting.
     *
     * @param player The player to set the cooldown for.
     */
    public static void setPlayerCooldown(Player player) {
        if (!player.hasPermission("lottery.bypass")) {
            new CooldownsManager().setCooldown(player.getName());
            cooldownsManager.saveCooldowns();
        }
    }

    /**
     * Checks if a player has an active cooldown.
     * If a cooldown is active, sends a message to the player with the remaining time.
     *
     * @param player The player to check the cooldown for.
     * @return True if the player does not have an active cooldown, false otherwise.
     */
    public static boolean checkPlayerCooldown(Player player) {
        if (cooldownsManager.hasCooldown(player.getName())) {
            String cooldown = cooldownsManager.getRemainingTime(player.getName());
            String messageReplace = getLanguageMessage.getLanguageMessage("Cooldown")
                    .replace("%time%", cooldown);
            message.sendMessageComponent(player, messageReplace);
            return false;
        }
        return true;
    }
}