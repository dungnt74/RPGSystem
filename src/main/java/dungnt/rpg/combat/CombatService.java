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

    // =========================
    // PLAYER -> TARGET
    // =========================

    // =========================
// MAGIC DAMAGE
// =========================

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
                damageCalculator.calculate(
                        magicAttack,
                        critChance,
                        critDamage,
                        skillMultiplier
                );

        double damage =
                result.getDamage();

        // =========================
        // MAGIC DEFENSE
        // =========================

        double magicDefense =
                getMagicDefense(target);

        damage =
                damageCalculator.applyDefense(
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
        // DODGE
        // =========================

        double dodgeChance =
                getDodgeChance(target);

        if (damageCalculator.isDodged(dodgeChance)) {

            if (target instanceof Player player) {

                player.sendMessage(
                        "§b§l✦ DODGE! §7Bạn đã né phép."
                );
            }

            return new DamageResult(
                    0,
                    result.isCritical()
            );
        }

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

        target.damage(
                damage,
                attacker
        );

        return new DamageResult(
                damage,
                result.isCritical()
        );
    }

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

            if (target instanceof Player player) {

                player.sendMessage(
                        "§b§l✦ DODGE! §7Bạn đã né đòn."
                );
            }

            return new DamageResult(
                    0,
                    result.isCritical()
            );
        }

        // =========================
        // TARGET DEFENSE
        // =========================

        double defense =
                getDefense(target);

        // =========================
        // DEFENSE
        // =========================

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

        target.damage(
                damage,
                attacker
        );

        return new DamageResult(
                damage,
                result.isCritical()
        );
    }

    private double getMagicDefense(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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

    // =========================
    // GET DEFENSE
    // =========================

    private double getDefense(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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

    // =========================
    // GET DAMAGE REDUCTION
    // =========================

    private double getDamageReduction(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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

    // =========================
    // GET BLOCK CHANCE
    // =========================

    private double getBlockChance(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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

    // =========================
    // GET BLOCK POWER
    // =========================

    private double getBlockPower(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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

    private double getDodgeChance(
            LivingEntity target
    ) {

        // PLAYER
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

        // RPG MOB
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