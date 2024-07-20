package starify.itemlottery.server.utils.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import starify.itemlottery.Managers.DrawManager;
import starify.itemlottery.Managers.GetLanguageMessage;
import starify.itemlottery.server.utils.senders.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EndLottery {
        private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();

        private final Message message = new Message();
        public void DrawEndLottery(Player player) {
            try {
                Map<String, Object> Item = DrawManager.getDrawItem();
                int Winner = DrawManager.getWinnersCount();

                if(DrawManager.isRunning()) {
                        message.sendMessageComponent(player, getLanguageMessage.
                                getLanguageMessage("LotteryNotActive", "LotteryCommandArgs"));
                }
                        DrawManager.Draw(Item, Winner, player);
                        message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("ForceEnd",
                                "LotteryCommandArgs"));
            } catch (Exception e) {
                    message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error",
                            "LotteryCommandArgs"));
            }
        }

        public void ForceEndLottery(Player player) {
            if(DrawManager.isRunning()) {
                    message.sendMessageComponent(player, getLanguageMessage.
                            getLanguageMessage("LotteryNotActive", "LotteryCommandArgs"));
                    return;
            }
                    DrawManager.getTask().cancel();
                    DrawManager.getBossBar().removeAll();
                    List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());

                    onlinePlayers.forEach(playerSend -> {
                            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage(
                                    "ForeEndBroadcast", "LotteryCommandArgs"));
                            message.sendMessageComponent(playerSend, getLanguageMessage.getLanguageMessage(
                                    "ForceEnd", "LotteryCommandArgs"));
                    });


        }

}
