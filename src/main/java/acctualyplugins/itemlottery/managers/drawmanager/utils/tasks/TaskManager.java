package acctualyplugins.itemlottery.managers.drawmanager.utils.tasks;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class TaskManager {
    public static final List<Player> selectedPlayers = new ArrayList<>();

}
