package dungnt.rpg.classsystem;

import dungnt.rpg.stats.StatType;

import java.util.EnumMap;
import java.util.Map;

public final class ClassLevelBonus {

    private final Map<StatType, Double> bonuses =
            new EnumMap<>(StatType.class);

    public ClassLevelBonus() {
    }

    public ClassLevelBonus add(
            StatType stat,
            double amountPerLevel
    ) {

        bonuses.merge(
                stat,
                amountPerLevel,
                Double::sum
        );

        return this;
    }

    public double getBonus(
            StatType stat,
            int level
    ) {

        if (level <= 1) {
            return 0;
        }

        double perLevel =
                bonuses.getOrDefault(
                        stat,
                        0.0
                );

        /*
         * Level 1 = 0 bonus
         * Level 2 = 1 lần
         * Level 3 = 2 lần
         * ...
         */

        return perLevel * (level - 1);
    }

    public Map<StatType, Double> getBonuses() {
        return Map.copyOf(bonuses);
    }
}