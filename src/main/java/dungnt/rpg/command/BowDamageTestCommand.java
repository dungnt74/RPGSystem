package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.DamageResult;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BowDamageTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    // =========================
    // CONFIG
    // =========================

    private static final double RANGE = 30.0;
    private static final double STEP = 0.15;
    private static final double HIT_RADIUS = 0.45;
    private static final double ARROW_SPEED = 3.0;

    public BowDamageTestCommand(MyRPG plugin) {
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
        // FIND TARGET
        // =========================

        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        LivingEntity target = null;
        Location hitLocation = null;

        for (double distance = 0; distance <= RANGE; distance += STEP) {

            Location point = start.clone().add(
                    direction.clone().multiply(distance)
            );

            for (Entity entity : point.getWorld().getNearbyEntities(
                    point,
                    HIT_RADIUS,
                    HIT_RADIUS,
                    HIT_RADIUS
            )) {

                if (entity.equals(player)) {
                    continue;
                }

                if (!(entity instanceof LivingEntity livingEntity)) {
                    continue;
                }

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

            if (!point.getBlock().isPassable()) {
                break;
            }
        }

        if (target == null) {
            player.sendMessage(
                    "§7§l[Bow] §fKhông trúng mục tiêu!"
            );
            return true;
        }

        // =========================
        // VISUAL ARROW
        // =========================

        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setVelocity(direction.clone().multiply(ARROW_SPEED));
        arrow.setDamage(0);
        arrow.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);
        arrow.setCritical(true);

        // Test command: arrow chỉ để tạo cảm giác bắn.
        // Damage RPG được tính riêng bên dưới để không bị
        // vanilla arrow damage cộng thêm.
        plugin.getServer().getScheduler().runTaskLater(
                plugin,
                arrow::remove,
                10L
        );

        // =========================
        // TEST BOW ATTACK
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new StatModifier(
                        "test_bow_attack",
                        StatType.BOW_ATTACK,
                        ModifierType.FLAT,
                        0
                )
        );

        // =========================
        // TEST ARMOR PENETRATION
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new StatModifier(
                        "test_bow_armor_penetration",
                        StatType.ARMOR_PENETRATION,
                        ModifierType.FLAT,
                        0
                )
        );

        // =========================
        // BOW DAMAGE
        // =========================

        DamageResult result =
                plugin.getCombatService()
                        .bowDamage(
                                player,
                                target,
                                1.0
                        );

        // =========================
        // HIT EFFECT
        // =========================

        if (hitLocation != null) {
            player.getWorld().spawnParticle(
                    org.bukkit.Particle.CRIT,
                    hitLocation,
                    12,
                    0.15,
                    0.15,
                    0.15,
                    0.05
            );
        }

        // Creative players are immune to RPG test damage.
        if (target instanceof Player targetPlayer
                && targetPlayer.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            player.sendMessage("§cKhông thể gây damage lên người chơi này.");
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
                            false
                    );
        }

        // =========================
        // APPLY RPG DAMAGE
        // =========================
        //
        // Không setHealth(0): command test phải gây đúng lượng
        // damage mà CombatService đã tính từ BOW_ATTACK của player.
        // Dùng setHealth trực tiếp để không tạo EntityDamageEvent
        // và không bị hệ thống combat tính damage lần thứ hai.
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
//                "§6§l===== BOW TEST ====="
//        );
//
//        player.sendMessage(
//                "§eBow Damage: §f"
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
//                "§7Bow Attack: §f"
//                        + String.format(
//                        "%.1f",
//                        plugin.getStatManager().getStat(
//                                player.getUniqueId(),
//                                StatType.BOW_ATTACK
//                        )
//                )
//        );
//
//        player.sendMessage(
//                "§7Armor Penetration: §f"
//                        + String.format(
//                        "%.1f%%",
//                        plugin.getStatManager().getStat(
//                                player.getUniqueId(),
//                                StatType.ARMOR_PENETRATION
//                        )
//                )
//        );

        // =========================
        // MOB INFO
        // =========================

        dungnt.rpg.mob.MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {
            player.sendMessage(
                    "§7Mob: §f" + mobData.getId()
            );

            player.sendMessage(
                    "§7Defense: §f"
                            + mobData.getStats().getDefense()
            );
        }

        return true;
    }
}
