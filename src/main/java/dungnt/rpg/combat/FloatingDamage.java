package dungnt.rpg.combat;

import dungnt.rpg.MyRPG;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class FloatingDamage {

    private final MyRPG plugin;

    public FloatingDamage(MyRPG plugin) {
        this.plugin = plugin;
    }

    // ==================================================
    // SHOW FLOATING DAMAGE
    // ==================================================

    public void show(
            LivingEntity target,
            double damage,
            boolean critical,
            boolean magic
    ) {

        // =========================
        // CHECK TARGET
        // =========================

        if (target == null || target.isDead()) {
            return;
        }

        // Không hiện số 0
        if (damage <= 0) {
            return;
        }

        // =========================
        // LOCATION
        // =========================

        Location location =
                target.getLocation()
                        .clone()
                        .add(
                                0,
                                target.getHeight() + 0.5,
                                0
                        );

        // =========================
        // CREATE ARMOR STAND
        // =========================

        ArmorStand armorStand =
                target.getWorld()
                        .spawn(
                                location,
                                ArmorStand.class
                        );

        // =========================
        // ARMOR STAND SETTINGS
        // =========================

        armorStand.setInvisible(true);
        armorStand.setMarker(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setSilent(true);
        armorStand.setCollidable(false);

        // =========================
        // DAMAGE TEXT
        // =========================

        String damageText;

        if (critical) {

            damageText =
                    "§6§l✦ "
                            + String.format(
                            "%.1f",
                            damage
                    )
                            + " ✦";

        } else if (magic) {

            damageText =
                    "§d"
                            + String.format(
                            "%.1f",
                            damage
                    );

        } else {

            damageText =
                    "§c"
                            + String.format(
                            "%.1f",
                            damage
                    );
        }

        armorStand.setCustomName(damageText);
        armorStand.setCustomNameVisible(true);

        // =========================
        // FLOAT ANIMATION
        // =========================

        new BukkitRunnable() {

            private int ticks = 0;

            @Override
            public void run() {

                // =========================
                // ENTITY INVALID
                // =========================

                if (!armorStand.isValid()) {

                    cancel();

                    return;
                }

                // =========================
                // COUNT TICKS
                // =========================

                ticks++;

                // =========================
                // MOVE UP
                // =========================

                Location current =
                        armorStand.getLocation();

                current.add(
                        0,
                        0.025,
                        0
                );

                armorStand.teleport(current);

                // =========================
                // REMOVE AFTER 30 TICKS
                // =========================

                if (ticks >= 30) {

                    armorStand.remove();

                    cancel();
                }
            }

        }.runTaskTimer(
                plugin,
                1L,
                1L
        );
    }
}