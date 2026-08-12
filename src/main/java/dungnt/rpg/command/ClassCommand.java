package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.player.PlayerData;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClassCommand implements CommandExecutor {

    private final MyRPG plugin;

    public ClassCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // ==================================================
        // PLAYER ONLY
        // ==================================================

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "§cChỉ Player mới sử dụng được lệnh này."
            );

            return true;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        // ==================================================
        // /CLASS
        // ==================================================

        if (args.length == 0) {

            showCurrentClass(
                    player,
                    data
            );

            return true;
        }

        // ==================================================
        // /CLASS LIST
        // ==================================================

        if (args[0].equalsIgnoreCase("list")) {

            showClassList(player);

            return true;
        }

        // ==================================================
        // /CLASS CHOOSE <CLASS>
        // ==================================================

        if (args[0].equalsIgnoreCase("choose")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/class choose <warrior|mage|archer|assassin>"
                );

                return true;
            }

            String classId =
                    args[1].toLowerCase();

            RPGClass rpgClass =
                    plugin.getClassManager()
                            .getClass(classId);

            // ==================================================
            // CLASS NOT FOUND
            // ==================================================

            if (rpgClass == null) {

                player.sendMessage(
                        "§cClass không tồn tại!"
                );

                player.sendMessage(
                        "§7Class: §ewarrior §7| §dmage §7| §aarcher §7| §5assassin"
                );

                return true;
            }

            // ==================================================
            // SAME CLASS
            // ==================================================

            if (data.getRpgClass() != null
                    && data.getRpgClass()
                    .getId()
                    .equalsIgnoreCase(
                            rpgClass.getId()
                    )) {

                player.sendMessage(
                        "§eBạn đang sử dụng Class này rồi."
                );

                return true;
            }

            // ==================================================
            // CHANGE CLASS
            // ==================================================

            RPGClass oldClass =
                    data.getRpgClass();

            plugin.getPlayerManager()
                    .setClass(
                            player,
                            rpgClass
                    );

            // ==================================================
            // MESSAGE
            // ==================================================

            player.sendMessage(
                    "§8§m--------------------------"
            );

            player.sendMessage(
                    "§a§l✔ CLASS SELECTED"
            );

            if (oldClass != null) {

                player.sendMessage(
                        "§7Class cũ: §c"
                                + oldClass.getName()
                );
            }

            player.sendMessage(
                    "§7Class mới: §e"
                            + rpgClass.getName()
            );

            player.sendMessage(
                    "§7"
                            + rpgClass.getDescription()
            );

            player.sendMessage(
                    "§a✦ Class Stats đã được áp dụng."
            );

            player.sendMessage(
                    "§a✦ Level Stats vẫn được giữ."
            );

            player.sendMessage(
                    "§8§m--------------------------"
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        sendHelp(player);

        return true;
    }

    // ==================================================
    // CURRENT CLASS
    // ==================================================

    private void showCurrentClass(
            Player player,
            PlayerData data
    ) {

        RPGClass rpgClass =
                data.getRpgClass();

        if (rpgClass == null) {

            player.sendMessage(
                    "§8§m--------------------------"
            );

            player.sendMessage(
                    "§e§l✦ CLASS"
            );

            player.sendMessage(
                    "§7Bạn chưa chọn Class."
            );

            player.sendMessage(
                    "§7Dùng: §e/class list"
            );

            player.sendMessage(
                    "§7Sau đó: §e/class choose <class>"
            );

            player.sendMessage(
                    "§8§m--------------------------"
            );

            return;
        }

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§l✦ CLASS"
        );

        player.sendMessage(
                "§7Class: §e"
                        + rpgClass.getName()
        );

        player.sendMessage(
                "§7"
                        + rpgClass.getDescription()
        );

        player.sendMessage(
                "§7Skills: §f"
                        + rpgClass.getSkills().size()
        );

        player.sendMessage(
                "§7Class Stats: §f"
                        + rpgClass.getStatModifiers().size()
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }

    // ==================================================
    // CLASS LIST
    // ==================================================

    private void showClassList(
            Player player
    ) {

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§l✦ RPG CLASSES"
        );

        for (RPGClass rpgClass :
                plugin.getClassManager()
                        .getClasses()) {

            player.sendMessage(
                    "§e"
                            + rpgClass.getId()
                            + " §7- §f"
                            + rpgClass.getName()
            );

            player.sendMessage(
                    "  §7"
                            + rpgClass.getDescription()
            );
        }

        player.sendMessage(
                "§8§m--------------------------"
        );
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
                "§6§l✦ CLASS COMMAND"
        );

        player.sendMessage(
                "§7/class"
        );

        player.sendMessage(
                "§7/class list"
        );

        player.sendMessage(
                "§7/class choose <class>"
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}