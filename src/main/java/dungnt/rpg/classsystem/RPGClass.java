package dungnt.rpg.classsystem;

import dungnt.rpg.skills.Skill;
import dungnt.rpg.stats.StatModifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RPGClass {

    private final String id;
    private final String name;
    private final String description;

    private final List<Skill> skills =
            new ArrayList<>();

    private final List<StatModifier> statModifiers =
            new ArrayList<>();

    protected RPGClass(
            String id,
            String name,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // ==================================================
    // BASIC
    // ==================================================

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // ==================================================
    // SKILLS
    // ==================================================

    public void addSkill(
            Skill skill
    ) {

        if (skill == null) {
            return;
        }

        skills.add(skill);
    }

    public List<Skill> getSkills() {

        return Collections.unmodifiableList(
                skills
        );
    }

    public boolean hasSkill(
            String skillId
    ) {

        if (skillId == null) {
            return false;
        }

        return skills.stream()
                .anyMatch(skill ->
                        skill.getId()
                                .equalsIgnoreCase(skillId)
                );
    }

    public Skill getSkill(
            String skillId
    ) {

        if (skillId == null) {
            return null;
        }

        return skills.stream()
                .filter(skill ->
                        skill.getId()
                                .equalsIgnoreCase(skillId)
                )
                .findFirst()
                .orElse(null);
    }

    // ==================================================
    // CLASS STATS
    // ==================================================

    public void addStatModifier(
            StatModifier modifier
    ) {

        if (modifier == null) {
            return;
        }

        // Không cho trùng ID
        statModifiers.removeIf(
                existing ->
                        existing.getId()
                                .equalsIgnoreCase(
                                        modifier.getId()
                                )
        );

        statModifiers.add(modifier);
    }

    public List<StatModifier> getStatModifiers() {

        return Collections.unmodifiableList(
                statModifiers
        );
    }
}