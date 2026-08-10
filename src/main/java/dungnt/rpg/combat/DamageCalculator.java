package dungnt.rpg.combat;

import dungnt.rpg.player.PlayerStats;

import java.util.concurrent.ThreadLocalRandom;

public class DamageCalculator {

    public DamageResult calculate(
            double attack,
            double critChance,
            double critDamage,
            double multiplier
    ) {

        double baseDamage =
                attack * multiplier;

        boolean critical =
                java.util.concurrent.ThreadLocalRandom
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

    public double applyDefense(
            double damage,
            double defense
    ) {

        return Math.max(
                0,
                damage - defense
        );
    }
}