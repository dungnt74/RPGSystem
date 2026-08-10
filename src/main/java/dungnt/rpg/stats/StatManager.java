package dungnt.rpg.stats;

import java.util.*;

public class StatManager {

    private final Map<UUID, List<StatModifier>> modifiers =
            new HashMap<>();

    public void addModifier(
            UUID uuid,
            StatModifier modifier
    ) {

        List<StatModifier> list =
                modifiers.computeIfAbsent(
                        uuid,
                        key -> new ArrayList<>()
                );

        list.removeIf(
                existing ->
                        existing.getId()
                                .equals(modifier.getId())
        );

        list.add(modifier);
    }

    public void removeModifier(
            UUID uuid,
            String modifierId
    ) {

        List<StatModifier> list =
                modifiers.get(uuid);

        if (list == null) {
            return;
        }

        list.removeIf(
                modifier ->
                        modifier.getId()
                                .equals(modifierId)
        );

        if (list.isEmpty()) {
            modifiers.remove(uuid);
        }
    }

    public double getStat(
            UUID uuid,
            StatsContainer baseStats,
            StatType type
    ) {

        double baseValue =
                getBaseStat(
                        baseStats,
                        type
                );

        double flatBonus = 0.0;
        double percentBonus = 0.0;

        List<StatModifier> list =
                modifiers.get(uuid);

        if (list != null) {

            for (StatModifier modifier : list) {

                if (modifier.getType() != type) {
                    continue;
                }

                if (modifier.getModifierType()
                        == ModifierType.FLAT) {

                    flatBonus += modifier.getAmount();

                } else if (
                        modifier.getModifierType()
                                == ModifierType.PERCENT
                ) {

                    percentBonus += modifier.getAmount();
                }
            }
        }

        double finalValue =
                baseValue + flatBonus;

        finalValue *=
                1.0 + (percentBonus / 100.0);

        return finalValue;
    }

    private double getBaseStat(
            StatsContainer stats,
            StatType type
    ) {

        return switch (type) {

            // =========================
            // OFFENSE
            // =========================

            case ATTACK ->
                    stats.getAttack();

            case ATTACK_SPEED ->
                    stats.getAttackSpeed();

            case CRIT_CHANCE ->
                    stats.getCritChance();

            case CRIT_DAMAGE ->
                    stats.getCritDamage();

            case CRIT_RESISTANCE ->
                    stats.getCritResistance();

            case ARMOR_PENETRATION ->
                    stats.getArmorPenetration();

            case MAGIC_PENETRATION ->
                    stats.getMagicPenetration();

            case SKILL_DAMAGE ->
                    stats.getSkillDamage();


            // =========================
            // DEFENSE
            // =========================

            case DEFENSE ->
                    stats.getDefense();

            case MAGIC_DEFENSE ->
                    stats.getMagicDefense();

            case DAMAGE_REDUCTION ->
                    stats.getDamageReduction();

            case BLOCK_CHANCE ->
                    stats.getBlockChance();

            case BLOCK_POWER ->
                    stats.getBlockPower();

            case DODGE_CHANCE ->
                    stats.getDodgeChance();


            // =========================
            // HEALTH / MANA
            // =========================

            case MAX_HEALTH ->
                    stats.getMaxHealth();

            case MAX_MANA ->
                    stats.getMaxMana();

            case HEALTH_REGEN ->
                    stats.getHealthRegen();

            case MANA_REGEN ->
                    stats.getManaRegen();

            case LIFESTEAL ->
                    stats.getLifesteal();

            case MANA_STEAL ->
                    stats.getManaSteal();

            case COOLDOWN_REDUCTION ->
                    stats.getCooldownReduction();


            // =========================
            // MOVEMENT
            // =========================

            case MOVE_SPEED ->
                    stats.getMoveSpeed();


            // =========================
            // UTILITY
            // =========================

            case EXP_BONUS ->
                    stats.getExpBonus();

            case GOLD_BONUS ->
                    stats.getGoldBonus();

            case DROP_RATE ->
                    stats.getDropRate();

            case LUCK ->
                    stats.getLuck();
        };
    }

    public void clearModifiers(UUID uuid) {
        modifiers.remove(uuid);
    }
}