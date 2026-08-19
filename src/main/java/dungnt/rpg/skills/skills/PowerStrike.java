package dungnt.rpg.skills.skills;

import dungnt.rpg.combat.DamageResult;
import dungnt.rpg.skills.Skill;
import dungnt.rpg.skills.SkillContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public class PowerStrike extends Skill {

    public PowerStrike() {

        super(
                "power_strike",
                "Power Strike",
                15,
                4
        );
    }

    @Override
    public void execute(SkillContext context) {

        Player player =
                context.getPlayer();

        RayTraceResult result =
                player.getWorld().rayTraceEntities(
                        player.getEyeLocation(),
                        player.getEyeLocation().getDirection(),
                        4,
                        entity ->
                                entity instanceof LivingEntity
                                        && entity != player
                );

        if (result == null) {

            player.sendMessage(
                    "§cKhông có mục tiêu!"
            );

            return;
        }

        if (!(result.getHitEntity()
                instanceof LivingEntity target)) {

            return;
        }

        // =========================
        // POWER STRIKE
        // =========================

        DamageResult damageResult =
                context.damage(
                        target,
                        1.5
                );

        String criticalText =
                damageResult.isCritical()
                        ? " §6✦ CRITICAL!"
                        : "";

        player.sendMessage(
                "§6⚔ Power Strike §f→ §c"
                        + String.format(
                        "%.1f",
                        damageResult.getDamage()
                )
                        + " Damage"
                        + criticalText
        );
    }
}