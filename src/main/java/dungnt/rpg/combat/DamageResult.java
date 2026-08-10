package dungnt.rpg.combat;

public class DamageResult {

    private final double damage;
    private final boolean critical;


    public DamageResult(double damage, boolean critical) {
        this.damage = damage;
        this.critical = critical;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isCritical() {
        return critical;
    }
}