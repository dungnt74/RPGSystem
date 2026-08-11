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
    // APPLY
    // ==================================================

    public static void apply(
            UUID uuid,
            RPGClass rpgClass,
            int level,
            StatManager statManager
    ) {

        if (uuid == null ||
                rpgClass == null ||
                statManager == null) {
            return;
        }

        if (level <= 1) {
            return;
        }

        double levels =
                level - 1;

        String prefix =
                "class_growth_" +
                        rpgClass.getId() +
                        "_";

        switch (rpgClass.getId().toLowerCase()) {

            // ==================================================
            // WARRIOR
            // ==================================================

            case "warrior" -> {

                add(
                        uuid,
                        statManager,
                        prefix + "attack",
                        StatType.ATTACK,
                        levels * 3.0
                );

                add(
                        uuid,
                        statManager,
                        prefix + "defense",
                        StatType.DEFENSE,
                        levels * 2.0
                );

                add(
                        uuid,
                        statManager,
                        prefix + "health",
                        StatType.MAX_HEALTH,
                        levels * 8.0
                );
            }

            // ==================================================
            // MAGE
            // ==================================================

            case "mage" -> {

                add(
                        uuid,
                        statManager,
                        prefix + "magic_attack",
                        StatType.MAGIC_ATTACK,
                        levels * 3.0
                );

                add(
                        uuid,
                        statManager,
                        prefix + "magic_defense",
                        StatType.MAGIC_DEFENSE,
                        levels * 1.5
                );

                add(
                        uuid,
                        statManager,
                        prefix + "mana",
                        StatType.MAX_MANA,
                        levels * 10.0
                );
            }

            // ==================================================
            // ARCHER
            // ==================================================

            case "archer" -> {

                add(
                        uuid,
                        statManager,
                        prefix + "attack",
                        StatType.ATTACK,
                        levels * 2.5
                );

                add(
                        uuid,
                        statManager,
                        prefix + "crit",
                        StatType.CRIT_CHANCE,
                        levels * 0.5
                );

                add(
                        uuid,
                        statManager,
                        prefix + "attack_speed",
                        StatType.ATTACK_SPEED,
                        levels * 0.5
                );
            }

            // ==================================================
            // ASSASSIN
            // ==================================================

            case "assassin" -> {

                add(
                        uuid,
                        statManager,
                        prefix + "attack",
                        StatType.ATTACK,
                        levels * 2.5
                );

                add(
                        uuid,
                        statManager,
                        prefix + "crit",
                        StatType.CRIT_CHANCE,
                        levels * 1.0
                );

                add(
                        uuid,
                        statManager,
                        prefix + "dodge",
                        StatType.DODGE_CHANCE,
                        levels * 0.5
                );
            }
        }
    }

    // ==================================================
    // ADD
    // ==================================================

    private static void add(
            UUID uuid,
            StatManager statManager,
            String id,
            StatType type,
            double amount
    ) {

        if (amount <= 0) {
            return;
        }

        statManager.addModifier(
                uuid,
                new StatModifier(
                        id,
                        type,
                        ModifierType.FLAT,
                        amount
                )
        );
    }
}