package dungnt.rpg.skills.skills;

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

        Player player = context.getPlayer();

        RayTraceResult result = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                player.getEyeLocation().getDirection(),
                4,
                entity -> entity instanceof LivingEntity
                        && entity != player
        );

        if (result == null) {
            return;
        }

        if (!(result.getHitEntity() instanceof LivingEntity target)) {
            return;
        }

        target.damage(10, player);
    }
}