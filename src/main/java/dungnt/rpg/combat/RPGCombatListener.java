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

        // ==================================================
        // CHỈ PLAYER ĐÁNH
        // ==================================================

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        // ==================================================
        // TARGET
        // ==================================================

        if (!(event.getEntity() instanceof LivingEntity target)) {
            return;
        }

        // ==================================================
        // HỦY DAMAGE VANILLA
        // ==================================================

        event.setCancelled(true);

        // ==================================================
        // RPG PHYSICAL DAMAGE
        // ==================================================

        DamageResult result =
                plugin.getCombatService()
                        .damage(
                                player,
                                target,
                                1.0
                        );

        // ==================================================
        // DAMAGE MESSAGE
        // ==================================================

        if (result.getDamage() <= 0) {

            player.sendMessage(
                    "§7✦ Đòn đánh không gây damage."
            );

            return;
        }

        // ==================================================
        // CRITICAL
        // ==================================================

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

        // ==================================================
        // NORMAL DAMAGE
        // ==================================================

        player.sendMessage(
                "§c⚔ Damage §f→ §e"
                        + String.format(
                        "%.1f",
                        result.getDamage()
                )
        );
    }
}