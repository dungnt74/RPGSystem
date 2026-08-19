package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ItemStatCommand
        implements CommandExecutor {

    private final MyRPG plugin;

    public ItemStatCommand(
            MyRPG plugin
    ) {
        this.plugin = plugin;
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
                    "§cChỉ Player mới sử dụng được."
            );

            return true;
        }

        ItemStack item =
                player.getInventory()
                        .getItemInMainHand();

        if (item == null ||
                item.getType().isAir()) {

            player.sendMessage(
                    "§cHãy cầm item cần chỉnh stat trên tay."
            );

            return true;
        }

        if (args.length == 0) {

            sendHelp(player);
            return true;
        }

        // ==================================================
        // ADD
        // ==================================================

        if (args[0].equalsIgnoreCase("add")) {

            if (args.length < 3) {

                player.sendMessage(
                        "§cDùng: §e/itemstat add <stat> <amount> [flat|percent]"
                );

                return true;
            }

            StatType statType =
                    parseStat(args[1]);

            if (statType == null) {

                player.sendMessage(
                        "§cStat không hợp lệ: §e"
                                + args[1]
                );

                return true;
            }

            double amount;

            try {

                amount =
                        Double.parseDouble(args[2]);

            } catch (NumberFormatException exception) {

                player.sendMessage(
                        "§cAmount phải là số."
                );

                return true;
            }

            ModifierType modifierType =
                    ModifierType.FLAT;

            if (args.length >= 4) {

                try {

                    modifierType =
                            ModifierType.valueOf(
                                    args[3]
                                            .toUpperCase(Locale.ROOT)
                            );

                } catch (IllegalArgumentException exception) {

                    player.sendMessage(
                            "§cModifier phải là §eflat §choặc §epercent."
                    );

                    return true;
                }
            }

            boolean success =
                    plugin.getItemManager()
                            .addStat(
                                    item,
                                    statType,
                                    modifierType,
                                    amount
                            );

            if (!success) {

                player.sendMessage(
                        "§cKhông thể thêm stat."
                );

                return true;
            }

            player.getInventory()
                    .setItemInMainHand(item);

            player.sendMessage(
                    "§aĐã thêm stat §e"
                            + statType.name()
                            + " §a= §e"
                            + amount
                            + " "
                            + modifierType.name()
            );

            player.sendMessage(
                    "§7Effect được lưu bằng PDC, lore chỉ là hiển thị."
            );

            return true;
        }

        // ==================================================
        // REMOVE
        // ==================================================

        if (args[0].equalsIgnoreCase("remove")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/itemstat remove <stat>"
                );

                return true;
            }

            StatType statType =
                    parseStat(args[1]);

            if (statType == null) {

                player.sendMessage(
                        "§cStat không hợp lệ: §e"
                                + args[1]
                );

                return true;
            }

            boolean existed =
                    plugin.getItemManager()
                            .removeStat(
                                    item,
                                    statType
                            );

            player.getInventory()
                    .setItemInMainHand(item);

            if (!existed) {

                player.sendMessage(
                        "§eItem không có stat §f"
                                + statType.name()
                );

                return true;
            }

            player.sendMessage(
                    "§aĐã xoá stat §e"
                            + statType.name()
                            + " §akhỏi item."
            );

            return true;
        }

        // ==================================================
        // CLEAR
        // ==================================================

        if (args[0].equalsIgnoreCase("clear")) {

            plugin.getItemManager()
                    .clearStats(item);

            player.getInventory()
                    .setItemInMainHand(item);

            player.sendMessage(
                    "§aĐã xoá toàn bộ stat của item."
            );

            return true;
        }

        sendHelp(player);
        return true;
    }

    // ==================================================
    // PARSE STAT
    // ==================================================

    private StatType parseStat(
            String value
    ) {

        if (value == null) {
            return null;
        }

        try {

            return StatType.valueOf(
                    value
                            .toUpperCase(Locale.ROOT)
                            .replace("-", "_")
            );

        } catch (IllegalArgumentException exception) {

            return null;
        }
    }

    // ==================================================
    // HELP
    // ==================================================

    private void sendHelp(
            Player player
    ) {

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§lITEM STAT"
        );

        player.sendMessage(
                "§7/itemstat add <stat> <amount> [flat|percent]"
        );

        player.sendMessage(
                "§7/itemstat remove <stat>"
        );

        player.sendMessage(
                "§7/itemstat clear"
        );

        player.sendMessage(
                "§8Ví dụ: §e/itemstat add attack 10"
        );

        player.sendMessage(
                "§8Ví dụ: §e/itemstat add crit_chance 5 percent"
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}
