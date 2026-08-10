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

    private final PlayerStats stats;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;

        this.level = 1;
        this.experience = 0;

        this.maxMana = 100;
        this.mana = maxMana;

        this.stats = new PlayerStats();
    }

    public UUID getUuid() {
        return uuid;
    }

    public RPGClass getRpgClass() {
        return rpgClass;
    }

    public void setRpgClass(RPGClass rpgClass) {
        this.rpgClass = rpgClass;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMana(double mana) {
        this.mana = Math.max(0, Math.min(mana, maxMana));
    }

    public void addMana(double amount) {
        setMana(this.mana + amount);
    }

    public PlayerStats getStats() {
        return stats;
    }

    public boolean useMana(double amount) {

        if (mana < amount) {
            return false;
        }

        mana -= amount;
        return true;
    }
}