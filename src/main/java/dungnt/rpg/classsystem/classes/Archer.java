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

        addStatModifier(
                new StatModifier(
                        "class_archer_attack",
                        StatType.ATTACK,
                        ModifierType.FLAT,
                        3
                )
        );

        addStatModifier(
                new StatModifier(
                        "class_archer_crit",
                        StatType.CRIT_CHANCE,
                        ModifierType.FLAT,
                        5
                )
        );

        addSkill(new Dash());
    }
}