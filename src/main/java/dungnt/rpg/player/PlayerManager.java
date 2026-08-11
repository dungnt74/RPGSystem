package dungnt.rpg.player;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.classsystem.ClassLevelBonus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private final MyRPG plugin;

    private final Map<UUID, PlayerData> players =
            new HashMap<>();

    public PlayerManager(
            MyRPG plugin
    ) {
        this.plugin = plugin;
    }

    // ==================================================
    // GET DATA
    // ==================================================

    public PlayerData getData(
            org.bukkit.entity.Player player
    ) {

        return getData(
                player.getUniqueId()
        );
    }

    public PlayerData getData(
            UUID uuid
    ) {

        return players.computeIfAbsent(
                uuid,
                PlayerData::new
        );
    }

    // ==================================================
    // SET CLASS
    // ==================================================

    public void setClass(
            org.bukkit.entity.Player player,
            RPGClass rpgClass
    ) {

        if (player == null || rpgClass == null) {
            return;
        }

        PlayerData data =
                getData(player);

        RPGClass oldClass =
                data.getRpgClass();

        // ==================================================
        // REMOVE OLD CLASS
        // ==================================================

        if (oldClass != null) {

            plugin.getStatManager()
                    .removeClass(
                            player.getUniqueId(),
                            oldClass
                    );
        }

        // ==================================================
        // SET NEW CLASS
        // ==================================================

        data.setRpgClass(
                rpgClass
        );

        // ==================================================
        // APPLY NEW CLASS
        // ==================================================

        plugin.getStatManager()
                .applyClass(
                        player.getUniqueId(),
                        rpgClass
                );

        // ==================================================
        // APPLY LEVEL BONUS
        // ==================================================

        ClassLevelBonus.apply(
                player.getUniqueId(),
                rpgClass,
                data.getLevel(),
                plugin.getStatManager()
        );
    }

    // ==================================================
    // REMOVE
    // ==================================================

    public void remove(
            org.bukkit.entity.Player player
    ) {

        if (player == null) {
            return;
        }

        UUID uuid =
                player.getUniqueId();

        PlayerData data =
                players.get(uuid);

        if (data != null &&
                data.getRpgClass() != null) {

            plugin.getStatManager()
                    .removeClass(
                            uuid,
                            data.getRpgClass()
                    );
        }

        plugin.getStatManager()
                .clearModifiers(uuid);

        players.remove(uuid);
    }

    // ==================================================
    // HAS DATA
    // ==================================================

    public boolean hasData(
            org.bukkit.entity.Player player
    ) {

        return player != null
                && players.containsKey(
                player.getUniqueId()
        );
    }
}