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

    public PlayerManager(
            MyRPG plugin
    ) {

        this.plugin = plugin;
    }

    // ==================================================
    // GET DATA
    // ==================================================

    public PlayerData getData(
            Player player
    ) {

        return getData(
                player.getUniqueId()
        );
    }

    public PlayerData getData(
            UUID uuid
    ) {

        if (uuid == null) {
            return null;
        }

        PlayerData data =
                players.computeIfAbsent(
                        uuid,
                        PlayerData::new
                );

        /*
         * PlayerStats là BASE STATS thực tế của player.
         *
         * StatManager dùng baseStats làm nền để tính:
         * BASE + CLASS + LEVEL + EQUIPMENT.
         *
         * Vì vậy phải đồng bộ PlayerStats vào StatManager
         * trước khi CombatService đọc stat.
         */
        plugin.getStatManager()
                .syncBaseStats(
                        uuid,
                        data.getStats()
                );

        return data;
    }

    // ==================================================
    // SET CLASS
    // ==================================================

    public void setClass(
            Player player,
            RPGClass rpgClass
    ) {

        if (player == null ||
                rpgClass == null) {

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

            plugin.getStatManager()
                    .removeClassGrowth(
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

        /*
         * Đổi class không được làm mất stat của equipment.
         * Re-sync inventory thật sau khi class/level modifiers
         * đã được cập nhật.
         */
        if (plugin.getEquipmentListener() != null) {

            plugin.getEquipmentListener()
                    .refreshEquipment(player);
        }
    }


    // ==================================================
    // REMOVE CLASS + RESET BASE STATS
    // ==================================================

    public void removeClassAndResetStats(
            Player player
    ) {

        if (player == null) {
            return;
        }

        UUID uuid =
                player.getUniqueId();

        PlayerData data =
                getData(player);

        /*
         * Xoá runtime equipment trước.
         */
        plugin.getEquipmentManager()
                .clear(uuid);

        /*
         * Xoá class / level / equipment modifiers.
         */
        plugin.getStatManager()
                .clear(uuid);

        /*
         * Reset BASE STATS về 0.
         */
        data.getStats()
                .resetAllToZero();

        /*
         * Bỏ class.
         */
        data.setRpgClass(null);

        /*
         * Đồng bộ lại base zero vào StatManager.
         */
        plugin.getStatManager()
                .syncBaseStats(
                        uuid,
                        data.getStats()
                );
    }

    // ==================================================
    // REMOVE PLAYER
    // ==================================================

    public void remove(
            Player player
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

        // Xóa toàn bộ modifier khi player bị remove
        plugin.getStatManager()
                .clearModifiers(uuid);

        players.remove(uuid);
    }

    // ==================================================
    // HAS DATA
    // ==================================================

    public boolean hasData(
            Player player
    ) {

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

    public void refreshStats(
            Player player
    ) {

        if (player == null) {
            return;
        }

        PlayerData data =
                getData(player);

        UUID uuid =
                player.getUniqueId();

        // ==================================================
        // REMOVE CLASS CŨ
        // ==================================================

        if (data.getRpgClass() != null) {

            plugin.getStatManager()
                    .removeClass(
                            uuid,
                            data.getRpgClass()
                    );

            plugin.getStatManager()
                    .removeClassGrowth(
                            uuid,
                            data.getRpgClass()
                    );
        }

        // ==================================================
        // REMOVE LEVEL BONUS
        // ==================================================

        plugin.getStatManager()
                .removeLevel(
                        uuid
                );

        // ==================================================
        // APPLY CLASS
        // ==================================================

        if (data.getRpgClass() != null) {

            plugin.getStatManager()
                    .applyClass(
                            uuid,
                            data.getRpgClass()
                    );
        }

        // ==================================================
        // APPLY LEVEL
        // ==================================================

        ClassLevelBonus.apply(
                uuid,
                data.getRpgClass(),
                data.getLevel(),
                plugin.getStatManager()
        );

        /*
         * KHÔNG clearModifiers()
         *
         * Equipment modifier phải được giữ nguyên.
         */
    }
}