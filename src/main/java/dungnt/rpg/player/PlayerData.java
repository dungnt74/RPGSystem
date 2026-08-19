package dungnt.rpg.player;

import dungnt.rpg.classsystem.RPGClass;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private RPGClass rpgClass;
    private int level;
    private double experience;
    private double mana;
    private double maxMana;
    private double health;
    private double maxHealth;
    private final PlayerStats stats;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.level = 1;
        this.experience = 0;
        this.maxMana = 20;
        this.mana = maxMana;
        this.maxHealth = 20;
        this.health = maxHealth;
        this.stats = new PlayerStats();
    }

    public UUID getUuid() { return uuid; }

    public RPGClass getRpgClass() { return rpgClass; }
    public void setRpgClass(RPGClass rpgClass) { this.rpgClass = rpgClass; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }
    public void addLevel() { setLevel(level + 1); }

    public double getExperience() { return experience; }
    public void setExperience(double experience) { this.experience = Math.max(0, experience); }
    public void addExperience(double amount) { if (amount > 0) experience += amount; }
    public boolean removeExperience(double amount) {
        if (amount <= 0) return true;
        if (experience < amount) return false;
        experience -= amount;
        return true;
    }

    public double getMana() { return mana; }
    public double getMaxMana() { return maxMana; }
    public void setMaxMana(double maxMana) {
        this.maxMana = Math.max(0, maxMana);
        this.mana = Math.min(this.mana, this.maxMana);
    }
    public void setMana(double mana) { this.mana = Math.max(0, Math.min(mana, maxMana)); }
    public void addMana(double amount) { setMana(mana + amount); }
    public boolean useMana(double amount) {
        if (amount <= 0) return true;
        if (mana < amount) return false;
        mana -= amount;
        return true;
    }

    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }
    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.health = Math.min(this.health, this.maxHealth);
    }
    public void setHealth(double health) { this.health = Math.max(0, Math.min(health, maxHealth)); }
    public void addHealth(double amount) { setHealth(health + amount); }

    public PlayerStats getStats() { return stats; }
}
