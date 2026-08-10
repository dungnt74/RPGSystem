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
                        "class_mage_skill_damage",
                        StatType.SKILL_DAMAGE,
                        ModifierType.PERCENT,
                        10
                )
        );

        addSkill(new Fireball());
    }
}