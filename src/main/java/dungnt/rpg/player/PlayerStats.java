package dungnt.rpg.player;

import dungnt.rpg.stats.StatsContainer;

public class PlayerStats implements StatsContainer {
    // =========================
    // OFFENSE
    // =========================

    private double attack = 10.0;
    private double bowAttack = 0.0;
    private double magicAttack = 0.0;

    private double attackSpeed = 0.0;

    private double critChance = 0.0;
    private double critDamage = 150.0;
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
    private double maxMana = 100.0;

    private double healthRegen = 0.0;
    private double manaRegen = 0.0;

    private double lifesteal = 0.0;
    private double manaSteal = 0.0;

    private double cooldownReduction = 0.0;


    // =========================
    // MOVEMENT
    // =========================

    private double moveSpeed = 0.0;


    // =========================
    // UTILITY
    // =========================

    private double expBonus = 1.0;
    private double goldBonus = 0.0;

    private double dropRate = 0.0;
    private double luck = 0.0;


    // =========================
    // GETTERS / SETTERS
    // =========================
    public double getBowAttack() {
        return bowAttack;
    }

    public double getMagicAttack() {
        return magicAttack;
    }

    public void setBowAttack(double bowAttack) {
        this.bowAttack = Math.max(0, bowAttack);
    }

    public void setMagicAttack(double magicAttack) {
        this.magicAttack = Math.max(0, magicAttack);
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = Math.max(0, attack);
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = Math.max(0, attackSpeed);
    }

    public double getCritChance() {
        return critChance;
    }

    public void setCritChance(double critChance) {
        this.critChance = Math.max(0, critChance);
    }

    public double getCritDamage() {
        return critDamage;
    }

    public void setCritDamage(double critDamage) {
        this.critDamage = Math.max(100.0, critDamage);
    }

    public double getCritResistance() {
        return critResistance;
    }

    public void setCritResistance(double critResistance) {
        this.critResistance = Math.max(0, critResistance);
    }

    public double getArmorPenetration() {
        return armorPenetration;
    }

    public void setArmorPenetration(double armorPenetration) {
        this.armorPenetration = Math.max(0, armorPenetration);
    }

    public double getMagicPenetration() {
        return magicPenetration;
    }

    public void setMagicPenetration(double magicPenetration) {
        this.magicPenetration = Math.max(0, magicPenetration);
    }

    public double getSkillDamage() {
        return skillDamage;
    }

    public void setSkillDamage(double skillDamage) {
        this.skillDamage = Math.max(0, skillDamage);
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = Math.max(0, defense);
    }

    public double getMagicDefense() {
        return magicDefense;
    }

    public void setMagicDefense(double magicDefense) {
        this.magicDefense = Math.max(0, magicDefense);
    }

    public double getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(double damageReduction) {
        this.damageReduction = Math.max(0, damageReduction);
    }

    public double getBlockChance() {
        return blockChance;
    }

    public void setBlockChance(double blockChance) {
        this.blockChance = Math.max(0, blockChance);
    }

    public double getBlockPower() {
        return blockPower;
    }

    public void setBlockPower(double blockPower) {
        this.blockPower = Math.max(0, blockPower);
    }

    public double getDodgeChance() {
        return dodgeChance;
    }

    public void setDodgeChance(double dodgeChance) {
        this.dodgeChance = Math.max(0, dodgeChance);
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = Math.max(0, maxMana);
    }

    public double getHealthRegen() {
        return healthRegen;
    }

    public void setHealthRegen(double healthRegen) {
        this.healthRegen = Math.max(0, healthRegen);
    }

    public double getManaRegen() {
        return manaRegen;
    }

    public void setManaRegen(double manaRegen) {
        this.manaRegen = Math.max(0, manaRegen);
    }

    public double getLifesteal() {
        return lifesteal;
    }

    public void setLifesteal(double lifesteal) {
        this.lifesteal = Math.max(0, lifesteal);
    }

    public double getManaSteal() {
        return manaSteal;
    }

    public void setManaSteal(double manaSteal) {
        this.manaSteal = Math.max(0, manaSteal);
    }

    public double getCooldownReduction() {
        return cooldownReduction;
    }

    public void setCooldownReduction(double cooldownReduction) {
        this.cooldownReduction = Math.max(0, cooldownReduction);
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = Math.max(0, moveSpeed);
    }

    public double getExpBonus() {
        return expBonus;
    }

    public void setExpBonus(double expBonus) {
        this.expBonus = Math.max(0, expBonus);
    }

    public double getGoldBonus() {
        return goldBonus;
    }

    public void setGoldBonus(double goldBonus) {
        this.goldBonus = Math.max(0, goldBonus);
    }

    public double getDropRate() {
        return dropRate;
    }

    public void setDropRate(double dropRate) {
        this.dropRate = Math.max(0, dropRate);
    }

    public double getLuck() {
        return luck;
    }

    public void setLuck(double luck) {
        this.luck = Math.max(0, luck);
    }


    // ==================================================
    // RESET TO DEFAULT BASE (dùng cho /class remove)
    // ==================================================
    //
    // Khác với resetAllToZero(): trả BASE STATS về đúng giá trị
    // mặc định ban đầu của một player chưa chọn Class (Attack=10,
    // Crit Damage=150%, Max Health=20, Max Mana=100), thay vì xoá
    // sạch về 0. Buff/stat đến từ item vẫn nằm ở StatManager dưới
    // dạng modifier riêng và KHÔNG bị đụng tới bởi hàm này.
    // ==================================================

    public void resetToDefaultBase() {

        attack = 1.0;
        bowAttack = 1.0;
        magicAttack = 1.0;
        attackSpeed = 1.0;

        critChance = 5.0;
        critDamage = 120.0;
        critResistance = 0.0;

        armorPenetration = 0.0;
        magicPenetration = 0.0;
        skillDamage = 0.0;

        defense = 0.0;
        magicDefense = 0.0;
        damageReduction = 0.0;

        blockChance = 0.0;
        blockPower = 0.0;
        dodgeChance = 0.0;

        maxHealth = 20.0;
        maxMana = 20.0;

        healthRegen = 0.0;
        manaRegen = 1.0;

        lifesteal = 0.0;
        manaSteal = 0.0;
        cooldownReduction = 0.0;

        moveSpeed = 0.0;

        expBonus = 1.0;
        goldBonus = 0.0;
        dropRate = 0.0;
        luck = 0.0;
    }

    // ==================================================
    // RESET ALL BASE STATS
    // ==================================================

    public void resetAllToZero() {

        attack = 0.0;
        bowAttack = 0.0;
        magicAttack = 0.0;
        attackSpeed = 0.0;

        critChance = 0.0;
        critDamage = 0.0;
        critResistance = 0.0;

        armorPenetration = 0.0;
        magicPenetration = 0.0;
        skillDamage = 0.0;

        defense = 0.0;
        magicDefense = 0.0;
        damageReduction = 0.0;

        blockChance = 0.0;
        blockPower = 0.0;
        dodgeChance = 0.0;

        maxHealth = 0.0;
        maxMana = 0.0;

        healthRegen = 0.0;
        manaRegen = 0.0;

        lifesteal = 0.0;
        manaSteal = 0.0;
        cooldownReduction = 0.0;

        moveSpeed = 0.0;

        expBonus = 0.0;
        goldBonus = 0.0;
        dropRate = 0.0;
        luck = 0.0;
    }
}