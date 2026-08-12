package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RPGCombatListener implements Listener {

    private final MyRPG plugin;

    public RPGCombatListener(MyRPG plugin) {
        this.plugin = plugin;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerAttack(
            EntityDamageByEntityEvent event
    ) {

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity target)) {
            return;
        }

        /*
         * Tắt damage vanilla.
         * RPGCombat sẽ tự tính damage.
         */
        event.setCancelled(true);

        DamageResult result =
                plugin.getCombatService()
                        .damage(
                                player,
                                target,
                                1.0
                        );

        if (result == null) {
            return;
        }

        if (result.getDamage() <= 0) {

            player.sendMessage(
                    "§7✦ Đòn đánh không gây damage."
            );

            return;
        }

        if (result.isCritical()) {

            player.sendMessage(
                    "§6§l✦ CRITICAL! §f"
                            + String.format(
                            "%.1f",
                            result.getDamage()
                    )
            );

            return;
        }

        player.sendMessage(
                "§c⚔ Damage §f→ §e"
                        + String.format(
                        "%.1f",
                        result.getDamage()
                )
        );
    }
}