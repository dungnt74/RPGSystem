package dungnt.rpg.combat;

import java.util.concurrent.ThreadLocalRandom;

public class DamageCalculator {

    // =========================
    // PHYSICAL DAMAGE
    // =========================

    public DamageResult calculate(
            double attack,
            double critChance,
            double critDamage,
            double multiplier
    ) {

        double baseDamage =
                attack * multiplier;

        boolean critical =
                ThreadLocalRandom
                        .current()
                        .nextDouble(100)
                        < critChance;

        if (critical) {
            baseDamage *= critDamage;
        }

        return new DamageResult(
                baseDamage,
                critical
        );
    }

    // =========================
    // MAGIC DAMAGE
    // =========================

    public DamageResult calculateMagic(
            double magicAttack,
            double critChance,
            double critDamage,
            double multiplier
    ) {

        double baseDamage =
                magicAttack * multiplier;

        boolean critical =
                ThreadLocalRandom
                        .current()
                        .nextDouble(100)
                        < critChance;

        if (critical) {
            baseDamage *= critDamage;
        }

        return new DamageResult(
                baseDamage,
                critical
        );
    }

    // =========================
    // PHYSICAL DEFENSE
    // =========================

    public double applyDefense(
            double damage,
            double defense,
            double armorPenetration
    ) {

        double effectiveDefense =
                defense *
                        (1.0 - armorPenetration / 100.0);

        effectiveDefense =
                Math.max(
                        0,
                        effectiveDefense
                );

        return Math.max(
                0,
                damage - effectiveDefense
        );
    }

    // =========================
    // MAGIC DEFENSE
    // =========================

    public double applyMagicDefense(
            double damage,
            double magicDefense,
            double magicPenetration
    ) {

        double effectiveMagicDefense =
                magicDefense *
                        (1.0 - magicPenetration / 100.0);

        effectiveMagicDefense =
                Math.max(
                        0,
                        effectiveMagicDefense
                );

        return Math.max(
                0,
                damage - effectiveMagicDefense
        );
    }

    // =========================
    // BLOCK
    // =========================

    public double applyBlock(
            double damage,
            double blockChance,
            double blockPower
    ) {

        double chance =
                Math.max(
                        0,
                        Math.min(
                                blockChance,
                                100
                        )
                );

        double power =
                Math.max(
                        0,
                        Math.min(
                                blockPower,
                                100
                        )
                );

        boolean blocked =
                ThreadLocalRandom
                        .current()
                        .nextDouble(100)
                        < chance;

        if (!blocked) {
            return damage;
        }

        return damage *
                (1.0 - power / 100.0);
    }

    // =========================
    // DODGE
    // =========================

    public boolean isDodged(
            double dodgeChance
    ) {

        double chance =
                Math.max(
                        0,
                        Math.min(
                                dodgeChance,
                                100
                        )
                );

        return ThreadLocalRandom
                .current()
                .nextDouble(100)
                < chance;
    }

    // =========================
    // DAMAGE REDUCTION
    // =========================

    public double applyDamageReduction(
            double damage,
            double damageReduction
    ) {

        double reduction =
                Math.max(
                        0,
                        Math.min(
                                damageReduction,
                                100
                        )
                );

        return damage *
                (1.0 - reduction / 100.0);
    }
}