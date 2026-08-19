package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.level.LevelManager;
import dungnt.rpg.player.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LevelCommand implements TabExecutor {

    private final MyRPG plugin;
    private final LevelManager levelManager;

    public LevelCommand(MyRPG plugin) {

        this.plugin = plugin;

        this.levelManager =
                plugin.getLevelManager();
    }

    // ==================================================
    // COMMAND
    // ==================================================

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // ==================================================
        // /LEVEL
        // ==================================================

        if (args.length == 0) {

            // Console không thể xem level của chính mình
            if (!(sender instanceof Player player)) {

                sender.sendMessage(
                        "§cConsole cần chỉ định player."
                );

                return true;
            }

            PlayerData data =
                    plugin.getPlayerManager()
                            .getData(player);

            sendLevelInfo(
                    player,
                    data
            );

            return true;
        }

        // ==================================================
        // /LEVEL EXP <PLAYER> <AMOUNT>
        // ==================================================

        if (args[0].equalsIgnoreCase("exp")) {

            // Kiểm tra đủ argument
            if (args.length < 3) {

                sender.sendMessage(
                        "§cDùng: §e/level exp <player> <amount>"
                );

                return true;
            }

            // ------------------------------------------
            // Tìm player
            // ------------------------------------------

            Player target =
                    plugin.getServer()
                            .getPlayerExact(args[1]);

            if (target == null) {

                sender.sendMessage(
                        "§cKhông tìm thấy player: §e"
                                + args[1]
                );

                return true;
            }

            // ------------------------------------------
            // Parse EXP
            // ------------------------------------------

            double amount;

            try {

                amount =
                        Double.parseDouble(
                                args[2]
                        );

            } catch (NumberFormatException exception) {

                sender.sendMessage(
                        "§cEXP phải là một số."
                );

                return true;
            }

            // ------------------------------------------
            // Kiểm tra EXP
            // ------------------------------------------

            if (amount <= 0) {

                sender.sendMessage(
                        "§cEXP phải lớn hơn 0."
                );

                return true;
            }

            // ------------------------------------------
            // Add EXP
            // ------------------------------------------

            double multiplier =
                    levelManager.getExpMultiplier(target);

            double finalAmount =
                    amount * multiplier;

            levelManager.addExperience(
                    target,
                    amount
            );

            sender.sendMessage(
                    "§aĐã thêm §e"
                            + formatNumber(amount)
                            + " EXP §acơ bản cho §e"
                            + target.getName()
                            + "§a."
            );

            sender.sendMessage(
                    "§7EXP Bonus: §e×"
                            + String.format("%.2f", multiplier)
                            + " §7→ Thực nhận: §a"
                            + formatNumber(finalAmount)
                            + " EXP"
            );

            return true;
        }

        // ==================================================
        // /LEVEL SET <PLAYER> <LEVEL>
        // ==================================================

        if (args[0].equalsIgnoreCase("set")) {

            // Kiểm tra đủ argument
            if (args.length < 3) {

                sender.sendMessage(
                        "§cDùng: §e/level set <player> <level>"
                );

                return true;
            }

            // ------------------------------------------
            // Tìm player
            // ------------------------------------------

            Player target =
                    plugin.getServer()
                            .getPlayerExact(args[1]);

            if (target == null) {

                sender.sendMessage(
                        "§cKhông tìm thấy player: §e"
                                + args[1]
                );

                return true;
            }

            // ------------------------------------------
            // Parse level
            // ------------------------------------------

            int level;

            try {

                level =
                        Integer.parseInt(
                                args[2]
                        );

            } catch (NumberFormatException exception) {

                sender.sendMessage(
                        "§cLevel phải là số nguyên."
                );

                return true;
            }

            // ------------------------------------------
            // Kiểm tra level
            // ------------------------------------------

            if (level < 1) {

                sender.sendMessage(
                        "§cLevel tối thiểu là 1."
                );

                return true;
            }

            // ------------------------------------------
            // Set level
            // ------------------------------------------

            levelManager.setLevel(
                    target,
                    level
            );

            sender.sendMessage(
                    "§aĐã đặt level của §e"
                            + target.getName()
                            + " §athành §e"
                            + level
                            + "§a."
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        sender.sendMessage(
                "§cLệnh không hợp lệ."
        );

        sender.sendMessage(
                "§7/level"
        );

        sender.sendMessage(
                "§7/level exp <player> <amount>"
        );

        sender.sendMessage(
                "§7/level set <player> <level>"
        );

        return true;
    }

    // ==================================================
    // TAB COMPLETE
    // ==================================================

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        List<String> suggestions =
                new ArrayList<>();

        // ==================================================
        // /level <TAB>
        // ==================================================

        if (args.length == 1) {

            suggestions.add("exp");
            suggestions.add("set");

            return filterSuggestions(
                    suggestions,
                    args[0]
            );
        }

        // ==================================================
        // /level exp <PLAYER> <TAB>
        // /level set <PLAYER> <TAB>
        // ==================================================

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("exp")
                    || args[0].equalsIgnoreCase("set")) {

                for (Player player :
                        plugin.getServer()
                                .getOnlinePlayers()) {

                    suggestions.add(
                            player.getName()
                    );
                }

                return filterSuggestions(
                        suggestions,
                        args[1]
                );
            }
        }

        // ==================================================
        // /level exp <PLAYER> <AMOUNT> <TAB>
        // ==================================================

        if (args.length == 3) {

            if (args[0].equalsIgnoreCase("exp")) {

                suggestions.add("100");
                suggestions.add("500");
                suggestions.add("1000");
                suggestions.add("5000");
                suggestions.add("10000");

            }

            // ==================================================
            // /level set <PLAYER> <LEVEL> <TAB>
            // ==================================================

            else if (args[0].equalsIgnoreCase("set")) {

                suggestions.add("10");
                suggestions.add("20");
                suggestions.add("50");
                suggestions.add("100");
                suggestions.add("200");
            }

            return filterSuggestions(
                    suggestions,
                    args[2]
            );
        }

        return suggestions;
    }

    // ==================================================
    // FILTER TAB COMPLETE
    // ==================================================

    private List<String> filterSuggestions(
            List<String> suggestions,
            String input
    ) {

        List<String> result =
                new ArrayList<>();

        String lowerInput =
                input.toLowerCase();

        for (String suggestion :
                suggestions) {

            if (suggestion
                    .toLowerCase()
                    .startsWith(lowerInput)) {

                result.add(suggestion);
            }
        }

        return result;
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

    // ==================================================
    // FORMAT NUMBER
    // ==================================================

    private String formatNumber(
            double number
    ) {

        if (number == Math.floor(number)) {

            return String.valueOf(
                    (long) number
            );
        }

        return String.format(
                "%.1f",
                number
        );
    }
}