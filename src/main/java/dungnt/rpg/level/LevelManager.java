package dungnt.rpg.level;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;

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
            return 100.0;
        }

        return 100.0 +
                ((level - 1) * 50.0);
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

        if (amount <= 0) {
            return;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        data.addExperience(amount);

        checkLevelUp(player);
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