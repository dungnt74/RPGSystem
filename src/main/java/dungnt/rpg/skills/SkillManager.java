package dungnt.rpg.skills;

import dungnt.rpg.skills.skills.Dash;
import dungnt.rpg.skills.skills.Fireball;
import dungnt.rpg.skills.skills.PowerStrike;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SkillManager {

    private final Map<String, Skill> skills = new HashMap<>();

    public SkillManager() {
        registerDefaultSkills();
    }

    private void registerDefaultSkills() {

        register(new Fireball());
        register(new PowerStrike());
        register(new Dash());
    }

    public void register(Skill skill) {

        skills.put(
                skill.getId().toLowerCase(),
                skill
        );
    }

    public Skill getSkill(String id) {

        if (id == null) {
            return null;
        }

        return skills.get(
                id.toLowerCase()
        );
    }

    public boolean exists(String id) {

        if (id == null) {
            return false;
        }

        return skills.containsKey(
                id.toLowerCase()
        );
    }

    public Collection<Skill> getSkills() {
        return skills.values();
    }
}