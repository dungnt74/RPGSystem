package dungnt.rpg.stats;

public interface StatsContainer {

    double getAttack();
    double getAttackSpeed();

    double getCritChance();
    double getCritDamage();
    double getCritResistance();

    double getArmorPenetration();
    double getMagicPenetration();

    double getSkillDamage();

    double getDefense();
    double getMagicDefense();

    double getDamageReduction();

    double getBlockChance();
    double getBlockPower();

    double getDodgeChance();

    double getMaxHealth();
    double getMaxMana();

    double getHealthRegen();
    double getManaRegen();

    double getLifesteal();
    double getManaSteal();

    double getCooldownReduction();

    double getMoveSpeed();

    double getExpBonus();
    double getGoldBonus();

    double getDropRate();
    double getLuck();
}