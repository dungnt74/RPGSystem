package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;
import dungnt.rpg.mob.MobData;
import dungnt.rpg.mob.MobStats;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.player.PlayerStats;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatType;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CombatService {

    private final MyRPG plugin;
    private final DamageCalculator damageCalculator;
    private final StatManager statManager;

    public CombatService(MyRPG plugin) {

        this.plugin = plugin;

        this.damageCalculator =
                plugin.getDamageCalculator();

        this.statManager =
                plugin.getStatManager();
    }

    // ==================================================
    // MAGIC DAMAGE
    // ==================================================

    public DamageResult magicDamage(
            Player attacker,
            LivingEntity target,
            double skillMultiplier
    ) {

        PlayerData attackerData =
                plugin.getPlayerManager()
                        .getData(attacker);

        if (attackerData == null) {
            return new DamageResult(0, false);
        }

        PlayerStats attackerStats =
                attackerData.getStats();

        // =========================
        // MAGIC ATTACK
        // =========================

        double magicAttack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.MAGIC_ATTACK
                );

        if (magicAttack <= 0) {
            return new DamageResult(0, false);
        }

        // =========================
        // CRIT
        // =========================

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.CRIT_DAMAGE
                );

        // =========================
        // MAGIC PENETRATION
        // =========================

        double magicPenetration =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.MAGIC_PENETRATION
                );

        // =========================
        // RAW MAGIC DAMAGE
        // =========================

        DamageResult result =
                damageCalculator.calculateMagic(
                        magicAttack,
                        critChance,
                        critDamage,
                        skillMultiplier
                );

        double damage =
                result.getDamage();

        // =========================
        // DODGE
        // =========================

        double dodgeChance =
                getDodgeChance(target);

        if (damageCalculator.isDodged(dodgeChance)) {

            attacker.sendMessage(
                    "§b§l✦ DODGE!"
            );

            return new DamageResult(
                    0,
                    result.isCritical()
            );
        }

        // =========================
        // MAGIC DEFENSE
        // =========================

        double magicDefense =
                getMagicDefense(target);

        damage =
                damageCalculator.applyMagicDefense(
                        damage,
                        magicDefense,
                        magicPenetration
                );

        // =========================
        // DAMAGE REDUCTION
        // =========================

        double damageReduction =
                getDamageReduction(target);

        damage =
                damageCalculator.applyDamageReduction(
                        damage,
                        damageReduction
                );

        // =========================
        // BLOCK
        // =========================

        double blockChance =
                getBlockChance(target);

        double blockPower =
                getBlockPower(target);

        damage =
                damageCalculator.applyBlock(
                        damage,
                        blockChance,
                        blockPower
                );

        // =========================
        // FINAL DAMAGE
        // =========================

        damage =
                Math.max(
                        0,
                        damage
                );

        // =========================
        // APPLY RPG DAMAGE
        // =========================

        applyRpgDamage(
                target,
                damage
        );

        // =========================
        // FLOATING MAGIC DAMAGE
        // =========================

        if (damage > 0) {

            plugin.getFloatingDamage().show(
                    target,
                    damage,
                    result.isCritical(),
                    true
            );
        }

        return new DamageResult(
                damage,
                result.isCritical()
        );
    }

    // ==================================================
    // PHYSICAL DAMAGE
    // ==================================================

    public DamageResult damage(
            Player attacker,
            LivingEntity target,
            double skillMultiplier
    ) {

        PlayerData attackerData =
                plugin.getPlayerManager()
                        .getData(attacker);

        if (attackerData == null) {
            return new DamageResult(0, false);
        }

        PlayerStats attackerStats =
                attackerData.getStats();

        // =========================
        // ATTACK
        // =========================

        double attack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.ATTACK
                );

        if (attack <= 0) {
            return new DamageResult(0, false);
        }

        // =========================
        // CRIT
        // =========================

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.CRIT_DAMAGE
                );

        // =========================
        // ARMOR PENETRATION
        // =========================

        double armorPenetration =
                statManager.getStat(
                        attacker.getUniqueId(),
                        attackerStats,
                        StatType.ARMOR_PENETRATION
                );

        // =========================
        // RAW DAMAGE
        // =========================

        DamageResult result =
                damageCalculator.calculate(
                        attack,
                        critChance,
                        critDamage,
                        skillMultiplier
                );

        double damage =
                result.getDamage();

        // =========================
        // DODGE
        // =========================

        double dodgeChance =
                getDodgeChance(target);

        if (damageCalculator.isDodged(dodgeChance)) {

            attacker.sendMessage(
                    "§b§l✦ DODGE!"
            );

            return new DamageResult(
                    0,
                    result.isCritical()
            );
        }

        // =========================
        // DEFENSE
        // =========================

        double defense =
                getDefense(target);

        damage =
                damageCalculator.applyDefense(
                        damage,
                        defense,
                        armorPenetration
                );

        // =========================
        // DAMAGE REDUCTION
        // =========================

        double damageReduction =
                getDamageReduction(target);

        damage =
                damageCalculator.applyDamageReduction(
                        damage,
                        damageReduction
                );

        // =========================
        // BLOCK
        // =========================

        double blockChance =
                getBlockChance(target);

        double blockPower =
                getBlockPower(target);

        damage =
                damageCalculator.applyBlock(
                        damage,
                        blockChance,
                        blockPower
                );

        // =========================
        // FINAL DAMAGE
        // =========================

        damage =
                Math.max(
                        0,
                        damage
                );

        // =========================
        // APPLY RPG DAMAGE
        // =========================

        applyRpgDamage(
                target,
                damage
        );

        // =========================
        // FLOATING PHYSICAL DAMAGE
        // =========================

        if (damage > 0) {

            plugin.getFloatingDamage().show(
                    target,
                    damage,
                    result.isCritical(),
                    false
            );
        }

        return new DamageResult(
                damage,
                result.isCritical()
        );
    }

    // ==================================================
    // APPLY RPG DAMAGE
    // ==================================================

    private void applyRpgDamage(
            LivingEntity target,
            double damage
    ) {

        if (damage <= 0 || target.isDead()) {
            return;
        }

        double currentHealth =
                target.getHealth();

        double newHealth =
                Math.max(
                        0,
                        currentHealth - damage
                );

        // Chỉ trừ HP RPG.
        //
        // KHÔNG dùng:
        //
        // target.damage(damage);
        //
        // vì sẽ tạo EntityDamageByEntityEvent
        // và có thể gây double damage.

        target.setHealth(newHealth);
    }

    // ==================================================
    // MAGIC DEFENSE
    // ==================================================

    private double getMagicDefense(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.MAGIC_DEFENSE
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            return mobData
                    .getStats()
                    .getMagicDefense();
        }

        return 0.0;
    }

    // ==================================================
    // PHYSICAL DEFENSE
    // ==================================================

    private double getDefense(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.DEFENSE
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            MobStats mobStats =
                    mobData.getStats();

            return mobStats.getDefense();
        }

        return 0.0;
    }

    // ==================================================
    // DAMAGE REDUCTION
    // ==================================================

    private double getDamageReduction(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.DAMAGE_REDUCTION
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            return mobData
                    .getStats()
                    .getDamageReduction();
        }

        return 0.0;
    }

    // ==================================================
    // BLOCK CHANCE
    // ==================================================

    private double getBlockChance(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.BLOCK_CHANCE
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            return mobData
                    .getStats()
                    .getBlockChance();
        }

        return 0.0;
    }

    // ==================================================
    // BLOCK POWER
    // ==================================================

    private double getBlockPower(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.BLOCK_POWER
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            return mobData
                    .getStats()
                    .getBlockPower();
        }

        return 0.0;
    }

    // ==================================================
    // DODGE CHANCE
    // ==================================================

    private double getDodgeChance(
            LivingEntity target
    ) {

        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager()
                            .getData(player);

            if (playerData == null) {
                return 0.0;
            }

            return statManager.getStat(
                    player.getUniqueId(),
                    playerData.getStats(),
                    StatType.DODGE_CHANCE
            );
        }

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            return mobData
                    .getStats()
                    .getDodgeChance();
        }

        return 0.0;
    }
}