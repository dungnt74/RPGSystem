package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.Dash;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

public class Archer extends RPGClass {

    public Archer() {

        super(
                "archer",
                "Archer",
                "Một xạ thủ chuyên chiến đấu từ khoảng cách xa."
        );

        // =========================
        // OFFENSE
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_archer_attack",
                        StatType.BOW_ATTACK,
                        ModifierType.FLAT,
                        5
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_archer_crit",
                        StatType.CRIT_CHANCE,
                        ModifierType.FLAT,
                        10
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_archer_crit_damage",
                        StatType.CRIT_DAMAGE,
                        ModifierType.FLAT,
                        20
                )
        );

        // =========================
        // MOVEMENT
        // =========================

        addStatModifier(
                new StatModifier(
                        "class_archer_speed",
                        StatType.MOVE_SPEED,
                        ModifierType.FLAT,
                        0.05
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