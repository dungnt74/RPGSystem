package dungnt.rpg.player;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.ClassLevelBonus;
import dungnt.rpg.classsystem.RPGClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerManager {

    private final MyRPG plugin;

    private final Map<UUID, PlayerData> players =
            new HashMap<>();

    public PlayerManager(MyRPG plugin) {
        this.plugin = plugin;
    }

    // ==================================================
    // GET DATA
    // ==================================================

    public PlayerData getData(Player player) {

        return getData(
                player.getUniqueId()
        );
    }

    public PlayerData getData(UUID uuid) {

        return players.computeIfAbsent(
                uuid,
                PlayerData::new
        );
    }

    // ==================================================
    // SET CLASS
    // ==================================================

    public void setClass(
            Player player,
            RPGClass rpgClass
    ) {

        if (player == null || rpgClass == null) {
            return;
        }

        PlayerData data =
                getData(player);

        UUID uuid =
                player.getUniqueId();

        RPGClass oldClass =
                data.getRpgClass();

        // ==================================================
        // REMOVE CLASS CŨ
        // ==================================================

        if (oldClass != null) {

            plugin.getStatManager()
                    .removeClass(
                            uuid,
                            oldClass
                    );
        }

        // ==================================================
        // SET CLASS MỚI
        // ==================================================

        data.setRpgClass(
                rpgClass
        );

        // ==================================================
        // APPLY CLASS
        // ==================================================

        plugin.getStatManager()
                .applyClass(
                        uuid,
                        rpgClass
                );

        // ==================================================
        // APPLY LEVEL BONUS
        // ==================================================

        ClassLevelBonus.apply(
                uuid,
                rpgClass,
                data.getLevel(),
                plugin.getStatManager()
        );
    }

    // ==================================================
    // REMOVE PLAYER
    // ==================================================

    public void remove(Player player) {

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

        // Xóa toàn bộ modifier còn lại
        plugin.getStatManager()
                .clearModifiers(uuid);

        players.remove(uuid);
    }

    // ==================================================
    // HAS DATA
    // ==================================================

    public boolean hasData(Player player) {

        if (player == null) {
            return false;
        }

        return players.containsKey(
                player.getUniqueId()
        );
    }

    // ==================================================
    // REFRESH CLASS + LEVEL
    // ==================================================

    public void refreshStats(Player player) {

        if (player == null) {
            return;
        }

        PlayerData data =
                getData(player);

        UUID uuid =
                player.getUniqueId();

        // Xóa toàn bộ modifier
        plugin.getStatManager()
                .clearModifiers(uuid);

        // Apply class
        if (data.getRpgClass() != null) {

            plugin.getStatManager()
                    .applyClass(
                            uuid,
                            data.getRpgClass()
                    );
        }

        // Apply level
        ClassLevelBonus.apply(
                uuid,
                data.getRpgClass(),
                data.getLevel(),
                plugin.getStatManager()
        );
    }
}