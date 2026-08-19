package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import dungnt.rpg.stats.StatType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class RPGCombatListener implements Listener {

    private final MyRPG plugin;

    /*
     * Last direct RPG attack time for each player.
     * nanoTime is monotonic and is not affected by system clock changes.
     */
    private final Map<UUID, Long> lastAttackTimes =
            new HashMap<>();

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

        // Creative players must remain completely immune to RPG player damage.
        if (target instanceof Player targetPlayer
                && targetPlayer.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            return;
        }

        /*
         * CHỈ xử lý đòn đánh tay trực tiếp (ENTITY_ATTACK).
         *
         * Minecraft vanilla còn bắn thêm một
         * EntityDamageByEntityEvent riêng cho "sweep attack"
         * (kiếm chém trúng thêm entity xung quanh) với damager
         * vẫn là Player. Nếu không lọc theo cause ở đây,
         * mỗi cú chém sẽ:
         *   - Bị tính lại thành damage RPG đầy đủ thêm 1 lần nữa
         *     cho CÙNG một cú đánh (hiện số damage 2-3 lần).
         *   - Hoặc để lọt damage vanilla (chưa qua CombatService)
         *     nếu cause đó không được xử lý ở đâu khác.
         *
         * Ta hủy hẳn phần sweep damage: RPG combat không mô
         * phỏng damage diện rộng riêng, tránh vừa bị nhân đôi
         * số vừa lộ ra một con số "vanilla" không được tính.
         */
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

            event.setCancelled(true);

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
        /*
         * ATTACK SPEED / ANTI-SPAM
         *
         * Base interval = 1.0 second.
         * Every 10 ATTACK_SPEED removes 0.1 second.
         *
         * 20 ATTACK_SPEED:
         *   1.0 - (20 * 0.01) = 0.8 second.
         *
         * If the player attacks again before the interval:
         *   damage is randomly reduced by 30% - 50%.
         *
         * Crit is calculated AFTER this multiplier inside
         * CombatService, so the crit also uses the reduced damage.
         */
        double attackSpeed =
                plugin.getStatManager()
                        .getStat(
                                player.getUniqueId(),
                                StatType.ATTACK_SPEED
                        );

        double attackInterval =
                Math.max(
                        0.1,
                        1.0 - attackSpeed * 0.01
                );

        long now =
                System.nanoTime();

        Long lastAttack =
                lastAttackTimes.put(
                        player.getUniqueId(),
                        now
                );

        double attackMultiplier = 1.0;

        if (lastAttack != null) {

            double elapsedSeconds =
                    (now - lastAttack) /
                            1_000_000_000.0;

            if (elapsedSeconds < attackInterval) {

                double reduction =
                        ThreadLocalRandom
                                .current()
                                .nextDouble(
                                        0.30,
                                        0.50
                                );

                attackMultiplier =
                        1.0 - reduction;
            }
        }

        DamageResult result =
                plugin.getCombatService()
                        .damage(
                                player,
                                target,
                                attackMultiplier
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
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastAttackTimes.remove(
                event.getPlayer().getUniqueId()
        );
    }
}