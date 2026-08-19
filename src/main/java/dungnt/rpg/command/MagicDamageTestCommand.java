//package dungnt.rpg.command;
//
//import dungnt.rpg.MyRPG;
//import dungnt.rpg.combat.DamageResult;
//import dungnt.rpg.stats.ModifierType;
//import dungnt.rpg.stats.StatModifier;
//import dungnt.rpg.stats.StatType;
//
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.LivingEntity;
//import org.bukkit.entity.Player;
//
//public class MagicDamageTestCommand
//        implements CommandExecutor {
//
//    private final MyRPG plugin;
//
//    public MagicDamageTestCommand(MyRPG plugin) {
//        this.plugin = plugin;
//    }
//
//    @Override
//    public boolean onCommand(
//            CommandSender sender,
//            Command command,
//            String label,
//            String[] args
//    ) {
//
//        if (!(sender instanceof Player player)) {
//            return true;
//        }
//
//        // =========================
//        // TARGET
//        // =========================
//
//        Entity targetEntity =
//                player.getTargetEntity(10);
//
//        if (!(targetEntity instanceof LivingEntity target)) {
//
//            player.sendMessage(
//                    "§cHãy nhìn vào một mob!"
//            );
//
//            return true;
//        }
//
//        // =========================
//        // TEST MAGIC ATTACK
//        // =========================
//
//        plugin.getStatManager().addModifier(
//                player.getUniqueId(),
//
//                new StatModifier(
//                        "test_magic_attack",
//                        StatType.MAGIC_ATTACK,
//                        ModifierType.FLAT,
//                        0
//                )
//        );
//
//        // =========================
//        // TEST MAGIC PENETRATION
//        // =========================
//
//        plugin.getStatManager().addModifier(
//                player.getUniqueId(),
//
//                new StatModifier(
//                        "test_magic_penetration",
//                        StatType.MAGIC_PENETRATION,
//                        ModifierType.FLAT,
//                        0
//                )
//        );
//
//        // =========================
//        // MAGIC DAMAGE
//        // =========================
//
//        DamageResult result =
//                plugin.getCombatService()
//                        .magicDamage(
//                                player,
//                                target,
//                                1.0
//                        );
//
//        // =========================
//        // APPLY REAL DAMAGE
//        // =========================
//        //
//        // Dùng target.damage(damage) KHÔNG truyền Player
//        // để tránh EntityDamageByEntityEvent quay lại
//        // RPGCombatListener và bị tính thành physical damage.
//        //
//        if (result.getDamage() > 0) {
//
//            /*
//             * Hiện floating trước khi trừ HP để vẫn thấy
//             * damage ngay cả khi đòn đánh giết chết mob.
//             */
//            plugin.getFloatingDamage()
//                    .show(
//                            target,
//                            result.getDamage(),
//                            result.isCritical(),
//                            true
//                    );
//
//            /*
//             * Không truyền Player làm damager.
//             * Nếu truyền Player, EntityDamageByEntityEvent
//             * sẽ quay lại RPGCombatListener và bị tính
//             * thành physical damage.
//             */
//            target.damage(
//                    result.getDamage()
//            );
//        }
//
//        // =========================
//        // RESULT
//        // =========================
//
//        player.sendMessage(
//                "§d§l===== MAGIC TEST ====="
//        );
//
//        player.sendMessage(
//                "§5Magic Damage: §f"
//                        + String.format(
//                        "%.1f",
//                        result.getDamage()
//                )
//        );
//
//        player.sendMessage(
//                "§6Critical: §f"
//                        + result.isCritical()
//        );
//
//        player.sendMessage(
//                "§7Magic Attack: §f30"
//        );
//
//        player.sendMessage(
//                "§7Magic Penetration: §f0%"
//        );
//
//        // =========================
//        // MOB INFO
//        // =========================
//
//        dungnt.rpg.mob.MobData mobData =
//                plugin.getMobManager()
//                        .getMob(target);
//
//        if (mobData != null) {
//
//            player.sendMessage(
//                    "§7Mob: §f"
//                            + mobData.getId()
//            );
//
//            player.sendMessage(
//                    "§7Magic Defense: §f"
//                            + mobData
//                            .getStats()
//                            .getMagicDefense()
//            );
//        }
//
//        return true;
//    }
//}

package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.DamageResult;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class MagicDamageTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    // =========================
    // CONFIG
    // =========================

    private static final double RANGE = 15.0;

    // Khoảng cách giữa mỗi điểm kiểm tra tia
    private static final double STEP = 0.15;

    // Bán kính để tia được tính là chạm mob
    private static final double HIT_RADIUS = 0.45;

    public MagicDamageTestCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {
            return true;
        }

        // =========================
        // RAY START
        // =========================

        Location start = player.getEyeLocation();

        Vector direction = start.getDirection().normalize();

        LivingEntity target = null;
        Location hitLocation = null;

        // =========================
        // SEARCH TARGET
        // =========================

        for (double distance = 0; distance <= RANGE; distance += STEP) {

            Location point = start.clone().add(
                    direction.clone().multiply(distance)
            );

            // =========================
            // PARTICLE
            // =========================

            showMagicParticle(point);

            // =========================
            // CHECK ENTITY
            // =========================

            for (Entity entity : point.getWorld().getNearbyEntities(
                    point,
                    HIT_RADIUS,
                    HIT_RADIUS,
                    HIT_RADIUS
            )) {

                // Không đánh chính mình
                if (entity.equals(player)) {
                    continue;
                }

                if (!(entity instanceof LivingEntity livingEntity)) {
                    continue;
                }

                // Không đánh entity đã chết
                if (livingEntity.isDead()) {
                    continue;
                }

                target = livingEntity;
                hitLocation = point.clone();

                break;
            }

            if (target != null) {
                break;
            }

            // =========================
            // STOP AT BLOCK
            // =========================

            if (!point.getBlock().isPassable()) {

                showHitEffect(point);

                break;
            }
        }

        // =========================
        // NO TARGET
        // =========================

        if (target == null) {

            player.sendMessage(
                    "§7§l[Magic] §fKhông trúng mục tiêu!"
            );

            return true;
        }

        // =========================
        // HIT EFFECT
        // =========================

        if (hitLocation != null) {
            showHitEffect(hitLocation);
        }

        // =========================
        // TEST MAGIC ATTACK
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),

                new StatModifier(
                        "test_magic_attack",
                        StatType.MAGIC_ATTACK,
                        ModifierType.FLAT,
                        0
                )
        );

        // =========================
        // TEST MAGIC PENETRATION
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),

                new StatModifier(
                        "test_magic_penetration",
                        StatType.MAGIC_PENETRATION,
                        ModifierType.FLAT,
                        0
                )
        );

        // =========================
        // MAGIC DAMAGE
        // =========================

        DamageResult result =
                plugin.getCombatService()
                        .magicDamage(
                                player,
                                target,
                                1.0
                        );

        // Creative players are immune to RPG test damage.
        if (target instanceof Player targetPlayer
                && targetPlayer.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            player.sendMessage("§cKhông thể gây damage lên người chơi.");
            return true;
        }

        // =========================
        // FLOATING DAMAGE
        // =========================

        if (result.getDamage() > 0) {

            plugin.getFloatingDamage()
                    .show(
                            target,
                            result.getDamage(),
                            result.isCritical(),
                            true
                    );
        }

        // =========================
        // APPLY RPG DAMAGE
        // =========================
        //
        // Không setHealth(0). Test command phải trừ đúng lượng
        // damage do MAGIC_ATTACK của player tạo ra.
        // Dùng setHealth trực tiếp để tránh EntityDamageEvent
        // quay lại combat listener và tính damage lần thứ hai.
        //
        if (result.getDamage() > 0 && !target.isDead()) {

            double remainingHealth =
                    Math.max(
                            0.0,
                            target.getHealth() - result.getDamage()
                    );

            target.setHealth(remainingHealth);
        }

        // =========================
        // RESULT
        // =========================

//        player.sendMessage(
//                "§d§l===== MAGIC TEST ====="
//        );
//
//        player.sendMessage(
//                "§5Magic Damage: §f"
//                        + String.format(
//                        "%.1f",
//                        result.getDamage()
//                )
//        );
//
//        player.sendMessage(
//                "§6Critical: §f"
//                        + result.isCritical()
//        );
//        player.sendMessage(
//                "§7Magic Penetration: §f0%"
//        );
//
//        player.sendMessage(
//                "§dTarget: §f"
//                        + target.getName()
//        );
//
//        player.sendMessage(
//                "§a§l✦ MAGIC HIT!"
//        );

        // =========================
        // MOB INFO
        // =========================

        dungnt.rpg.mob.MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            player.sendMessage(
                    "§7Mob: §f"
                            + mobData.getId()
            );

            player.sendMessage(
                    "§7Magic Defense: §f"
                            + mobData
                            .getStats()
                            .getMagicDefense()
            );
        }

        return true;
    }

    // =====================================================
    // MAGIC BEAM PARTICLE
    // =====================================================

    private void showMagicParticle(Location location) {

        // Tia tím chính
        location.getWorld().spawnParticle(
                Particle.DUST,
                location,
                2,
                0.02,
                0.02,
                0.02,
                0,
                new Particle.DustOptions(
                        Color.fromRGB(170, 0, 255),
                        1.2f
                )
        );

        // Hạt lấp lánh
        location.getWorld().spawnParticle(
                Particle.END_ROD,
                location,
                1,
                0.02,
                0.02,
                0.02,
                0.01
        );

        // Hơi phép tím
        location.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                location,
                1,
                0.03,
                0.03,
                0.03,
                0.0,
                0.0f
        );
    }

    // =====================================================
    // HIT EFFECT
    // =====================================================

    private void showHitEffect(Location location) {

        location.getWorld().spawnParticle(
                Particle.DRAGON_BREATH,
                location,
                25,
                0.25,
                0.25,
                0.25,
                0.05,
                0.0f
        );

        location.getWorld().spawnParticle(
                Particle.END_ROD,
                location,
                15,
                0.2,
                0.2,
                0.2,
                0.08
        );

        location.getWorld().spawnParticle(
                Particle.DUST,
                location,
                20,
                0.25,
                0.25,
                0.25,
                0,
                new Particle.DustOptions(
                        Color.fromRGB(200, 50, 255),
                        1.5f
                )
        );
    }
}