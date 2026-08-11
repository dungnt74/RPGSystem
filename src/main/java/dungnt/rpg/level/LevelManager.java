package dungnt.rpg.level;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.ClassLevelBonus;
import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class LevelManager {

    private final MyRPG plugin;

    // ==================================================
    // EXP CONFIG
    // ==================================================

    private final double baseExperience = 100.0;

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

        checkLevelUp(
                player,
                data
        );
    }

    // ==================================================
    // CHECK LEVEL UP
    // ==================================================

    private void checkLevelUp(
            Player player,
            PlayerData data
    ) {

        while (true) {

            int currentLevel =
                    data.getLevel();

            double required =
                    getRequiredExperience(
                            currentLevel
                    );

            if (data.getExperience() < required) {
                break;
            }

            // ==================================================
            // REMOVE REQUIRED EXP
            // ==================================================

            data.setExperience(
                    data.getExperience() - required
            );

            // ==================================================
            // LEVEL UP
            // ==================================================

            int newLevel =
                    currentLevel + 1;

            data.setLevel(
                    newLevel
            );

            // ==================================================
            // APPLY CLASS LEVEL BONUS
            // ==================================================

            applyClassBonus(
                    player,
                    data
            );

            // ==================================================
            // MESSAGE
            // ==================================================

            player.sendMessage(
                    "§6§l✦ LEVEL UP!"
            );

            player.sendMessage(
                    "§7Bạn đã đạt Level §e"
                            + newLevel
            );

            if (data.getRpgClass() != null) {

                player.sendMessage(
                        "§7Class: §b"
                                + data.getRpgClass()
                                .getName()
                );

                player.sendMessage(
                        "§a✦ Chỉ số Class đã được tăng!"
                );
            }
        }
    }

    // ==================================================
    // APPLY CLASS BONUS
    // ==================================================

    private void applyClassBonus(
            Player player,
            PlayerData data
    ) {

        if (data.getRpgClass() == null) {
            return;
        }

        ClassLevelBonus.apply(
                player.getUniqueId(),
                data.getRpgClass(),
                data.getLevel(),
                plugin.getStatManager()
        );
    }

    // ==================================================
    // REQUIRED EXP
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
    // REQUIRED EXP - PLAYER
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

        // ==================================================
        // SET LEVEL
        // ==================================================

        data.setLevel(level);

        // Reset EXP
        data.setExperience(0);

        // ==================================================
        // APPLY CLASS BONUS
        // ==================================================

        applyClassBonus(
                player,
                data
        );

        player.sendMessage(
                "§aLevel của bạn đã được đặt thành §e"
                        + level
        );

        player.sendMessage(
                "§a✦ Class level bonus đã được cập nhật."
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
    // GET EXP
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
    // EXP PERCENTAGE
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