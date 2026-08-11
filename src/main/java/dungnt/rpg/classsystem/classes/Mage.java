package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.Fireball;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

public class Mage extends RPGClass {

    public Mage() {

        super(
                "mage",
                "Mage",
                "Một pháp sư sử dụng sức mạnh phép thuật."
        );

        // =========================
        // CLASS STATS
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_mage_magic_attack",
                        StatType.MAGIC_ATTACK,
                        ModifierType.FLAT,
                        10
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_mage_magic_defense",
                        StatType.MAGIC_DEFENSE,
                        ModifierType.FLAT,
                        5
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_mage_mana",
                        StatType.MAX_MANA,
                        ModifierType.FLAT,
                        50
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_mage_crit",
                        StatType.CRIT_CHANCE,
                        ModifierType.FLAT,
                        5
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_mage_skill_damage",
                        StatType.SKILL_DAMAGE,
                        ModifierType.PERCENT,
                        10
                )
        );

        // =========================
        // SKILLS
        // =========================

        addSkill(
                new Fireball()
        );
    }
}