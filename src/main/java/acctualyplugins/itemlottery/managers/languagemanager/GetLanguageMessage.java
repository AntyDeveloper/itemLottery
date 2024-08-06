package starify.itemlottery.managers.languagemanager;

import org.bukkit.configuration.ConfigurationSection;
import starify.itemlottery.ItemLottery;

/**
 * Class for retrieving language messages from the configuration.
 */
public class GetLanguageMessage {

    /**
     * Retrieves a language message by its ID.
     *
     * @param messageId The ID of the message to retrieve.
     * @return The language message corresponding to the given ID.
     */
    public String getLanguageMessage(String messageId) {
        ConfigurationSection messages = ItemLottery.getInstance().getConfig().getConfigurationSection("Messages");
        assert messages != null;
        return messages.getString(messageId);
    }

    /**
     * Retrieves a language message by its ID and configuration ID.
     *
     * @param messageId The ID of the message to retrieve.
     * @param messageConfigId The ID of the message configuration section.
     * @return The language message corresponding to the given IDs.
     */
    public String getLanguageMessage(String messageId, String messageConfigId) {

        ConfigurationSection messages = ItemLottery.getInstance().getConfig().getConfigurationSection("Messages");
        assert messages != null;
        ConfigurationSection messageConfiguration = messages.getConfigurationSection(messageConfigId);
        assert messageConfiguration != null;
        return messageConfiguration.getString(messageId);
    }
}