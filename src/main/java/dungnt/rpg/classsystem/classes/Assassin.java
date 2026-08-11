package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.Dash;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

public class Assassin extends RPGClass {

    public Assassin() {

        super(
                "assassin",
                "Assassin",
                "Một sát thủ nhanh nhẹn, chuyên gây sát thương chí mạng."
        );

        // =========================
        // OFFENSE
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_assassin_attack",
                        StatType.ATTACK,
                        ModifierType.FLAT,
                        4
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_assassin_crit",
                        StatType.CRIT_CHANCE,
                        ModifierType.FLAT,
                        15
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_assassin_crit_damage",
                        StatType.CRIT_DAMAGE,
                        ModifierType.FLAT,
                        0.50
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_assassin_armor_penetration",
                        StatType.ARMOR_PENETRATION,
                        ModifierType.FLAT,
                        5
                )
        );

        // =========================
        // DEFENSE
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_assassin_dodge",
                        StatType.DODGE_CHANCE,
                        ModifierType.FLAT,
                        10
                )
        );

        // =========================
        // MOVEMENT
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_assassin_speed",
                        StatType.MOVE_SPEED,
                        ModifierType.FLAT,
                        0.10
                )
        );

        // =========================
        // SKILLS
        // =========================

        addSkill(
                new Dash()
        );
    }
}