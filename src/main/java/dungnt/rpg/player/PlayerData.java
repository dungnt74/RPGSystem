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

        this.stats =
                new PlayerStats();
    }

    // ==================================================
    // UUID
    // ==================================================

    public UUID getUuid() {
        return uuid;
    }

    // ==================================================
    // CLASS
    // ==================================================

    public RPGClass getRpgClass() {
        return rpgClass;
    }

    public void setRpgClass(
            RPGClass rpgClass
    ) {
        this.rpgClass = rpgClass;
    }

    // ==================================================
    // LEVEL
    // ==================================================

    public int getLevel() {
        return level;
    }

    public void setLevel(
            int level
    ) {

        this.level =
                Math.max(
                        1,
                        level
                );
    }

    public void addLevel() {
        setLevel(
                this.level + 1
        );
    }

    // ==================================================
    // EXPERIENCE
    // ==================================================

    public double getExperience() {
        return experience;
    }

    public void setExperience(
            double experience
    ) {

        this.experience =
                Math.max(
                        0,
                        experience
                );
    }

    public void addExperience(
            double amount
    ) {

        if (amount <= 0) {
            return;
        }

        experience += amount;
    }

    public boolean removeExperience(
            double amount
    ) {

        if (amount <= 0) {
            return true;
        }

        if (experience < amount) {
            return false;
        }

        experience -= amount;

        return true;
    }

    // ==================================================
    // MANA
    // ==================================================

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(
            double maxMana
    ) {

        this.maxMana =
                Math.max(
                        0,
                        maxMana
                );

        this.mana =
                Math.min(
                        this.mana,
                        this.maxMana
                );
    }

    public void setMana(
            double mana
    ) {

        this.mana =
                Math.max(
                        0,
                        Math.min(
                                mana,
                                maxMana
                        )
                );
    }

    public void addMana(
            double amount
    ) {

        setMana(
                this.mana + amount
        );
    }

    public boolean useMana(
            double amount
    ) {

        if (amount <= 0) {
            return true;
        }

        if (mana < amount) {
            return false;
        }

        mana -= amount;

        return true;
    }

    // ==================================================
    // STATS
    // ==================================================

    public PlayerStats getStats() {
        return stats;
    }
}