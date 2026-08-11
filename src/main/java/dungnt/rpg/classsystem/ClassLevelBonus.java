package dungnt.rpg.classsystem;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import java.util.UUID;

public final class ClassLevelBonus {

    private ClassLevelBonus() {
    }

    // ==================================================
    // APPLY LEVEL BONUS
    // ==================================================

    public static void apply(
            UUID uuid,
            RPGClass rpgClass,
            int level,
            StatManager statManager
    ) {

        if (uuid == null || rpgClass == null) {
            return;
        }

        if (level <= 1) {
            return;
        }

        int bonusLevels =
                level - 1;

        String classId =
                rpgClass.getId();

        // ==================================================
        // MAGE
        // ==================================================

        if (classId.equalsIgnoreCase("mage")) {

            // +5 Magic Attack / level
            statManager.addModifier(
                    uuid,
                    new StatModifier(
                            "level_" + classId + "_magic_attack",
                            StatType.MAGIC_ATTACK,
                            ModifierType.FLAT,
                            5.0 * bonusLevels
                    )
            );

            // +10 Max Mana / level
            statManager.addModifier(
                    uuid,
                    new StatModifier(
                            "level_" + classId + "_max_mana",
                            StatType.MAX_MANA,
                            ModifierType.FLAT,
                            10.0 * bonusLevels
                    )
            );

            // +2 Magic Defense / level
            statManager.addModifier(
                    uuid,
                    new StatModifier(
                            "level_" + classId + "_magic_defense",
                            StatType.MAGIC_DEFENSE,
                            ModifierType.FLAT,
                            2.0 * bonusLevels
                    )
            );

            // +1% Skill Damage / level
            statManager.addModifier(
                    uuid,
                    new StatModifier(
                            "level_" + classId + "_skill_damage",
                            StatType.SKILL_DAMAGE,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );
        }

        // ==================================================
        // SAU NÀY THÊM CLASS
        // ==================================================
        //
        // warrior
        // archer
        // assassin
        // tank
        //
        // ==================================================
    }
}