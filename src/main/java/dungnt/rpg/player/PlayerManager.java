package dungnt.rpg.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final Map<UUID, PlayerData> players = new HashMap<>();

    public PlayerData getData(Player player) {

        return getData(player.getUniqueId());
    }

    public PlayerData getData(UUID uuid) {

        return players.computeIfAbsent(
                uuid,
                PlayerData::new
        );
    }

    public void remove(Player player) {

        players.remove(player.getUniqueId());
    }

    public boolean hasData(Player player) {

        return players.containsKey(player.getUniqueId());
    }
}