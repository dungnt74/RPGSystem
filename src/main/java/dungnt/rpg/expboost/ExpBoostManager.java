package dungnt.rpg.expboost;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.ModifierSource;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ExpBoostManager implements Listener {

    public static final int[] BONUSES = {50, 100, 200, 300};
    public static final int[] HOURS = {1, 72, 168, 672};

    private final MyRPG plugin;
    private final File file;
    private final Map<UUID, Map<Integer, Long>> active = new HashMap<>();

    private PlayerPointsAPI pointsAPI;

    public ExpBoostManager(MyRPG plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "expboost.yml");
        hookPlayerPoints();
        load();
        startExpiryTask();
    }

    private void hookPlayerPoints() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") instanceof PlayerPoints pp) {
            pointsAPI = pp.getAPI();
        }
    }

    public boolean isAvailable() {
        return pointsAPI != null;
    }

    public int getPoints(Player player) {
        if (player == null || pointsAPI == null) return 0;
        return pointsAPI.look(player.getUniqueId());
    }

    public int getCost(int bonusPercent, int durationIndex) {
        String key = "expboost.prices." + bonusPercent + "." + durationIndex;
        int defaults;
        switch (bonusPercent) {
            case 50 -> defaults = switch (durationIndex) {
                case 0 -> 100;
                case 1 -> 500;
                case 2 -> 1000;
                default -> 3000;
            };
            case 100 -> defaults = switch (durationIndex) {
                case 0 -> 200;
                case 1 -> 1000;
                case 2 -> 2000;
                default -> 6000;
            };
            case 200 -> defaults = switch (durationIndex) {
                case 0 -> 400;
                case 1 -> 2000;
                case 2 -> 4000;
                default -> 12000;
            };
            default -> defaults = switch (durationIndex) {
                case 0 -> 600;
                case 1 -> 3000;
                case 2 -> 6000;
                default -> 18000;
            };
        }
        return plugin.getConfig().getInt(key, defaults);
    }

    public long getDurationMillis(int durationIndex) {
        long hours = switch (durationIndex) {
            case 0 -> 1L;
            case 1 -> 72L;
            case 2 -> 168L;
            default -> 672L;
        };
        return hours * 60L * 60L * 1000L;
    }

    public long getExpiry(Player player, int bonusPercent) {
        purgeExpired(player);
        return active
                .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                .getOrDefault(bonusPercent, 0L);
    }

    public boolean purchase(Player player, int bonusPercent, int durationIndex) {
        if (player == null || !isAvailable()) return false;
        if (!containsBonus(bonusPercent) || durationIndex < 0 || durationIndex > 3) return false;

        int cost = getCost(bonusPercent, durationIndex);
        if (getPoints(player) < cost) return false;

        if (!pointsAPI.take(player.getUniqueId(), cost)) {
            return false;
        }

        purgeExpired(player);

        UUID uuid = player.getUniqueId();
        Map<Integer, Long> buffs =
                active.computeIfAbsent(uuid, k -> new HashMap<>());

        long now = System.currentTimeMillis();
        long oldExpiry = buffs.getOrDefault(bonusPercent, 0L);
        long base = Math.max(now, oldExpiry);
        long expiry = base + getDurationMillis(durationIndex);

        buffs.put(bonusPercent, expiry);
        applyModifier(player, bonusPercent);
        save();

        return true;
    }

    public void applyAll(Player player) {
        if (player == null) return;
        purgeExpired(player);

        Map<Integer, Long> buffs = active.get(player.getUniqueId());
        if (buffs == null) return;

        for (Integer bonus : buffs.keySet()) {
            applyModifier(player, bonus);
        }
    }

    private void applyModifier(Player player, int bonusPercent) {
        if (player == null) return;

        String id = modifierId(bonusPercent);
        plugin.getStatManager().removeModifier(
                player.getUniqueId(),
                id
        );

        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new StatModifier(
                        id,
                        StatType.EXP_BONUS,
                        ModifierType.FLAT,
                        bonusPercent / 100.0,
                        ModifierSource.BUFF
                )
        );
    }

    private void removeModifier(Player player, int bonusPercent) {
        if (player == null) return;
        plugin.getStatManager().removeModifier(
                player.getUniqueId(),
                modifierId(bonusPercent)
        );
    }

    private String modifierId(int bonusPercent) {
        return "expboost_" + bonusPercent;
    }

    private boolean containsBonus(int bonus) {
        for (int value : BONUSES) {
            if (value == bonus) return true;
        }
        return false;
    }

    public void purgeExpired(Player player) {
        if (player == null) return;

        UUID uuid = player.getUniqueId();
        Map<Integer, Long> buffs = active.get(uuid);
        if (buffs == null) return;

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, Long>> iterator = buffs.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Long> entry = iterator.next();
            if (entry.getValue() <= now) {
                removeModifier(player, entry.getKey());
                iterator.remove();
            }
        }

        if (buffs.isEmpty()) {
            active.remove(uuid);
        }
    }

    private void startExpiryTask() {
        Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    boolean changed = false;

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        int before = active.getOrDefault(
                                player.getUniqueId(),
                                Collections.emptyMap()
                        ).size();

                        purgeExpired(player);

                        int after = active.getOrDefault(
                                player.getUniqueId(),
                                Collections.emptyMap()
                        ).size();

                        if (before != after) changed = true;
                    }

                    if (changed) save();
                },
                20L,
                20L
        );
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> applyAll(event.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        save();
        Player player = event.getPlayer();

        Map<Integer, Long> buffs = active.get(player.getUniqueId());
        if (buffs != null) {
            for (Integer bonus : buffs.keySet()) {
                removeModifier(player, bonus);
            }
        }
    }

    public void save() {
        YamlConfiguration cfg = new YamlConfiguration();

        for (Map.Entry<UUID, Map<Integer, Long>> playerEntry : active.entrySet()) {
            String base = "players." + playerEntry.getKey();

            for (Map.Entry<Integer, Long> buff : playerEntry.getValue().entrySet()) {
                if (buff.getValue() > System.currentTimeMillis()) {
                    cfg.set(base + "." + buff.getKey(), buff.getValue());
                }
            }
        }

        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning(
                    "Không thể lưu expboost.yml: " + e.getMessage()
            );
        }
    }

    private void load() {
        if (!file.exists()) return;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection players = cfg.getConfigurationSection("players");

        if (players == null) return;

        long now = System.currentTimeMillis();

        for (String uuidText : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidText);
                ConfigurationSection buffs =
                        players.getConfigurationSection(uuidText);

                if (buffs == null) continue;

                Map<Integer, Long> map = new HashMap<>();

                for (String bonusText : buffs.getKeys(false)) {
                    int bonus = Integer.parseInt(bonusText);
                    long expiry = buffs.getLong(bonusText);

                    if (containsBonus(bonus) && expiry > now) {
                        map.put(bonus, expiry);
                    }
                }

                if (!map.isEmpty()) {
                    active.put(uuid, map);
                }
            } catch (Exception ignored) {
                // Ignore malformed entries.
            }
        }
    }

    public void shutdown() {
        save();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Map<Integer, Long> buffs = active.get(player.getUniqueId());
            if (buffs != null) {
                for (Integer bonus : buffs.keySet()) {
                    removeModifier(player, bonus);
                }
            }
        }
    }
}
