package dungnt.rpg.stats;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.player.PlayerData;

import java.util.*;

public class StatManager {

    private final Map<UUID, List<StatModifier>> modifiers =
            new HashMap<>();

    // ==================================================
    // ADD MODIFIER
    // ==================================================

    public void addModifier(
            UUID uuid,
            StatModifier modifier
    ) {

        List<StatModifier> list =
                modifiers.computeIfAbsent(
                        uuid,
                        key -> new ArrayList<>()
                );

        // Nếu ID trùng thì thay modifier cũ
        list.removeIf(
                existing ->
                        existing.getId()
                                .equals(modifier.getId())
        );

        list.add(modifier);
    }

    // ==================================================
    // REMOVE MODIFIER
    // ==================================================

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

    // ==================================================
    // GET STAT
    // ==================================================

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

                    flatBonus +=
                            modifier.getAmount();

                } else if (
                        modifier.getModifierType()
                                == ModifierType.PERCENT
                ) {

                    percentBonus +=
                            modifier.getAmount();
                }
            }
        }

        double finalValue =
                baseValue + flatBonus;

        finalValue *=
                1.0 + (percentBonus / 100.0);

        return finalValue;
    }

    // ==================================================
    // GET PLAYER STAT
    //
    // Đây là hàm mới.
    //
    // Nó lấy:
    //
    // Base Stat
    // + Class Stat
    // + Level Stat
    // + Equipment Stat sau này
    //
    // ==================================================

    public double getPlayerStat(
            PlayerData playerData,
            StatType type
    ) {

        if (playerData == null) {
            return 0.0;
        }

        return getStat(
                playerData.getUuid(),
                playerData.getStats(),
                type
        );
    }

    // ==================================================
    // ADD CLASS MODIFIERS
    // ==================================================

    public void applyClassModifiers(
            PlayerData playerData
    ) {

        if (playerData == null) {
            return;
        }

        UUID uuid =
                playerData.getUuid();

        // Xóa modifier class cũ
        removeClassModifiers(uuid);

        RPGClass rpgClass =
                playerData.getRpgClass();

        if (rpgClass == null) {
            return;
        }

        for (StatModifier modifier :
                rpgClass.getStatModifiers()) {

            addModifier(
                    uuid,
                    modifier
            );
        }
    }

    // ==================================================
    // REMOVE CLASS MODIFIERS
    // ==================================================

    private void removeClassModifiers(
            UUID uuid
    ) {

        List<StatModifier> list =
                modifiers.get(uuid);

        if (list == null) {
            return;
        }

        list.removeIf(
                modifier ->
                        modifier.getId()
                                .startsWith("class_")
        );

        if (list.isEmpty()) {
            modifiers.remove(uuid);
        }
    }

    // ==================================================
    // LEVEL STAT
    // ==================================================

    public void applyLevelModifiers(
            PlayerData playerData
    ) {

        if (playerData == null) {
            return;
        }

        UUID uuid =
                playerData.getUuid();

        // Xóa level modifier cũ
        removeLevelModifiers(uuid);

        int level =
                playerData.getLevel();

        if (level <= 1) {
            return;
        }

        /*
         * Mỗi level tăng:
         *
         * +2 Attack
         * +1 Magic Attack
         * +1 Defense
         * +1 Magic Defense
         * +2 Max Health
         * +2 Max Mana
         */

        int bonusLevels =
                level - 1;

        addModifier(
                uuid,
                new StatModifier(
                        "level_attack",
                        StatType.ATTACK,
                        ModifierType.FLAT,
                        bonusLevels * 2.0
                )
        );

        addModifier(
                uuid,
                new StatModifier(
                        "level_magic_attack",
                        StatType.MAGIC_ATTACK,
                        ModifierType.FLAT,
                        bonusLevels * 1.0
                )
        );

        addModifier(
                uuid,
                new StatModifier(
                        "level_defense",
                        StatType.DEFENSE,
                        ModifierType.FLAT,
                        bonusLevels * 1.0
                )
        );

        addModifier(
                uuid,
                new StatModifier(
                        "level_magic_defense",
                        StatType.MAGIC_DEFENSE,
                        ModifierType.FLAT,
                        bonusLevels * 1.0
                )
        );

        addModifier(
                uuid,
                new StatModifier(
                        "level_max_health",
                        StatType.MAX_HEALTH,
                        ModifierType.FLAT,
                        bonusLevels * 2.0
                )
        );

        addModifier(
                uuid,
                new StatModifier(
                        "level_max_mana",
                        StatType.MAX_MANA,
                        ModifierType.FLAT,
                        bonusLevels * 2.0
                )
        );
    }

    // ==================================================
    // REMOVE LEVEL MODIFIERS
    // ==================================================

    private void removeLevelModifiers(
            UUID uuid
    ) {

        List<StatModifier> list =
                modifiers.get(uuid);

        if (list == null) {
            return;
        }

        list.removeIf(
                modifier ->
                        modifier.getId()
                                .startsWith("level_")
        );

        if (list.isEmpty()) {
            modifiers.remove(uuid);
        }
    }

    // ==================================================
    // APPLY ALL
    // ==================================================

    public void refreshPlayer(
            PlayerData playerData
    ) {

        if (playerData == null) {
            return;
        }

        applyClassModifiers(
                playerData
        );

        applyLevelModifiers(
                playerData
        );
    }

    // ==================================================
    // BASE STAT
    // ==================================================

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

            case MAGIC_ATTACK ->
                    stats.getMagicAttack();

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

    // ==================================================
    // CLEAR
    // ==================================================

    public void clearModifiers(
            UUID uuid
    ) {

        modifiers.remove(uuid);
    }
}