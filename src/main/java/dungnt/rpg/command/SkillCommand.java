package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.skills.Skill;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor {

    private final MyRPG plugin;

    public SkillCommand(MyRPG plugin) {
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
                plugin.getPlayerManager().getData(player);

        if (data.getRpgClass() == null) {

            player.sendMessage(
                    ChatColor.RED +
                            "Bạn chưa chọn Class!"
            );

            return true;
        }

        if (args.length == 0) {

            player.sendMessage(
                    ChatColor.YELLOW +
                            "Skill của bạn:"
            );

            for (Skill skill :
                    data.getRpgClass().getSkills()) {

                player.sendMessage(
                        ChatColor.GRAY +
                                "- " +
                                ChatColor.AQUA +
                                skill.getId()
                                +
                                ChatColor.GRAY +
                                " | Mana: "
                                +
                                ChatColor.BLUE +
                                skill.getManaCost()
                                +
                                ChatColor.GRAY +
                                " | CD: "
                                +
                                ChatColor.RED +
                                skill.getCooldown()
                                + "s"
                );
            }

            return true;
        }

        String skillId = args[0];

        plugin.getSkillService()
                .useSkill(player, skillId);

        return true;
    }
}