package dungnt.rpg.skills;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.DamageResult;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FireballListener implements Listener {

    private final MyRPG plugin;

    /*
     * Lưu UUID của những Fireball được tạo bởi
     * skill Fireball của RPG.
     */
    private final Set<UUID> rpgFireballs =
            new HashSet<>();

    public FireballListener(
            MyRPG plugin
    ) {

        this.plugin = plugin;
    }

    // ==================================================
    // ĐĂNG KÝ FIREBALL RPG
    // ==================================================

    public void registerFireball(
            Fireball fireball
    ) {

        if (fireball == null) {
            return;
        }

        rpgFireballs.add(
                fireball.getUniqueId()
        );
    }

    // ==================================================
    // KIỂM TRA FIREBALL RPG
    // ==================================================

    private boolean isRpgFireball(
            Fireball fireball
    ) {

        if (fireball == null) {
            return false;
        }

        return rpgFireballs.contains(
                fireball.getUniqueId()
        );
    }

    // ==================================================
    // FIREBALL GÂY DAMAGE
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = false
    )
    public void onFireballDamage(
            EntityDamageByEntityEvent event
    ) {

        // ==================================================
        // DAMAGER PHẢI LÀ FIREBALL
        // ==================================================

        if (!(event.getDamager()
                instanceof Fireball fireball)) {

            return;
        }

        // ==================================================
        // CHỈ FIREBALL RPG
        // ==================================================

        if (!isRpgFireball(fireball)) {
            return;
        }

        // ==================================================
        // HỦY DAMAGE VANILLA
        // ==================================================

        event.setCancelled(true);
    }

    // ==================================================
    // FIREBALL HIT
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = false
    )
    public void onFireballHit(
            ProjectileHitEvent event
    ) {

        // ==================================================
        // PHẢI LÀ FIREBALL
        // ==================================================

        if (!(event.getEntity()
                instanceof Fireball fireball)) {

            return;
        }

        // ==================================================
        // CHỈ FIREBALL RPG
        // ==================================================

        if (!isRpgFireball(fireball)) {
            return;
        }

        // ==================================================
        // LẤY PLAYER BẮN
        // ==================================================

        if (!(fireball.getShooter()
                instanceof Player player)) {

            rpgFireballs.remove(
                    fireball.getUniqueId()
            );

            fireball.remove();

            return;
        }

        // ==================================================
        // TARGET
        // ==================================================

        if (!(event.getHitEntity()
                instanceof LivingEntity target)) {

            /*
             * Nếu bắn vào block thì không có target.
             *
             * Xóa Fireball khỏi danh sách.
             */

            rpgFireballs.remove(
                    fireball.getUniqueId()
            );

            fireball.remove();

            return;
        }

        // ==================================================
        // XÓA FIREBALL KHỎI DANH SÁCH
        // ==================================================

        rpgFireballs.remove(
                fireball.getUniqueId()
        );

        // ==================================================
        // XÓA FIREBALL
        // ==================================================

        fireball.remove();

        // ==================================================
        // RPG MAGIC DAMAGE
        // ==================================================

        DamageResult result =
                plugin.getCombatService()
                        .magicDamage(
                                player,
                                target,
                                1.0
                        );

        // ==================================================
        // MESSAGE
        // ==================================================

        if (result.getDamage() > 0) {

            // Hiện số damage phép (màu tím) phía trên đầu mob,
            // giống hệt cách physical damage hiện số màu đỏ.
            plugin.getFloatingDamage()
                    .show(
                            target,
                            result.getDamage(),
                            result.isCritical(),
                            true
                    );

            String criticalText =
                    result.isCritical()
                            ? " §6✦ CRITICAL!"
                            : "";

            player.sendMessage(
                    "§d🔥 Fireball §f→ §c"
                            + String.format(
                            "%.1f",
                            result.getDamage()
                    )
                            + " Magic Damage"
                            + criticalText
            );
        }
    }

    // ==================================================
    // CHẶN FIREBALL DAMAGE KHÁC
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = false
    )
    public void onFireballDamageGeneric(
            EntityDamageEvent event
    ) {

        /*
         * Paper 1.21.11 không có DamageCause.FIREBALL.
         *
         * Fireball entity damage đi qua
         * EntityDamageByEntityEvent với damager
         * là Fireball.
         *
         * Vì vậy phần chặn chính nằm ở
         * onFireballDamage().
         */
    }
}