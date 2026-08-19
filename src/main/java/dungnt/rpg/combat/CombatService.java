package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.stats.StatType;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CombatService {

    private final MyRPG plugin;

    private final DamageCalculator damageCalculator;

    private final StatManager statManager;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public CombatService(
            MyRPG plugin
    ) {

        this.plugin = plugin;

        this.damageCalculator =
                plugin.getDamageCalculator();

        this.statManager =
                plugin.getStatManager();
    }

    /**
     * RPG damage must never be applied to a player who is in Creative mode.
     * This guard is centralized so physical, magic and bow damage all follow
     * the same rule.
     */
    public boolean canReceiveRPGDamage(LivingEntity target) {
        return target != null
                && (!(target instanceof Player player)
                || player.getGameMode() != org.bukkit.GameMode.CREATIVE);
    }

    private boolean canDamageInPvPZone(Player attacker, LivingEntity target) {
        if (!(target instanceof Player targetPlayer)) {
            return true;
        }

        return PvPProtection.canDamagePlayer(attacker, targetPlayer);
    }

    // ==================================================
    // PHYSICAL DAMAGE
    // ==================================================

    public DamageResult damage(
            Player attacker,
            LivingEntity target,
            double multiplier
    ) {

        if (attacker == null ||
                target == null) {

            return new DamageResult(
                    0,
                    false
            );
        }

        if (!target.isValid() ||
                target.isDead() ||
                !canReceiveRPGDamage(target) ||
                !canDamageInPvPZone(attacker, target)) {

            return new DamageResult(
                    0,
                    false
            );
        }

        if (multiplier <= 0) {

            return new DamageResult(
                    0,
                    false
            );
        }

        // ==================================================
        // FINAL ATTACK
        // ==================================================

        double attack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.ATTACK
                );

        // ==================================================
        // CRIT
        // ==================================================

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_DAMAGE
                );

        double critMultiplier =
                normalizeCritDamage(
                        critDamage
                );

        // ==================================================
        // CALCULATE
        // ==================================================

        DamageResult result =
                damageCalculator.calculate(
                        attack,
                        critChance,
                        critMultiplier,
                        multiplier
                );

        // ==================================================
        // TARGET DEFENSE
        // ==================================================

        double defense =
                getTargetDefense(
                        target
                );

        // ==================================================
        // ARMOR PENETRATION
        // ==================================================

        double armorPenetration =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.ARMOR_PENETRATION
                );

        // ==================================================
        // APPLY DEFENSE
        // ==================================================

        double finalDamage =
                damageCalculator.applyDefense(
                        result.getDamage(),
                        defense,
                        armorPenetration
                );

        // ==================================================
        // DAMAGE REDUCTION
        // ==================================================

        double damageReduction =
                getTargetDamageReduction(
                        target
                );

        finalDamage =
                damageCalculator.applyDamageReduction(
                        finalDamage,
                        damageReduction
                );

        // ==================================================
        // LIFESTEAL
        // ==================================================

        applyLifesteal(
                attacker,
                finalDamage
        );

        // ==================================================
        // RETURN
        // ==================================================

        return new DamageResult(
                finalDamage,
                result.isCritical()
        );
    }

    // ==================================================
    // MAGIC DAMAGE
    // ==================================================

    public DamageResult magicDamage(
            Player attacker,
            LivingEntity target,
            double multiplier
    ) {

        if (attacker == null ||
                target == null) {

            return new DamageResult(
                    0,
                    false
            );
        }

        if (!target.isValid() ||
                target.isDead() ||
                !canReceiveRPGDamage(target) ||
                !canDamageInPvPZone(attacker, target)) {

            return new DamageResult(
                    0,
                    false
            );
        }

        if (multiplier <= 0) {

            return new DamageResult(
                    0,
                    false
            );
        }

        // ==================================================
        // MAGIC ATTACK
        // ==================================================

        double magicAttack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.MAGIC_ATTACK
                );

        // ==================================================
        // CRIT
        // ==================================================

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_DAMAGE
                );

        double critMultiplier =
                normalizeCritDamage(
                        critDamage
                );

        // ==================================================
        // CALCULATE
        // ==================================================

        DamageResult result =
                damageCalculator.calculateMagic(
                        magicAttack,
                        critChance,
                        critMultiplier,
                        multiplier
                );

        // ==================================================
        // MAGIC DEFENSE
        // ==================================================

        double magicDefense =
                getTargetMagicDefense(
                        target
                );

        // ==================================================
        // MAGIC PENETRATION
        // ==================================================

        double magicPenetration =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.MAGIC_PENETRATION
                );

        // ==================================================
        // APPLY MAGIC DEFENSE
        // ==================================================

        double finalDamage =
                damageCalculator.applyMagicDefense(
                        result.getDamage(),
                        magicDefense,
                        magicPenetration
                );

        // ==================================================
        // DAMAGE REDUCTION
        // ==================================================

        double damageReduction =
                getTargetDamageReduction(
                        target
                );

        finalDamage =
                damageCalculator.applyDamageReduction(
                        finalDamage,
                        damageReduction
                );

        // ==================================================
        // LIFESTEAL
        // ==================================================

        applyLifesteal(
                attacker,
                finalDamage
        );

        // ==================================================
        // RETURN
        // ==================================================

        return new DamageResult(
                finalDamage,
                result.isCritical()
        );
    }

    // ==================================================
    // BOW DAMAGE
    // ==================================================

    public DamageResult bowDamage(
            Player attacker,
            LivingEntity target,
            double multiplier
    ) {

        if (attacker == null || target == null) {
            return new DamageResult(0, false);
        }

        if (!target.isValid() ||
                target.isDead() ||
                !canReceiveRPGDamage(target) ||
                !canDamageInPvPZone(attacker, target)) {
            return new DamageResult(0, false);
        }

        if (multiplier <= 0) {
            return new DamageResult(0, false);
        }

        // ==================================================
        // BOW ATTACK
        // ==================================================

        double bowAttack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.BOW_ATTACK
                );

        // ==================================================
        // CRIT
        // ==================================================

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.CRIT_DAMAGE
                );

        double critMultiplier =
                normalizeCritDamage(critDamage);

        // ==================================================
        // CALCULATE
        // ==================================================

        DamageResult result =
                damageCalculator.calculateBow(
                        bowAttack,
                        critChance,
                        critMultiplier,
                        multiplier
                );

        // ==================================================
        // PHYSICAL DEFENSE
        // ==================================================

        double defense =
                getTargetDefense(target);

        // ==================================================
        // ARMOR PENETRATION
        // ==================================================

        double armorPenetration =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.ARMOR_PENETRATION
                );

        // ==================================================
        // APPLY DEFENSE
        // ==================================================

        double finalDamage =
                damageCalculator.applyDefense(
                        result.getDamage(),
                        defense,
                        armorPenetration
                );

        // ==================================================
        // DAMAGE REDUCTION
        // ==================================================

        double damageReduction =
                getTargetDamageReduction(target);

        finalDamage =
                damageCalculator.applyDamageReduction(
                        finalDamage,
                        damageReduction
                );

        applyLifesteal(
                attacker,
                finalDamage
        );

        return new DamageResult(
                finalDamage,
                result.isCritical()
        );
    }

    // ==================================================
    // LIFESTEAL
    // ==================================================

    /**
     * Lifesteal is based on the attacker's own MAX HP, not damage dealt.
     *
     * 10 points = 1% of max HP healed.
     * Therefore:
     *   heal = maxHealth * lifesteal / 1000
     */
    private void applyLifesteal(
            Player attacker,
            double finalDamage
    ) {

        if (attacker == null
                || finalDamage <= 0.0
                || attacker.isDead()) {
            return;
        }

        double lifesteal =
                statManager.getStat(
                        attacker.getUniqueId(),
                        StatType.LIFESTEAL
                );

        if (lifesteal <= 0.0) {
            return;
        }

        // Lifesteal now has a fixed 15% proc chance per successful RPG hit.
        if (Math.random() >= 0.15) {
            return;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(attacker);

        if (data == null) {
            return;
        }

        double maxHealth =
                plugin.getPlayerManager()
                        .getEffectiveMaxHealth(
                                attacker.getUniqueId()
                        );

        double heal =
                maxHealth *
                        lifesteal /
                        1000.0;

        if (heal <= 0.0) {
            return;
        }

        double newHealth =
                Math.min(
                        maxHealth,
                        attacker.getHealth() + heal
                );

        if (newHealth > attacker.getHealth()) {
            attacker.setHealth(newHealth);
        }

        data.setMaxHealth(maxHealth);
        data.setHealth(newHealth);
    }

    // ==================================================
    // TARGET DEFENSE
    // ==================================================

    private double getTargetDefense(
            LivingEntity target
    ) {

        if (!(target instanceof Player player)) {

            /*
             * Mob sẽ được nối với
             * MobStatsManager sau.
             */
            return 0;
        }

        return statManager.getStat(
                player.getUniqueId(),
                StatType.DEFENSE
        );
    }

    // ==================================================
    // TARGET MAGIC DEFENSE
    // ==================================================

    private double getTargetMagicDefense(
            LivingEntity target
    ) {

        if (!(target instanceof Player player)) {

            return 0;
        }

        return statManager.getStat(
                player.getUniqueId(),
                StatType.MAGIC_DEFENSE
        );
    }

    // ==================================================
    // TARGET DAMAGE REDUCTION
    // ==================================================

    private double getTargetDamageReduction(
            LivingEntity target
    ) {

        if (!(target instanceof Player player)) {

            return 0;
        }

        return statManager.getStat(
                player.getUniqueId(),
                StatType.DAMAGE_REDUCTION
        );
    }

    // ==================================================
    // CRIT DAMAGE
    // ==================================================

    private double normalizeCritDamage(
            double critDamage
    ) {

        /*
         * StatManager lưu Crit Damage theo %.
         *
         * 150 → 1.5x
         * 200 → 2.0x
         *
         * Nếu chưa có Crit Damage:
         * → 1.0x
         */

        if (critDamage <= 0) {

            return 1.0;
        }

        /*
         * CRIT_DAMAGE được lưu thống nhất theo %.
         *
         * 150 = 150% = 1.5x
         * 175 = 175% = 1.75x
         *
         * Không dùng 1.5 ở PlayerStats nữa,
         * tránh trường hợp stat hiển thị sai kiểu 1700%.
         */
        return critDamage / 100.0;
    }
}