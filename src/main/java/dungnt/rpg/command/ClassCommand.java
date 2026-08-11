package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.player.PlayerData;

import org.bukkit.ChatColor;
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

            if (data.getRpgClass() == null) {

                player.sendMessage(
                        "§eBạn chưa chọn Class."
                );

                player.sendMessage(
                        "§7Các Class:"
                );

                player.sendMessage(
                        "§cWarrior §7| §dMage §7| §aArcher §7| §5Assassin"
                );

                player.sendMessage(
                        "§7Dùng: §e/class choose <class>"
                );

                return true;
            }

            RPGClass rpgClass =
                    data.getRpgClass();

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
                    "§8§m--------------------------"
            );

            return true;
        }

        // ==================================================
        // /CLASS LIST
        // ==================================================

        if (args[0].equalsIgnoreCase("list")) {

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
            }

            player.sendMessage(
                    "§8§m--------------------------"
            );

            return true;
        }

        // ==================================================
        // /CLASS CHOOSE
        // ==================================================

        if (args[0].equalsIgnoreCase("choose")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/class choose <warrior|mage|archer|assassin>"
                );

                return true;
            }

            RPGClass rpgClass =
                    plugin.getClassManager()
                            .getClass(args[1]);

            if (rpgClass == null) {

                player.sendMessage(
                        "§cClass không tồn tại!"
                );

                player.sendMessage(
                        "§7Warrior, Mage, Archer, Assassin"
                );

                return true;
            }

            // ==================================================
            // SET CLASS
            // ==================================================

            plugin.getPlayerManager()
                    .setClass(
                            player,
                            rpgClass
                    );

            // ==================================================
            // MESSAGE
            // ==================================================

            player.sendMessage(
                    "§a§l✔ CLASS SELECTED"
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
                    "§a✦ Class Stats đã được áp dụng."
            );

            player.sendMessage(
                    "§a✦ Level Growth đã được áp dụng."
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        player.sendMessage(
                "§cDùng:"
        );

        player.sendMessage(
                "§7/class"
        );

        player.sendMessage(
                "§7/class list"
        );

        player.sendMessage(
                "§7/class choose <warrior|mage|archer|assassin>"
        );

        return true;
    }
}