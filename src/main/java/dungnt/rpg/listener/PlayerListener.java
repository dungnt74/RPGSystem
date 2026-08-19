package dungnt.rpg.listener;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import dungnt.rpg.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;

import java.util.Locale;

public class PlayerListener implements Listener {
    private final MyRPG plugin;
    private BukkitTask actionbarTask;
    private BukkitTask regenerationTask;

    public PlayerListener(MyRPG plugin) {
        this.plugin = plugin;
        startActionbar();
        startRegeneration();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerManager().getData(player);
        plugin.getPlayerManager().refreshStats(player);
        plugin.getPlayerManager().refreshResources(player, true);
        plugin.getEquipmentListener().scheduleRefreshEquipment(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = event.getPlayer();
            PlayerData data = plugin.getPlayerManager().getData(player);
            // Respawn is intentionally a full heal.
            plugin.getPlayerManager().refreshResources(player);
            data.setHealth(data.getMaxHealth());
            player.setHealth(data.getMaxHealth());
            plugin.getPlayerManager().saveData(player);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().unload(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                PlayerData data = plugin.getPlayerManager().getData(player);
                data.setHealth(player.getHealth());
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                PlayerData data = plugin.getPlayerManager().getData(player);
                data.setHealth(player.getHealth());
            });
        }
    }

    public void stop() {
        if (actionbarTask != null) {
            actionbarTask.cancel();
            actionbarTask = null;
        }

        if (regenerationTask != null) {
            regenerationTask.cancel();
            regenerationTask = null;
        }
    }

    /**
     * Every 3 seconds:
     *   1 HEALTH_REGEN = +0.1 HP
     *   1 MANA_REGEN   = +0.1 Mana
     *
     * Values are clamped to the current max HP / max Mana.
     */
    private void startRegeneration() {
        regenerationTask =
                Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        () -> {
                            for (Player player :
                                    Bukkit.getOnlinePlayers()) {

                                if (!player.isOnline()
                                        || player.isDead()) {
                                    continue;
                                }

                                PlayerData data =
                                        plugin.getPlayerManager()
                                                .getData(player);

                                if (data == null) {
                                    continue;
                                }

                                double healthRegen =
                                        plugin.getStatManager()
                                                .getStat(
                                                        player.getUniqueId(),
                                                        StatType.HEALTH_REGEN
                                                );

                                double manaRegen =
                                        plugin.getStatManager()
                                                .getStat(
                                                        player.getUniqueId(),
                                                        StatType.MANA_REGEN
                                                );

                                // 1 point = 0.1 HP every 3 seconds.
                                if (healthRegen > 0.0) {
                                    double maxHealth =
                                            plugin.getPlayerManager()
                                                    .getEffectiveMaxHealth(
                                                            player.getUniqueId()
                                                    );

                                    double healed =
                                            healthRegen * 0.1;

                                    double newHealth =
                                            Math.min(
                                                    maxHealth,
                                                    player.getHealth() + healed
                                            );

                                    if (newHealth > player.getHealth()) {
                                        player.setHealth(newHealth);
                                    }

                                    data.setMaxHealth(maxHealth);
                                    data.setHealth(newHealth);
                                } else {
                                    data.setHealth(player.getHealth());
                                }

                                // 1 point = 0.1 Mana every 3 seconds.
                                if (manaRegen > 0.0) {
                                    double maxMana =
                                            plugin.getPlayerManager()
                                                    .getEffectiveMaxMana(
                                                            player.getUniqueId()
                                                    );

                                    double newMana =
                                            Math.min(
                                                    maxMana,
                                                    data.getMana()
                                                            + manaRegen * 0.1
                                            );

                                    data.setMaxMana(maxMana);
                                    data.setMana(newMana);
                                }
                            }
                        },
                        60L,
                        60L
                );
    }

    private void startActionbar() {
        actionbarTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerData data = plugin.getPlayerManager().getData(player);
                double maxHealth = data.getMaxHealth();
                double health = Math.min(player.getHealth(), maxHealth);
                player.sendActionBar(Component.text(
                        "§c❤ " + format(health) + "§7/§c" + format(maxHealth)
                                + "   §b✦ " + format(data.getMana()) + "§7/§b" + format(data.getMaxMana())
                ));
            }
        }, 1L, 2L);
    }

    private String format(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001) return String.valueOf((long) Math.rint(value));
        return String.format(Locale.US, "%.1f", value);
    }
}
