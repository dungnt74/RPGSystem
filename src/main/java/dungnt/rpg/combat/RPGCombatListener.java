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
         * KHÔNG cancel event.
         *
         * Nếu cancel rồi chỉ gọi CombatService.damage(),
         * Bukkit sẽ không trừ HP thật và các listener damage
         * cũng không nhận được damage cuối cùng.
         *
         * CombatService chỉ có nhiệm vụ TÍNH damage.
         * Ở đây ta đưa damage cuối cùng vào Bukkit event
         * để Minecraft tự trừ máu.
         */
        DamageResult result =
                plugin.getCombatService()
                        .damage(
                                player,
                                target,
                                1.0
                        );

        double finalDamage =
                Math.max(
                        0,
                        result.getDamage()
                );

        event.setDamage(finalDamage);

        // Floating damage dùng đúng damage cuối cùng.
        if (finalDamage > 0) {

            plugin.getFloatingDamage()
                    .show(
                            target,
                            finalDamage,
                            result.isCritical(),
                            false
                    );
        }
    }
}
