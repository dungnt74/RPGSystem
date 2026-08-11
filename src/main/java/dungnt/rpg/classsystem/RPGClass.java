package dungnt.rpg.classsystem;

import dungnt.rpg.skills.Skill;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class RPGClass {

    private final String id;
    private final String name;
    private final String description;

    private final List<Skill> skills =
            new ArrayList<>();

    private final List<StatModifier> statModifiers =
            new ArrayList<>();

    /*
     * Bonus riêng theo level của Class.
     */
    private final ClassLevelBonus levelBonus =
            new ClassLevelBonus();

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
    // BASIC INFO
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

    public void addSkill(Skill skill) {

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
    // STATIC CLASS STATS
    // ==================================================

    public void addStatModifier(
            StatModifier modifier
    ) {

        if (modifier == null) {
            return;
        }

        statModifiers.add(modifier);
    }

    public List<StatModifier> getStatModifiers() {

        return Collections.unmodifiableList(
                statModifiers
        );
    }

    // ==================================================
    // LEVEL BONUS
    // ==================================================

    public ClassLevelBonus getLevelBonus() {
        return levelBonus;
    }

    /**
     * Thiết lập bonus stat theo mỗi level.
     *
     * Ví dụ:
     *
     * addLevelBonus(
     *     StatType.ATTACK,
     *     2
     * );
     *
     * Level 1 = +0
     * Level 2 = +2
     * Level 3 = +4
     * Level 10 = +18
     */
    protected void addLevelBonus(
            StatType stat,
            double amountPerLevel
    ) {

        if (stat == null) {
            return;
        }

        levelBonus.add(
                stat,
                amountPerLevel
        );
    }

    /**
     * Lấy bonus của một stat tại level hiện tại.
     */
    public double getLevelBonus(
            StatType stat,
            int level
    ) {

        return levelBonus.getBonus(
                stat,
                level
        );
    }

    /**
     * Lấy toàn bộ level bonus.
     */
    public Map<StatType, Double> getLevelBonuses() {

        return levelBonus.getBonuses();
    }
}