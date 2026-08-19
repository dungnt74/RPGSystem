package dungnt.rpg.stats;

public enum StatType {

    // =========================
    // OFFENSE
    // =========================

    ATTACK,
    BOW_ATTACK,
    MAGIC_ATTACK,

    ATTACK_SPEED,

    CRIT_CHANCE,
    CRIT_DAMAGE,
    CRIT_RESISTANCE,

    ARMOR_PENETRATION,
    MAGIC_PENETRATION,

    SKILL_DAMAGE,


    // =========================
    // DEFENSE
    // =========================

    DEFENSE,
    MAGIC_DEFENSE,

    DAMAGE_REDUCTION,

    BLOCK_CHANCE,
    BLOCK_POWER,

    DODGE_CHANCE,


    // =========================
    // HEALTH & RESOURCE
    // =========================

    MAX_HEALTH,
    MAX_MANA,

    HEALTH_REGEN,
    MANA_REGEN,

    LIFESTEAL,
    MANA_STEAL,

    COOLDOWN_REDUCTION,


    // =========================
    // MOVEMENT
    // =========================

    MOVE_SPEED,


    // =========================
    // ECONOMY / UTILITY
    // =========================

    EXP_BONUS,
    GOLD_BONUS,

    DROP_RATE,

    LUCK
}