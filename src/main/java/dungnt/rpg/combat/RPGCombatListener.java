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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RPGCombatListener implements Listener {

    private final MyRPG plugin;
    private final StatManager statManager;

    public RPGCombatListener(MyRPG plugin) {
        this.plugin = plugin;
        this.statManager = plugin.getStatManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAttack(
            EntityDamageByEntityEvent event
    ) {

        // =========================
        // PLAYER ATTACK
        // =========================

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        // =========================
        // TARGET
        // =========================

        if (!(event.getEntity() instanceof LivingEntity target)) {
            return;
        }

        // =========================
        // PLAYER DATA
        // =========================

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            return;
        }

        PlayerStats stats =
                data.getStats();

        // =========================
        // RPG ATTACK
        // =========================

        double attack =
                statManager.getStat(
                        player.getUniqueId(),
                        stats,
                        StatType.ATTACK
                );

        if (attack <= 0) {
            event.setCancelled(true);
            return;
        }

        // =========================
        // CRIT
        // =========================

        double critChance =
                statManager.getStat(
                        player.getUniqueId(),
                        stats,
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                statManager.getStat(
                        player.getUniqueId(),
                        stats,
                        StatType.CRIT_DAMAGE
                );

        // =========================
        // ARMOR PENETRATION
        // =========================

        double armorPenetration =
                statManager.getStat(
                        player.getUniqueId(),
                        stats,
                        StatType.ARMOR_PENETRATION
                );

        // =========================
        // CALCULATE DAMAGE
        // =========================

        DamageResult result =
                plugin.getDamageCalculator()
                        .calculate(
                                attack,
                                critChance,
                                critDamage,
                                1.0
                        );

        double damage =
                result.getDamage();

        // =========================
        // RPG MOB DEFENSE
        // =========================

        MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            MobStats mobStats =
                    mobData.getStats();

            double defense =
                    mobStats.getDefense();

            player.sendMessage(
                    "§b[DEBUG] Mob: "
                            + mobData.getId()
                            + " | Defense: "
                            + defense
                            + " | Armor Pen: "
                            + armorPenetration
                            + "% | Damage trước: "
                            + String.format("%.1f", damage)
            );

            // =========================
            // APPLY DEFENSE + PENETRATION
            // =========================

            damage =
                    plugin.getDamageCalculator()
                            .applyDefense(
                                    damage,
                                    defense,
                                    armorPenetration
                            );

            player.sendMessage(
                    "§a[DEBUG] Damage sau Defense: "
                            + String.format("%.1f", damage)
            );
        }

        // =========================
        // APPLY FINAL DAMAGE
        // =========================

        event.setDamage(damage);

        // =========================
        // CRIT MESSAGE
        // =========================

        if (result.isCritical()) {

            player.sendMessage(
                    "§6§l✦ CRITICAL! §f"
                            + String.format(
                            "%.1f",
                            result.getDamage()
                    )
            );
        }
    }
}