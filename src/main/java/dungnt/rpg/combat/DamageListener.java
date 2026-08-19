package dungnt.rpg.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {

        Entity victim = event.getEntity();
        Entity damager = event.getDamager();

        if (!(victim instanceof LivingEntity)) {
            return;
        }

        // Chỉ debug damage do Player gây ra
        if (!(damager instanceof Player player)) {
            return;
        }

        double damage = event.getDamage();

        player.sendMessage(
                "§8[§cDEBUG DAMAGE§8] "
                        + "§7Target: §f"
                        + victim.getName()
                        + " §7| Damage: §c"
                        + String.format("%.1f", damage)
        );
    }
}