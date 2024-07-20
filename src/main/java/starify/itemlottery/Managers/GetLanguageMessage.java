package starify.itemlottery.Managers;

import org.bukkit.configuration.ConfigurationSection;
import starify.itemlottery.ItemLottery;

public class GetLanguageMessage {
    public  String getLanguageMessage(String messageId) {
       ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");
        assert settings != null;
        ConfigurationSection messages = settings.getConfigurationSection("Messages");
        assert messages != null;
        return messages.getString(messageId);
    }
    public String getLanguageMessage(String messageId, String messageConfigId) {
        ConfigurationSection settings = ItemLottery.getInstance().getConfig().getConfigurationSection("settings");
        assert settings != null;
        ConfigurationSection messages = settings.getConfigurationSection("Messages");
        assert messages != null;
        ConfigurationSection messageConfiguration = messages.getConfigurationSection(messageConfigId);
        assert messageConfiguration != null;
        return messageConfiguration.getString(messageId);
    }
}
