package dungnt.rpg.skills.skills;

import dungnt.rpg.skills.Skill;
import dungnt.rpg.skills.SkillContext;
import org.bukkit.entity.Player;

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
    public void execute(SkillContext context) {

        Player player = context.getPlayer();

        player.launchProjectile(
                org.bukkit.entity.Fireball.class
        );
    }
}