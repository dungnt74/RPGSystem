package dungnt.rpg.skills.skills;

import dungnt.rpg.skills.Skill;
import dungnt.rpg.skills.SkillContext;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Fireball extends Skill {

    public Fireball() {

        super(
                "fireball",
                "Fireball",
                20,
                5
        );
    }

    @Override
    public void execute(
            SkillContext context
    ) {

        Player player =
                context.getPlayer();

        // ==================================================
        // TẠO FIREBALL VANILLA
        // ==================================================

        org.bukkit.entity.Fireball fireball =
                player.launchProjectile(
                        org.bukkit.entity.Fireball.class
                );

        // ==================================================
        // HƯỚNG BAY
        // ==================================================

        Vector direction =
                player.getEyeLocation()
                        .getDirection()
                        .normalize();

        fireball.setDirection(direction);

        // ==================================================
        // FIREBALL RPG
        // ==================================================
        //
        // Không dùng:
        // setDamage()
        // PersistentDataContainer
        // Metadata
        //
        // Nhận diện bằng UUID thông qua FireballListener.
        // ==================================================

//        context.getPlugin()
//                .getFireballListener()
//                .registerFireball(fireball);

        // ==================================================
        // FIREBALL KHÔNG ĐỐT BLOCK
        // ==================================================

        fireball.setIsIncendiary(false);

        // ==================================================
        // KHÔNG NỔ BLOCK
        // ==================================================

        fireball.setYield(0);
    }
}