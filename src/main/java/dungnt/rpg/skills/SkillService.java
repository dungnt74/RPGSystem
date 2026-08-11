package dungnt.rpg.skills;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class SkillService {

    private final MyRPG plugin;

    public SkillService(
            MyRPG plugin
    ) {

        this.plugin = plugin;
    }

    public boolean useSkill(
            Player player,
            String skillId
    ) {

        // ==================================================
        // PLAYER DATA
        // ==================================================

        PlayerData playerData =
                plugin.getPlayerManager()
                        .getData(player);

        if (playerData == null) {

            player.sendMessage(
                    "§cKhông tìm thấy dữ liệu người chơi!"
            );

            return false;
        }

        // ==================================================
        // CLASS
        // ==================================================

        if (playerData.getRpgClass() == null) {

            player.sendMessage(
                    "§cBạn chưa chọn Class!"
            );

            return false;
        }

        // ==================================================
        // GET SKILL
        // ==================================================

        Skill skill =
                playerData.getRpgClass()
                        .getSkill(skillId);

        if (skill == null) {

            player.sendMessage(
                    "§cClass của bạn không có skill này!"
            );

            return false;
        }

        // ==================================================
        // COOLDOWN
        // ==================================================

        if (plugin.getCooldownManager()
                .isOnCooldown(
                        player.getUniqueId(),
                        skill.getId()
                )) {

            long remaining =
                    plugin.getCooldownManager()
                            .getRemaining(
                                    player.getUniqueId(),
                                    skill.getId()
                            );

            double seconds =
                    remaining / 1000.0;

            player.sendMessage(
                    "§cSkill đang cooldown: §e"
                            + String.format(
                            "%.1f",
                            seconds
                    )
                            + "s"
            );

            return false;
        }

        // ==================================================
        // MANA
        // ==================================================

        if (!playerData.useMana(
                skill.getManaCost()
        )) {

            player.sendMessage(
                    "§cBạn không đủ Mana!"
            );

            return false;
        }

        // ==================================================
        // CONTEXT
        // ==================================================

        SkillContext context =
                new SkillContext(
                        plugin,
                        player,
                        playerData,
                        skill,
                        plugin.getCombatService()
                );

        // ==================================================
        // EXECUTE
        // ==================================================

        skill.execute(context);

        // ==================================================
        // COOLDOWN
        // ==================================================

        plugin.getCooldownManager()
                .setCooldown(
                        player.getUniqueId(),
                        skill.getId(),
                        skill.getCooldown()
                );

        return true;
    }
}