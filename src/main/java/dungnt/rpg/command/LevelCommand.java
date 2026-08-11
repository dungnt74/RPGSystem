package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.level.LevelManager;
import dungnt.rpg.player.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    private final MyRPG plugin;
    private final LevelManager levelManager;

    public LevelCommand(MyRPG plugin) {

        this.plugin = plugin;

        this.levelManager =
                plugin.getLevelManager();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "§cLệnh này chỉ dành cho Player."
            );

            return true;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        // ==================================================
        // /LEVEL
        // ==================================================

        if (args.length == 0) {

            sendLevelInfo(
                    player,
                    data
            );

            return true;
        }

        // ==================================================
        // /LEVEL EXP <AMOUNT>
        // ==================================================

        if (args[0].equalsIgnoreCase("exp")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/level exp <amount>"
                );

                return true;
            }

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                args[1]
                        );

            } catch (NumberFormatException exception) {

                player.sendMessage(
                        "§cEXP phải là một số."
                );

                return true;
            }

            if (amount <= 0) {

                player.sendMessage(
                        "§cEXP phải lớn hơn 0."
                );

                return true;
            }

            levelManager.addExperience(
                    player,
                    amount
            );

            return true;
        }

        // ==================================================
        // /LEVEL SET <LEVEL>
        // ==================================================

        if (args[0].equalsIgnoreCase("set")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/level set <level>"
                );

                return true;
            }

            int level;

            try {

                level =
                        Integer.parseInt(
                                args[1]
                        );

            } catch (NumberFormatException exception) {

                player.sendMessage(
                        "§cLevel phải là số nguyên."
                );

                return true;
            }

            if (level < 1) {

                player.sendMessage(
                        "§cLevel tối thiểu là 1."
                );

                return true;
            }

            levelManager.setLevel(
                    player,
                    level
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        player.sendMessage(
                "§cLệnh không hợp lệ."
        );

        player.sendMessage(
                "§7/level"
        );

        player.sendMessage(
                "§7/level exp <amount>"
        );

        player.sendMessage(
                "§7/level set <level>"
        );

        return true;
    }

    // ==================================================
    // LEVEL INFO
    // ==================================================

    private void sendLevelInfo(
            Player player,
            PlayerData data
    ) {

        int level =
                data.getLevel();

        double currentExp =
                data.getExperience();

        double requiredExp =
                levelManager.getRequiredExperience(
                        player
                );

        double percentage =
                levelManager.getExperiencePercentage(
                        player
                );

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§l✦ LEVEL"
        );

        player.sendMessage(
                "§7Level: §e"
                        + level
        );

        player.sendMessage(
                "§7EXP: §a"
                        + String.format(
                        "%.1f",
                        currentExp
                )
                        + " §7/ §a"
                        + String.format(
                        "%.1f",
                        requiredExp
                )
        );

        player.sendMessage(
                "§7Progress: §b"
                        + String.format(
                        "%.1f",
                        percentage
                )
                        + "%"
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}