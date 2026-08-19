package dungnt.rpg.level;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.stats.ModifierSource;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.entity.Player;

public class LevelManager {

    private final MyRPG plugin;

    public LevelManager(MyRPG plugin) {
        this.plugin = plugin;
    }

    // ==================================================
    // REQUIRED EXP
    // ==================================================

    public double getRequiredExperience(int level) {

        if (level <= 1) {
            return 200.0;
        }

        return ((level * level + level) * 200.0);
    }

    public double getRequiredExperience(
            Player player
    ) {

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        return getRequiredExperience(
                data.getLevel()
        );
    }

    // ==================================================
    // EXP %
    // ==================================================

    public double getExperiencePercentage(
            Player player
    ) {

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        double required =
                getRequiredExperience(
                        data.getLevel()
                );

        if (required <= 0) {
            return 100.0;
        }

        double percentage =
                (data.getExperience() /
                        required) * 100.0;

        return Math.max(
                0.0,
                Math.min(
                        100.0,
                        percentage
                )
        );
    }

    // ==================================================
    // ADD EXP
    // ==================================================

    public void addExperience(
            Player player,
            double amount
    ) {

        if (player == null || amount <= 0) {
            return;
        }

        /*
         * EXP received from gameplay is multiplied by:
         *
         * (1 + rankBonus) * (1 + equipmentAndBuffBonus)
         *
         * Example:
         * rank = 1.0 (100%)
         * ring = 0.2 (20%)
         * belt = 0.1 (10%)
         * potion = 0.5 (50%)
         *
         * => (1 + 1.0) * (1 + 0.2 + 0.1 + 0.5)
         * => 3.6x
         */
        double multiplier =
                getExpMultiplier(player);

        addRawExperience(
                player,
                amount * multiplier
        );
    }

    /**
     * Adds EXP without applying EXP_BONUS.
     * Used by administrative commands so /level exp <player> <amount>
     * means exactly the amount entered by the administrator.
     */
    public void addRawExperience(
            Player player,
            double amount
    ) {

        if (player == null || amount <= 0) {
            return;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        data.addExperience(amount);

        checkLevelUp(player);
    }

    // ==================================================
    // EXP BONUS
    // ==================================================

    /**
     * Returns the final EXP multiplier.
     *
     * Rank:
     *   rpg.expbonus.<amount>
     *
     * uses the highest numeric permission the player has.
     *
     * Equipment/buff values are stored as multiplier additions:
     *   0.20 = +20%
     *   0.50 = +50%
     *
     * Formula:
     *   (1 + rankBonus) * (1 + equipmentAndBuffBonus)
     */
    public double getExpMultiplier(
            Player player
    ) {

        if (player == null) {
            return 1.0;
        }

        double rankBonus =
                getRankExpBonus(player);

        double equipmentAndBuffBonus = 0.0;

        StatManager statManager =
                plugin.getStatManager();

        for (StatModifier modifier :
                statManager.getModifiers(
                        player.getUniqueId()
                ).values()) {

            if (modifier == null
                    || modifier.getType() != StatType.EXP_BONUS
                    || modifier.getModifierType() != ModifierType.FLAT) {
                continue;
            }

            ModifierSource source =
                    modifier.getSource();

            if (source == ModifierSource.EQUIPMENT
                    || source == ModifierSource.BUFF) {

                equipmentAndBuffBonus +=
                        modifier.getAmount();
            }
        }

        double base =
                1.0 + Math.max(0.0, rankBonus);

        double sources =
                1.0 + Math.max(
                        0.0,
                        equipmentAndBuffBonus
                );

        return Math.max(
                1.0,
                base * sources
        );
    }

    /**
     * Finds the highest exact permission:
     *   rpg.expbonus.<amount>
     *
     * Example:
     *   rpg.expbonus.0.5
     *   rpg.expbonus.1
     *
     * If both exist, 1.0 wins.
     */
    private double getRankExpBonus(
            Player player
    ) {

        double highest =
                0.0;

        final String prefix =
                "rpg.expbonus.";

        for (org.bukkit.permissions.PermissionAttachmentInfo permission :
                player.getEffectivePermissions()) {

            if (permission == null
                    || !permission.getValue()) {
                continue;
            }

            String node =
                    permission.getPermission();

            if (node == null
                    || !node.toLowerCase(java.util.Locale.ROOT)
                    .startsWith(prefix)) {
                continue;
            }

            String amountText =
                    node.substring(prefix.length());

            try {
                double amount =
                        Double.parseDouble(amountText);

                if (Double.isFinite(amount)
                        && amount >= 0.0) {

                    highest =
                            Math.max(
                                    highest,
                                    amount
                            );
                }
            } catch (NumberFormatException ignored) {
                // Ignore nodes such as rpg.expbonus.*.
            }
        }

        return highest;
    }

    // ==================================================
    // CHECK LEVEL UP
    // ==================================================

    private void checkLevelUp(
            Player player
    ) {

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        while (
                data.getExperience()
                        >= getRequiredExperience(
                        data.getLevel()
                )
        ) {

            double required =
                    getRequiredExperience(
                            data.getLevel()
                    );

            data.setExperience(
                    data.getExperience()
                            - required
            );

            levelUp(player);
        }
    }

    // ==================================================
    // LEVEL UP
    // ==================================================

    private void levelUp(
            Player player
    ) {

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        int newLevel =
                data.getLevel() + 1;

        data.setLevel(newLevel);

        // Refresh toàn bộ:
        // Class + Class Growth
        plugin.getPlayerManager()
                .refreshStats(player);

        player.sendMessage(
                "§6§l✦ LEVEL UP!"
        );

        player.sendMessage(
                "§7Bạn đã đạt Level §e"
                        + newLevel
                        + "§7!"
        );

        if (data.getRpgClass() != null) {

            player.sendMessage(
                    "§a✦ Class Growth đã được cập nhật."
            );
        }
    }

    // ==================================================
    // SET LEVEL
    // ==================================================

    public void setLevel(
            Player player,
            int level
    ) {

        level =
                Math.max(
                        1,
                        level
                );

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        data.setLevel(level);
        data.setExperience(0);

        plugin.getPlayerManager()
                .refreshStats(player);
    }
}