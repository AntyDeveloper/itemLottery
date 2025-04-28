package acctualyplugins.itemlottery.server.utils.subcommands;

import acctualyplugins.itemlottery.ItemLottery;
import acctualyplugins.itemlottery.server.utils.handlers.PermissionsHandler;
import org.bukkit.entity.Player;
import acctualyplugins.itemlottery.managers.languagemanager.GetLanguageMessage;
import acctualyplugins.itemlottery.server.utils.senders.Message;
import acctualyplugins.itemlottery.managers.drawmanager.DrawManager; // Główny manager

public class EndLottery {
    private final GetLanguageMessage getLanguageMessage = new GetLanguageMessage();
    private final Message message = new Message();

    /**
     * Natychmiast kończy odliczanie bieżącej loterii i rozpoczyna losowanie zwycięzców.
     * Nie anuluje loterii, jedynie przyspiesza jej zakończenie.
     *
     * @param player Gracz wykonujący komendę.
     */
    public void forceDraw(Player player) {
        try {
            // Użyj dedykowanego uprawnienia, jeśli chcesz rozróżnić akcje
            PermissionsHandler.hasPermission(player, "lottery.forcedraw", "Permissions"); // np. lottery.forcedraw

            if (!DrawManager.isRunning()) {
                message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("NoLotteryRunning", "ErrorMessages")); // Przykład klucza wiadomości
                return;
            }

            // Sprawdź, czy czas jest większy niż np. 1 sekunda, aby uniknąć wielokrotnego wywołania
            if (DrawManager.getRemainingTime() > 1) {
                ItemLottery.getInstance().getLogger().info("Player " + player.getName() + " is forcing the draw for the current lottery.");
                // Ustawiamy pozostały czas na 1 sekundę. Następny tick zadania CountDown
                // wykryje, że czas się skończył (remainingTime-- sprawi, że będzie 0)
                // i uruchomi LotteryDrawingService.performDraw.
                DrawManager.setRemainingTime(1);
                // Można też zmodyfikować nazwę bossbaru
                if (DrawManager.getBossBar() != null) {
                     DrawManager.getBossBar().name(DrawManager.REFACTOR.chatRefactor("&eDrawing winners soon...", player)); // Używamy REFACTOR z DrawManager
                }
                message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("ForcingDraw", "LotteryCommandArgs")); // Przykład klucza wiadomości
            } else {
                // Loterie już się kończy/losuje
                message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("LotteryEndingSoon", "ErrorMessages")); // Przykład klucza wiadomości
            }

        } catch (Exception e) {
            ItemLottery.getInstance().getLogger().severe("Error forcing lottery draw: " + e.getMessage());
            e.printStackTrace();
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs"));
        }
    }

    /**
     * Anuluje bieżącą loterię bez wyłaniania zwycięzców.
     * Jeśli w kolejce są inne loterie, następna zostanie uruchomiona automatycznie.
     *
     * @param player Gracz wykonujący komendę.
     */
    public void cancelLottery(Player player) {
        try {
            // Użyj dedykowanego uprawnienia
            PermissionsHandler.hasPermission(player, "lottery.cancel", "Permissions"); // np. lottery.cancel

            if (!DrawManager.isRunning()) {
                message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("NoLotteryRunning", "ErrorMessages")); // Przykład klucza wiadomości
                return;
            }

            ItemLottery.getInstance().getLogger().info("Player " + player.getName() + " is cancelling the current lottery.");

            // Wywołaj nową, centralną metodę do zatrzymywania loterii
            // Ta metoda powinna zająć się anulowaniem zadania, usunięciem bossbaru,
            // zresetowaniem stanu i wywołaniem LotteryQueue.triggerNextLotteryCheck()
            DrawManager.stopLottery();

            // Wiadomość o anulowaniu powinna być wysyłana przez DrawManager.stopLottery()
            // Ale możemy wysłać potwierdzenie do gracza, który wywołał komendę.
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("LotteryCancelled", "LotteryCommandArgs")); // Przykład klucza wiadomości

            // Nie ma potrzeby ręcznego czyszczenia list (TaskManager, ServiceManager) - DrawManager/Reset powinien to robić
            // Nie ma potrzeby ręcznego usuwania bossbaru - DrawManager.stopLottery() to robi
            // Nie ma potrzeby ręcznego planowania LotteryQueue - DrawManager.stopLottery()/Reset to robi

        } catch (Exception e) {
            ItemLottery.getInstance().getLogger().severe("Error cancelling lottery: " + e.getMessage());
            e.printStackTrace();
            message.sendMessageComponent(player, getLanguageMessage.getLanguageMessage("Error", "LotteryCommandArgs"));
        }
    }
}