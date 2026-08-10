package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;
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
        this.damageCalculator = plugin.getDamageCalculator();

        this.statManager =
                plugin.getStatManager();
    }

    public DamageResult damage(
            Player attacker,
            LivingEntity target,
            double skillMultiplier
    ) {

        PlayerData attackerData =
                plugin.getPlayerManager().getData(attacker);

        PlayerStats baseStats =
                attackerData.getStats();

        double attack =
                statManager.getStat(
                        attacker.getUniqueId(),
                        baseStats,
                        StatType.ATTACK
                );

        double critChance =
                statManager.getStat(
                        attacker.getUniqueId(),
                        baseStats,
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        attacker.getUniqueId(),
                        baseStats,
                        StatType.CRIT_DAMAGE
                );

        DamageResult result =
                damageCalculator.calculate(
                        attack,
                        critChance,
                        critDamage,
                        skillMultiplier
                );

        double defense =
                getDefense(target);

        double finalDamage =
                damageCalculator.applyDefense(
                        result.getDamage(),
                        defense
                );

        target.damage(
                finalDamage,
                attacker
        );

        return new DamageResult(
                finalDamage,
                result.isCritical()
        );
    }

    private double getDefense(LivingEntity target) {

        // Player
        if (target instanceof Player player) {

            PlayerData playerData =
                    plugin.getPlayerManager().getData(player);

            return playerData.getStats().getDefense();
        }

        // Mob hiện tại chưa có RPG Stats
        return 0.0;
    }
}