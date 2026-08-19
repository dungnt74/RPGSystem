package dungnt.rpg.stats;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.item.RPGItem;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatManager {

    /*
     * Base stats của từng player.
     *
     * Đây là stat gốc trước khi cộng:
     * - Class
     * - Level
     * - Equipment
     */
    private final Map<UUID, EnumMap<StatType, Double>> baseStats =
            new HashMap<>();

    /*
     * Các modifier đang áp dụng cho player.
     *
     * Ví dụ:
     *
     * equipment_mainhand_attack
     * class_warrior_attack
     * level_attack
     */
    private final Map<UUID, Map<String, StatModifier>> modifiers =
            new HashMap<>();

    // ==================================================
    // BASE STAT
    // ==================================================

    public void setBaseStat(
            UUID uuid,
            StatType type,
            double value
    ) {

        if (uuid == null || type == null) {
            return;
        }

        EnumMap<StatType, Double> stats =
                baseStats.computeIfAbsent(
                        uuid,
                        key -> new EnumMap<>(StatType.class)
                );

        stats.put(
                type,
                value
        );
    }

    // ==================================================
    // ADD BASE STAT
    // ==================================================

    public void addBaseStat(
            UUID uuid,
            StatType type,
            double amount
    ) {

        if (uuid == null || type == null) {
            return;
        }

        double current =
                getBaseStat(
                        uuid,
                        type
                );

        setBaseStat(
                uuid,
                type,
                current + amount
        );
    }

    // ==================================================
    // GET BASE STAT
    // ==================================================

    public double getBaseStat(
            UUID uuid,
            StatType type
    ) {

        if (uuid == null || type == null) {
            return 0;
        }

        Map<StatType, Double> stats =
                baseStats.get(uuid);

        if (stats == null) {
            return 0;
        }

        return stats.getOrDefault(
                type,
                0.0
        );
    }

    // ==================================================
    // ADD MODIFIER
    // ==================================================

    public void addModifier(
            UUID uuid,
            StatModifier modifier
    ) {

        if (uuid == null || modifier == null) {
            return;
        }

        Map<String, StatModifier> playerModifiers =
                modifiers.computeIfAbsent(
                        uuid,
                        key -> new HashMap<>()
                );

        /*
         * Cùng ID thì replace.
         *
         * Điều này tránh duplicate modifier
         * khi equipment được refresh.
         */
        playerModifiers.put(
                modifier.getId(),
                modifier
        );
    }

    // ==================================================
    // REMOVE MODIFIER
    // ==================================================

    public void removeModifier(
            UUID uuid,
            String modifierId
    ) {

        if (uuid == null || modifierId == null) {
            return;
        }

        Map<String, StatModifier> playerModifiers =
                modifiers.get(uuid);

        if (playerModifiers == null) {
            return;
        }

        playerModifiers.remove(
                modifierId
        );

        if (playerModifiers.isEmpty()) {
            modifiers.remove(uuid);
        }
    }

    // ==================================================
    // REMOVE BY PREFIX
    // ==================================================

    private void removeModifiersByPrefix(
            UUID uuid,
            String prefix
    ) {

        if (uuid == null || prefix == null) {
            return;
        }

        Map<String, StatModifier> playerModifiers =
                modifiers.get(uuid);

        if (playerModifiers == null) {
            return;
        }

        String lowerPrefix =
                prefix.toLowerCase();

        playerModifiers.entrySet()
                .removeIf(entry ->
                        entry.getKey()
                                .toLowerCase()
                                .startsWith(lowerPrefix)
                );

        if (playerModifiers.isEmpty()) {
            modifiers.remove(uuid);
        }
    }

    // ==================================================
    // GET MODIFIER
    // ==================================================

    public StatModifier getModifier(
            UUID uuid,
            String modifierId
    ) {

        if (uuid == null || modifierId == null) {
            return null;
        }

        Map<String, StatModifier> playerModifiers =
                modifiers.get(uuid);

        if (playerModifiers == null) {
            return null;
        }

        return playerModifiers.get(
                modifierId
        );
    }

    // ==================================================
    // GET ALL MODIFIERS
    // ==================================================

    public Map<String, StatModifier> getModifiers(
            UUID uuid
    ) {

        if (uuid == null) {
            return Map.of();
        }

        Map<String, StatModifier> playerModifiers =
                modifiers.get(uuid);

        if (playerModifiers == null) {
            return Map.of();
        }

        return Map.copyOf(
                playerModifiers
        );
    }

    // ==================================================
    // FINAL STAT
    // ==================================================

    public double getStat(
            UUID uuid,
            StatType type
    ) {

        if (uuid == null || type == null) {
            return 0;
        }

        /*
         * Bắt đầu bằng BASE STAT.
         */
        double value =
                getBaseStat(
                        uuid,
                        type
                );

        Map<String, StatModifier> playerModifiers =
                modifiers.get(uuid);

        if (playerModifiers == null) {
            return value;
        }

        // ==================================================
        // FLAT
        // ==================================================

        for (StatModifier modifier :
                playerModifiers.values()) {

            if (modifier == null) {
                continue;
            }

            if (modifier.getType() != type) {
                continue;
            }

            if (modifier.getModifierType()
                    == ModifierType.FLAT) {

                value +=
                        modifier.getAmount();
            }
        }

        // ==================================================
        // PERCENT
        // ==================================================

        double percent = 0;

        for (StatModifier modifier :
                playerModifiers.values()) {

            if (modifier == null) {
                continue;
            }

            if (modifier.getType() != type) {
                continue;
            }

            if (modifier.getModifierType()
                    == ModifierType.PERCENT) {

                percent +=
                        modifier.getAmount();
            }
        }

        value *=
                1.0 +
                        percent / 100.0;

        return value;
    }

    // ==================================================
    // APPLY LEVEL
    // ==================================================

    public void removeLevel(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        removeModifiersByPrefix(
                uuid,
                "level_"
        );
    }

    // ==================================================
    // APPLY RPG ITEM
    // ==================================================

    public void applyItem(
            UUID uuid,
            RPGItem item
    ) {

        if (uuid == null || item == null) {
            return;
        }

        for (StatModifier modifier :
                item.getStatModifiers()) {

            if (modifier == null) {
                continue;
            }

            /*
             * Item modifiers are always tracked as EQUIPMENT.
             * This lets systems such as EXP_BONUS distinguish
             * equipment buffs from rank/base modifiers.
             */
            StatModifier equipmentModifier =
                    new StatModifier(
                            modifier.getId(),
                            modifier.getType(),
                            modifier.getModifierType(),
                            modifier.getAmount(),
                            ModifierSource.EQUIPMENT
                    );

            addModifier(
                    uuid,
                    equipmentModifier
            );
        }
    }

    // ==================================================
    // REMOVE RPG ITEM
    // ==================================================

    public void removeItem(
            UUID uuid,
            RPGItem item
    ) {

        if (uuid == null || item == null) {
            return;
        }

        for (StatModifier modifier :
                item.getStatModifiers()) {

            if (modifier == null) {
                continue;
            }

            removeModifier(
                    uuid,
                    modifier.getId()
            );
        }
    }

    // ==================================================
    // APPLY CLASS
    // ==================================================

    public void applyClass(
            UUID uuid,
            RPGClass rpgClass
    ) {

        if (uuid == null || rpgClass == null) {
            return;
        }

        for (StatModifier modifier :
                rpgClass.getStatModifiers()) {

            if (modifier == null) {
                continue;
            }

            StatModifier classModifier =
                    new StatModifier(
                            "class_" + modifier.getId(),
                            modifier.getType(),
                            modifier.getModifierType(),
                            modifier.getAmount()
                    );

            addModifier(
                    uuid,
                    classModifier
            );
        }
    }

    public void syncBaseStats(
            UUID uuid,
            StatsContainer stats
    ) {

        if (uuid == null || stats == null) {
            return;
        }

        setBaseStat(uuid, StatType.ATTACK, stats.getAttack());
        setBaseStat(uuid, StatType.BOW_ATTACK, stats.getBowAttack());
        setBaseStat(uuid, StatType.MAGIC_ATTACK, stats.getMagicAttack());

        setBaseStat(uuid, StatType.ATTACK_SPEED, stats.getAttackSpeed());

        setBaseStat(uuid, StatType.CRIT_CHANCE, stats.getCritChance());
        setBaseStat(uuid, StatType.CRIT_DAMAGE, stats.getCritDamage());
        setBaseStat(uuid, StatType.CRIT_RESISTANCE, stats.getCritResistance());

        setBaseStat(uuid, StatType.ARMOR_PENETRATION, stats.getArmorPenetration());
        setBaseStat(uuid, StatType.MAGIC_PENETRATION, stats.getMagicPenetration());

        setBaseStat(uuid, StatType.SKILL_DAMAGE, stats.getSkillDamage());

        setBaseStat(uuid, StatType.DEFENSE, stats.getDefense());
        setBaseStat(uuid, StatType.MAGIC_DEFENSE, stats.getMagicDefense());

        setBaseStat(uuid, StatType.DAMAGE_REDUCTION, stats.getDamageReduction());

        setBaseStat(uuid, StatType.BLOCK_CHANCE, stats.getBlockChance());
        setBaseStat(uuid, StatType.BLOCK_POWER, stats.getBlockPower());

        setBaseStat(uuid, StatType.DODGE_CHANCE, stats.getDodgeChance());

        setBaseStat(uuid, StatType.MAX_HEALTH, stats.getMaxHealth());
        setBaseStat(uuid, StatType.MAX_MANA, stats.getMaxMana());

        setBaseStat(uuid, StatType.HEALTH_REGEN, stats.getHealthRegen());
        setBaseStat(uuid, StatType.MANA_REGEN, stats.getManaRegen());

        setBaseStat(uuid, StatType.LIFESTEAL, stats.getLifesteal());
        setBaseStat(uuid, StatType.MANA_STEAL, stats.getManaSteal());

        setBaseStat(uuid, StatType.COOLDOWN_REDUCTION, stats.getCooldownReduction());

        setBaseStat(uuid, StatType.MOVE_SPEED, stats.getMoveSpeed());

        setBaseStat(uuid, StatType.EXP_BONUS, stats.getExpBonus());
        setBaseStat(uuid, StatType.GOLD_BONUS, stats.getGoldBonus());

        setBaseStat(uuid, StatType.DROP_RATE, stats.getDropRate());
        setBaseStat(uuid, StatType.LUCK, stats.getLuck());
    }

    // ==================================================
    // REMOVE CLASS GROWTH
    // ==================================================

    public void removeClassGrowth(
            UUID uuid,
            RPGClass rpgClass
    ) {

        if (uuid == null || rpgClass == null) {
            return;
        }

        removeModifiersByPrefix(
                uuid,
                "class_growth_" +
                        rpgClass.getId() +
                        "_"
        );
    }

    // ==================================================
    // REMOVE CLASS
    // ==================================================

    public void removeClass(
            UUID uuid,
            RPGClass rpgClass
    ) {

        if (uuid == null || rpgClass == null) {
            return;
        }

        for (StatModifier modifier :
                rpgClass.getStatModifiers()) {

            if (modifier == null) {
                continue;
            }

            removeModifier(
                    uuid,
                    "class_" +
                            modifier.getId()
            );
        }
    }

    // ==================================================
    // CLEAR MODIFIERS
    // ==================================================

    public void clearModifiers(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        modifiers.remove(
                uuid
        );
    }

    // ==================================================
    // CLEAR PLAYER
    // ==================================================

    public void clear(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        baseStats.remove(uuid);
        modifiers.remove(uuid);
    }

    // ==================================================
    // REMOVE PLAYER
    // ==================================================

    public void remove(
            UUID uuid
    ) {

        clear(uuid);
    }
}