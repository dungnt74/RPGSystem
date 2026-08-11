package dungnt.rpg.level;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class LevelManager {

    private final MyRPG plugin;

    // ==================================================
    // CONFIG
    // ==================================================

    // EXP cần ở Level 1 -> Level 2
    private final double baseExperience = 100.0;

    // Mỗi level tăng thêm 25%
    private final double experienceMultiplier = 1.25;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public LevelManager(MyRPG plugin) {
        this.plugin = plugin;
    }

    // ==================================================
    // ADD EXPERIENCE
    // ==================================================

    public void addExperience(
            Player player,
            double amount
    ) {

        if (player == null || amount <= 0) {
            return;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return;
        }

        data.addExperience(amount);

        checkLevelUp(player, data);
    }

    // ==================================================
    // CHECK LEVEL UP
    // ==================================================

    private void checkLevelUp(
            Player player,
            PlayerData data
    ) {

        while (true) {

            int level =
                    data.getLevel();

            double required =
                    getRequiredExperience(level);

            if (data.getExperience() < required) {
                break;
            }

            // Trừ EXP cần thiết
            data.setExperience(
                    data.getExperience() - required
            );

            // Tăng level
            int newLevel =
                    level + 1;

            data.setLevel(newLevel);

            // ==========================================
            // LEVEL UP
            // ==========================================

            player.sendMessage(
                    "§6§l✦ LEVEL UP!"
            );

            player.sendMessage(
                    "§7Bạn đã đạt Level §e"
                            + newLevel
            );

            // ==========================================
            // SAU NÀY SẼ THÊM:
            //
            // - Class level bonus
            // - Stat bonus
            // - Skill unlock
            // - Equipment update
            // ==========================================
        }
    }

    // ==================================================
    // GET REQUIRED EXP
    // ==================================================

    public double getRequiredExperience(
            int level
    ) {

        if (level < 1) {
            level = 1;
        }

        return baseExperience
                * Math.pow(
                experienceMultiplier,
                level - 1
        );
    }

    // ==================================================
    // GET REQUIRED EXP FROM PLAYER
    // ==================================================

    public double getRequiredExperience(
            Player player
    ) {

        if (player == null) {
            return baseExperience;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return baseExperience;
        }

        return getRequiredExperience(
                data.getLevel()
        );
    }

    // ==================================================
    // SET LEVEL
    // ==================================================

    public void setLevel(
            Player player,
            int level
    ) {

        if (player == null) {
            return;
        }

        if (level < 1) {
            level = 1;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return;
        }

        data.setLevel(level);

        // Reset EXP khi set level
        data.setExperience(0);

        player.sendMessage(
                "§aLevel của bạn đã được đặt thành §e"
                        + level
        );
    }

    // ==================================================
    // GET LEVEL
    // ==================================================

    public int getLevel(
            Player player
    ) {

        if (player == null) {
            return 1;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return 1;
        }

        return data.getLevel();
    }

    // ==================================================
    // GET CURRENT EXP
    // ==================================================

    public double getExperience(
            Player player
    ) {

        if (player == null) {
            return 0;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return 0;
        }

        return data.getExperience();
    }

    // ==================================================
    // GET EXP %
    // ==================================================

    public double getExperiencePercentage(
            Player player
    ) {

        double required =
                getRequiredExperience(player);

        if (required <= 0) {
            return 100.0;
        }

        double current =
                getExperience(player);

        return Math.min(
                100.0,
                (current / required) * 100.0
        );
    }
}