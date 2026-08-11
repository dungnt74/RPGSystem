package dungnt.rpg.level;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import java.util.ArrayList;
import java.util.List;

public final class ClassStatGrowth {

    private ClassStatGrowth() {
    }

    /**
     * Stat tăng thêm MỖI LEVEL.
     *
     * Ví dụ:
     *
     * Mage:
     * Level 2 -> +5 Magic Attack
     * Level 3 -> +10 Magic Attack
     * Level 4 -> +15 Magic Attack
     *
     * Vì đây là modifier tổng theo level,
     * không bị cộng chồng khi reload/reapply.
     */

    public static List<StatModifier> getGrowth(
            String classId,
            int level
    ) {

        List<StatModifier> modifiers =
                new ArrayList<>();

        if (level <= 1) {
            return modifiers;
        }

        int bonusLevels =
                level - 1;

        // ==================================================
        // MAGE
        // ==================================================

        if (classId.equalsIgnoreCase("mage")) {

            modifiers.add(
                    new StatModifier(
                            "level_mage_magic_attack",
                            StatType.MAGIC_ATTACK,
                            ModifierType.FLAT,
                            5.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_mage_max_mana",
                            StatType.MAX_MANA,
                            ModifierType.FLAT,
                            10.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_mage_skill_damage",
                            StatType.SKILL_DAMAGE,
                            ModifierType.PERCENT,
                            1.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_mage_magic_pen",
                            StatType.MAGIC_PENETRATION,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );

            return modifiers;
        }

        // ==================================================
        // WARRIOR
        // ==================================================

        if (classId.equalsIgnoreCase("warrior")) {

            modifiers.add(
                    new StatModifier(
                            "level_warrior_attack",
                            StatType.ATTACK,
                            ModifierType.FLAT,
                            3.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_warrior_health",
                            StatType.MAX_HEALTH,
                            ModifierType.FLAT,
                            5.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_warrior_defense",
                            StatType.DEFENSE,
                            ModifierType.FLAT,
                            2.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_warrior_crit_resistance",
                            StatType.CRIT_RESISTANCE,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );

            return modifiers;
        }

        // ==================================================
        // ROGUE
        // ==================================================

        if (classId.equalsIgnoreCase("rogue")) {

            modifiers.add(
                    new StatModifier(
                            "level_rogue_attack",
                            StatType.ATTACK,
                            ModifierType.FLAT,
                            3.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_rogue_crit",
                            StatType.CRIT_CHANCE,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_rogue_attack_speed",
                            StatType.ATTACK_SPEED,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );

            modifiers.add(
                    new StatModifier(
                            "level_rogue_dodge",
                            StatType.DODGE_CHANCE,
                            ModifierType.FLAT,
                            1.0 * bonusLevels
                    )
            );
        }

        return modifiers;
    }
}