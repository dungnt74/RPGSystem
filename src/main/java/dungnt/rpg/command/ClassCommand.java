package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.stats.StatModifier;
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
                    "Chỉ người chơi mới sử dụng được command này."
            );

            return true;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {

            player.sendMessage(
                    ChatColor.RED +
                            "Không tìm thấy dữ liệu người chơi."
            );

            return true;
        }

        // =========================
        // /class
        // =========================

        if (args.length == 0) {

            if (data.getRpgClass() == null) {

                player.sendMessage(
                        ChatColor.YELLOW +
                                "Bạn chưa chọn Class."
                );

                player.sendMessage(
                        ChatColor.GRAY +
                                "Sử dụng: /class choose <warrior/mage/archer>"
                );

            } else {

                player.sendMessage(
                        ChatColor.GREEN +
                                "Class hiện tại: " +
                                ChatColor.GOLD +
                                data.getRpgClass().getName()
                );
            }

            return true;
        }

        // =========================
        // /class choose <class>
        // =========================

        if (args[0].equalsIgnoreCase("choose")) {

            if (args.length < 2) {

                player.sendMessage(
                        ChatColor.RED +
                                "Sử dụng: /class choose <class>"
                );

                return true;
            }

            String classId = args[1];

            RPGClass rpgClass =
                    plugin.getClassManager()
                            .getClass(classId);

            if (rpgClass == null) {

                player.sendMessage(
                        ChatColor.RED +
                                "Class không tồn tại!"
                );

                return true;
            }

            // =========================
            // XÓA MODIFIER CLASS CŨ
            // =========================

            plugin.getStatManager()
                    .clearModifiers(
                            player.getUniqueId()
                    );

            // =========================
            // SET CLASS MỚI
            // =========================

            plugin.getPlayerManager()
                    .setClass(
                            player,
                            rpgClass
                    );
            // =========================
            // APPLY MODIFIER CLASS
            // =========================

            for (StatModifier modifier :
                    rpgClass.getStatModifiers()) {

                plugin.getStatManager()
                        .addModifier(
                                player.getUniqueId(),
                                modifier
                        );
            }

            // =========================
            // THÔNG BÁO
            // =========================

            player.sendMessage(
                    ChatColor.GREEN +
                            "Bạn đã chọn Class: " +
                            ChatColor.GOLD +
                            rpgClass.getName()
            );

            player.sendMessage(
                    ChatColor.GRAY +
                            "Đã áp dụng " +
                            rpgClass.getStatModifiers().size() +
                            " Stat Modifier."
            );

            return true;
        }

        player.sendMessage(
                ChatColor.RED +
                        "Sử dụng: /class hoặc /class choose <class>"
        );

        return true;
    }
}