package dungnt.rpg.skills;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class SkillService {

    private final MyRPG plugin;

    public SkillService(MyRPG plugin) {
        this.plugin = plugin;
    }

    public boolean useSkill(Player player, String skillId) {

        PlayerData playerData =
                plugin.getPlayerManager().getData(player);

        // 1. Kiểm tra Class
        if (playerData.getRpgClass() == null) {

            player.sendMessage(
                    "§cBạn chưa chọn Class!"
            );

            return false;
        }

        // 2. Lấy skill từ Class
        Skill skill =
                playerData.getRpgClass().getSkill(skillId);

        if (skill == null) {

            player.sendMessage(
                    "§cClass của bạn không có skill này!"
            );

            return false;
        }

        // 3. Kiểm tra cooldown
        if (plugin.getCooldownManager().isOnCooldown(
                player.getUniqueId(),
                skill.getId()
        )) {

            long remaining =
                    plugin.getCooldownManager().getRemaining(
                            player.getUniqueId(),
                            skill.getId()
                    );

            double seconds =
                    remaining / 1000.0;

            player.sendMessage(
                    "§cSkill đang cooldown: §e"
                            + String.format("%.1f", seconds)
                            + "s"
            );

            return false;
        }

        // 4. Kiểm tra Mana
        if (!playerData.useMana(skill.getManaCost())) {

            player.sendMessage(
                    "§cBạn không đủ Mana!"
            );

            return false;
        }

        // 5. Tạo Context
        SkillContext context =
                new SkillContext(
                        player,
                        playerData,
                        skill
                );

        // 6. Execute
        skill.execute(context);

        // 7. Bắt đầu cooldown
        plugin.getCooldownManager().setCooldown(
                player.getUniqueId(),
                skill.getId(),
                skill.getCooldown()
        );

        return true;
    }
}