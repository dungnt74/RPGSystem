package dungnt.rpg.mob;

import org.bukkit.entity.EntityType;

/**
 * Định nghĩa 1 loại quái RPG, đọc từ Mobs/*.yml (tương tự cách
 * RPGItem được đọc từ Items/*.yml). Đây CHỈ là "khuôn mẫu";
 * mỗi lần spawn ra 1 con quái thật (LivingEntity), plugin sẽ tạo
 * ra 1 {@link MobStats} + {@link MobData} runtime dựa theo
 * khuôn này (xem MobDefinitionManager / /rpg mob spawn).
 */
public class MobDefinition {

    private final String id;
    private final EntityType entityType;

    private String displayName;
    private double maxHealth = 20.0;
    private double attack = 5.0;
    private double defense = 0.0;
    private double magicDefense = 0.0;
    private String model;

    public MobDefinition(String id, EntityType entityType) {
        this.id = id;
        this.entityType = entityType;
        this.displayName = id;
    }

    public String getId() {
        return id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        this.maxHealth = Math.max(1.0, maxHealth);
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = Math.max(0.0, attack);
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = Math.max(0.0, defense);
    }

    public double getMagicDefense() {
        return magicDefense;
    }

    public void setMagicDefense(double magicDefense) {
        this.magicDefense = Math.max(0.0, magicDefense);
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    /** Tạo 1 {@link MobStats} runtime mới dựa theo định nghĩa này. */
    public MobStats toStats() {
        MobStats stats = new MobStats();
        stats.setMaxHealth(maxHealth);
        stats.setAttack(attack);
        stats.setDefense(defense);
        stats.setMagicDefense(magicDefense);
        return stats;
    }
}