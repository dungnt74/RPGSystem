package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.PowerStrike;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

public class Warrior extends RPGClass {

    public Warrior() {

        super(
                "warrior",
                "Warrior",
                "Một chiến binh mạnh mẽ chuyên chiến đấu cận chiến."
        );

        // =========================
        // OFFENSE
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_warrior_attack",
                        StatType.ATTACK,
                        ModifierType.FLAT,
                        5
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_warrior_crit_damage",
                        StatType.CRIT_DAMAGE,
                        ModifierType.FLAT,
                        0.25
                )
        );

        // =========================
        // DEFENSE
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_warrior_defense",
                        StatType.DEFENSE,
                        ModifierType.FLAT,
                        10
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_warrior_health",
                        StatType.MAX_HEALTH,
                        ModifierType.FLAT,
                        20
                )
        );

        // =========================
        // SKILLS
        // =========================

        addSkill(
                new PowerStrike()
        );
    }
}