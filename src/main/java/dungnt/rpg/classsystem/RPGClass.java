package dungnt.rpg.classsystem;

import dungnt.rpg.skills.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class RPGClass {

    private final String id;
    private final String name;
    private final String description;

    private final List<Skill> skills = new ArrayList<>();

    protected RPGClass(
            String id,
            String name,
            String description
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    public boolean hasSkill(String skillId) {

        return skills.stream()
                .anyMatch(skill ->
                        skill.getId().equalsIgnoreCase(skillId)
                );
    }

    public Skill getSkill(String skillId) {

        return skills.stream()
                .filter(skill ->
                        skill.getId().equalsIgnoreCase(skillId)
                )
                .findFirst()
                .orElse(null);
    }
}