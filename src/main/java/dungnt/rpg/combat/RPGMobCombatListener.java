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

public class RPGMobCombatListener implements Listener {

    private final MyRPG plugin;
    private final StatManager statManager;

    public RPGMobCombatListener(MyRPG plugin) {
        this.plugin = plugin;
        this.statManager = plugin.getStatManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMobAttack(
            EntityDamageByEntityEvent event
    ) {

        // =========================
        // ATTACKER
        // =========================

        if (!(event.getDamager() instanceof LivingEntity attacker)) {
            return;
        }

        // =========================
        // TARGET
        // =========================

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // =========================
        // KIỂM TRA RPG MOB
        // =========================

        MobData mobData =
                plugin.getMobManager()
                        .getMob(attacker);

        if (mobData == null) {
            return;
        }

        MobStats mobStats =
                mobData.getStats();

        // =========================
        // MOB ATTACK
        // =========================

        double attack =
                mobStats.getAttack();

        if (attack <= 0) {
            event.setCancelled(true);
            return;
        }

        // =========================
        // PLAYER DATA
        // =========================

        PlayerData playerData =
                plugin.getPlayerManager()
                        .getData(player);

        if (playerData == null) {
            return;
        }

        PlayerStats playerStats =
                playerData.getStats();

        // =========================
        // PLAYER DEFENSE
        // =========================

        double defense =
                statManager.getStat(
                        player.getUniqueId(),
                        playerStats,
                        StatType.DEFENSE
                );

        // =========================
        // CALCULATE DAMAGE
        // =========================

        double damage =
                plugin.getDamageCalculator()
                        .applyDefense(
                                attack,
                                defense,
                                0
                        );

        // =========================
        // DEBUG
        // =========================

        player.sendMessage(
                "§c[DEBUG] "
                        + mobData.getId()
                        + " Attack: "
                        + String.format("%.1f", attack)
                        + " §7| §bDefense: "
                        + String.format("%.1f", defense)
                        + " §7| §cDamage: "
                        + String.format("%.1f", damage)
        );

        // =========================
        // APPLY DAMAGE
        // =========================

        event.setDamage(damage);
    }
}