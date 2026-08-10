package dungnt.rpg.mob;

import dungnt.rpg.stats.StatsContainer;

public class MobStats implements StatsContainer {
    // =========================
    // OFFENSE
    // =========================

    private double attack = 0.0;
    private double magicAttack = 0.0;
    private double attackSpeed = 0.0;

    private double critChance = 0.0;
    private double critDamage = 1.5;
    private double critResistance = 0.0;

    private double armorPenetration = 0.0;
    private double magicPenetration = 0.0;

    private double skillDamage = 0.0;


    // =========================
    // DEFENSE
    // =========================

    private double defense = 0.0;
    private double magicDefense = 0.0;

    private double damageReduction = 0.0;

    private double blockChance = 0.0;
    private double blockPower = 0.0;

    private double dodgeChance = 0.0;


    // =========================
    // HEALTH & MANA
    // =========================

    private double maxHealth = 20.0;
    private double maxMana = 0.0;

    private double healthRegen = 0.0;
    private double manaRegen = 0.0;

    private double lifesteal = 0.0;
    private double manaSteal = 0.0;

    private double cooldownReduction = 0.0;


    // =========================
    // GETTERS
    // =========================

    public double getAttack() {
        return attack;
    }

    public double getMagicAttack() {
        return magicAttack;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getCritChance() {
        return critChance;
    }

    public double getCritDamage() {
        return critDamage;
    }

    public double getCritResistance() {
        return critResistance;
    }

    public double getArmorPenetration() {
        return armorPenetration;
    }

    public double getMagicPenetration() {
        return magicPenetration;
    }

    public double getSkillDamage() {
        return skillDamage;
    }

    public double getDefense() {
        return defense;
    }

    public double getMagicDefense() {
        return magicDefense;
    }

    public double getDamageReduction() {
        return damageReduction;
    }

    public double getBlockChance() {
        return blockChance;
    }

    public double getBlockPower() {
        return blockPower;
    }

    public double getDodgeChance() {
        return dodgeChance;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getHealthRegen() {
        return healthRegen;
    }

    public double getManaRegen() {
        return manaRegen;
    }

    public double getLifesteal() {
        return lifesteal;
    }

    public double getManaSteal() {
        return manaSteal;
    }

    public double getCooldownReduction() {
        return cooldownReduction;
    }

    @Override
    public double getMoveSpeed() {
        return 0;
    }

    @Override
    public double getExpBonus() {
        return 0;
    }

    @Override
    public double getGoldBonus() {
        return 0;
    }

    @Override
    public double getDropRate() {
        return 0;
    }

    @Override
    public double getLuck() {
        return 0;
    }


    // =========================
    // SETTERS
    // =========================

    public void setAttack(double attack) {
        this.attack = Math.max(0, attack);
    }

    public void setMagicAttack(double magicAttack) {
        this.magicAttack =
                Math.max(0, magicAttack);
    }

    public void setDefense(double defense) {
        this.defense = Math.max(0, defense);
    }

    public void setDamageReduction(double damageReduction) {
        this.damageReduction =
                Math.max(
                        0,
                        Math.min(
                                damageReduction,
                                100
                        )
                );
    }

    public void setCritChance(double critChance) {
        this.critChance = Math.max(0, critChance);
    }

    public void setCritDamage(double critDamage) {
        this.critDamage = Math.max(1.0, critDamage);
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
    }

    public void setMagicDefense(double magicDefense) {
        this.magicDefense = Math.max(0, magicDefense);
    }
}