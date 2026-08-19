package dungnt.rpg.skills.skills;

import dungnt.rpg.skills.Skill;
import dungnt.rpg.skills.SkillContext;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Dash extends Skill {

    public Dash() {

        super(
                "dash",
                "Dash",
                10,
                3
        );
    }

    @Override
    public void execute(SkillContext context) {

        Player player = context.getPlayer();

        Vector direction =
                player.getLocation()
                        .getDirection()
                        .normalize();

        direction.setY(0.25);

        player.setVelocity(
                direction.multiply(1.5)
        );
    }
}