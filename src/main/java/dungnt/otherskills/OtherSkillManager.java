package dungnt.otherskills;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.ModifierSource;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.block.data.Ageable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Independent secondary-skill system: Movement, Mining, Woodcutting and Farming. */
public final class OtherSkillManager implements Listener {
    private final MyRPG plugin;
    private final File folder;
    private final Map<UUID, OtherSkillData> data = new HashMap<>();
    private final Map<OtherSkillType, YamlConfiguration> configs = new EnumMap<>(OtherSkillType.class);
    private final Set<String> playerPlacedBlocks = new HashSet<>();
    private final File placedFile;
    private final Map<UUID, org.bukkit.Location> lastLocations = new HashMap<>();
    private final Map<UUID, Double> movementRemainder = new HashMap<>();
    private org.bukkit.scheduler.BukkitTask saveTask;

    public OtherSkillManager(MyRPG plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "otherskills");
        if (!folder.exists()) folder.mkdirs();
        this.placedFile = new File(folder, "placed-blocks.yml");
        createDefaultConfigs();
        loadConfigs();
        loadPlacedBlocks();
        startSaveTask();
    }

    public void reload() {
        createDefaultConfigs();
        loadConfigs();
        for (Player player : Bukkit.getOnlinePlayers()) applyStatBonuses(player);
    }

    private void createDefaultConfigs() {
        createConfig(OtherSkillType.MOVEMENT,
                "# Movement EXP is gained per block moved. Falling damage gives damage x 2 EXP.\n" +
                "exp-per-block: 0.1\n" +
                "exp-per-swimming-block: 0.2\n" +
                "fall-damage-multiplier: 2.0\n" +
                "level:\n  base-required-exp: 200.0\n  growth: 1.0\n" + statsYaml());

        createConfig(OtherSkillType.MINING,
                "# Mining EXP. Only natural/unplaced ore and STONE are eligible.\n" +
                "blocks:\n" +
                "  STONE: 1.0\n" +
                "  COAL_ORE: 1.0\n" +
                "  DEEPSLATE_COAL_ORE: 1.0\n" +
                "  IRON_ORE: 1.0\n" +
                "  DEEPSLATE_IRON_ORE: 1.0\n" +
                "  COPPER_ORE: 1.0\n" +
                "  DEEPSLATE_COPPER_ORE: 1.0\n" +
                "  GOLD_ORE: 1.0\n" +
                "  DEEPSLATE_GOLD_ORE: 1.0\n" +
                "  REDSTONE_ORE: 1.0\n" +
                "  DEEPSLATE_REDSTONE_ORE: 1.0\n" +
                "  LAPIS_ORE: 1.0\n" +
                "  DEEPSLATE_LAPIS_ORE: 1.0\n" +
                "  DIAMOND_ORE: 1.0\n" +
                "  DEEPSLATE_DIAMOND_ORE: 1.0\n" +
                "  EMERALD_ORE: 1.0\n" +
                "  DEEPSLATE_EMERALD_ORE: 1.0\n" +
                "  NETHER_GOLD_ORE: 1.0\n" +
                "  NETHER_QUARTZ_ORE: 1.0\n" +
                "  ANCIENT_DEBRIS: 1.0\n" +
                "level:\n  base-required-exp: 200.0\n  growth: 1.0\n" + statsYaml());

        createConfig(OtherSkillType.WOODCUTTING,
                "# Every supported wood/log type starts at 1 EXP.\n" +
                "exp-per-log: 1.0\n" +
                "level:\n  base-required-exp: 200.0\n  growth: 1.0\n" + statsYaml());

        createConfig(OtherSkillType.FARMING,
                "# Crop EXP. Pumpkin and melon only give EXP if the block was not player/admin placed.\n" +
                "blocks:\n" +
                "  CARROTS: 1.0\n" +
                "  WHEAT: 1.0\n" +
                "  POTATOES: 1.0\n" +
                "  BEETROOTS: 1.0\n" +
                "  NETHER_WART: 1.0\n" +
                "  PUMPKIN: 1.0\n" +
                "  MELON: 1.0\n" +
                "level:\n  base-required-exp: 200.0\n  growth: 1.0\n" + statsYaml());
    }

    private String statsYaml() {
        StringBuilder b = new StringBuilder("stats:\n");
        for (StatType stat : StatType.values()) {
            b.append("  ").append(stat.name()).append(": 0.0\n");
        }
        return b.toString();
    }

    private void createConfig(OtherSkillType type, String body) {
        File file = new File(folder, type.getId() + ".yml");
        if (file.exists()) return;
        String content = body;
        if (!content.contains("stats:")) content += statsYaml();
        try {
            java.nio.file.Files.writeString(file.toPath(), content);
        } catch (IOException e) {
            plugin.getLogger().warning("Cannot create " + file.getName() + ": " + e.getMessage());
        }
    }

    private void loadConfigs() {
        configs.clear();
        for (OtherSkillType type : OtherSkillType.values()) {
            configs.put(type, YamlConfiguration.loadConfiguration(new File(folder, type.getId() + ".yml")));
        }
    }

    public OtherSkillData getData(Player player) {
        if (player == null) return null;
        UUID uuid = player.getUniqueId();
        return data.computeIfAbsent(uuid, this::loadData);
    }

    private OtherSkillData loadData(UUID uuid) {
        OtherSkillData d = new OtherSkillData();
        File file = new File(folder, uuid + ".yml");
        if (!file.exists()) return d;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (OtherSkillType type : OtherSkillType.values()) {
            String path = type.getId();
            d.setLevel(type, cfg.getInt(path + ".level", 1));
            d.setExperience(type, cfg.getDouble(path + ".experience", 0.0));
        }
        return d;
    }

    public void saveData(Player player) { if (player != null) saveData(player.getUniqueId()); }

    public void saveData(UUID uuid) {
        OtherSkillData d = data.get(uuid);
        if (d == null) return;
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("uuid", uuid.toString());
        for (OtherSkillType type : OtherSkillType.values()) {
            cfg.set(type.getId() + ".level", d.getLevel(type));
            cfg.set(type.getId() + ".experience", d.getExperience(type));
        }
        try { cfg.save(new File(folder, uuid + ".yml")); }
        catch (IOException e) { plugin.getLogger().warning("Cannot save other skill data: " + e.getMessage()); }
    }

    public void saveAll() { for (UUID uuid : new ArrayList<>(data.keySet())) saveData(uuid); savePlacedBlocks(); }

    public void unload(Player player) {
        if (player == null) return;
        saveData(player);
        data.remove(player.getUniqueId());
        lastLocations.remove(player.getUniqueId());
        movementRemainder.remove(player.getUniqueId());
    }

    public double getRequiredExperience(OtherSkillType type, int level) {
        YamlConfiguration cfg = configs.get(type);
        double base = cfg == null ? 200.0 : cfg.getDouble("level.base-required-exp", 200.0);
        double growth = cfg == null ? 1.0 : cfg.getDouble("level.growth", 1.0);
        if (level <= 1) return base;
        return base * ((level * level + level) / 2.0) * Math.max(0.01, growth);
    }

    public double getPercentage(OtherSkillType type, Player player) {
        OtherSkillData d = getData(player);
        double req = getRequiredExperience(type, d.getLevel(type));
        return req <= 0 ? 100 : Math.max(0, Math.min(100, d.getExperience(type) * 100.0 / req));
    }

    public void addExperience(Player player, OtherSkillType type, double amount) {
        if (player == null || type == null || amount <= 0) return;
        OtherSkillData d = getData(player);
        d.setExperience(type, d.getExperience(type) + amount * plugin.getLevelManager().getExpMultiplier(player));
        while (d.getExperience(type) >= getRequiredExperience(type, d.getLevel(type))) {
            d.setExperience(type, d.getExperience(type) - getRequiredExperience(type, d.getLevel(type)));
            d.setLevel(type, d.getLevel(type) + 1);
            applyStatBonuses(player, type);
            player.sendMessage("§6§l✦ " + pretty(type) + " LEVEL UP!");
            player.sendMessage("§7Bạn đã đạt §eLevel " + d.getLevel(type) + "§7.");
        }
        // Data is persisted on quit and by the periodic save task.
    }

    private String pretty(OtherSkillType type) {
        return switch (type) {
            case MOVEMENT -> "Movement";
            case MINING -> "Mining";
            case WOODCUTTING -> "Woodcutting";
            case FARMING -> "Farming";
        };
    }

    public void applyStatBonuses(Player player) {
        if (player == null) return;
        for (OtherSkillType type : OtherSkillType.values()) applyStatBonuses(player, type);
    }

    private void applyStatBonuses(Player player, OtherSkillType type) {
        UUID uuid = player.getUniqueId();
        YamlConfiguration cfg = configs.get(type);
        if (cfg == null) return;
        OtherSkillData d = getData(player);
        int levels = Math.max(0, d.getLevel(type) - 1);
        ConfigurationSection stats = cfg.getConfigurationSection("stats");
        if (stats == null) return;
        for (StatType stat : StatType.values()) {
            String id = "otherskill_" + type.getId() + "_" + stat.name().toLowerCase(Locale.ROOT);
            plugin.getStatManager().removeModifier(uuid, id);
            double perLevel = stats.getDouble(stat.name(), 0.0);
            double amount = perLevel * levels;
            if (Math.abs(amount) > 0.0000001) {
                plugin.getStatManager().addModifier(uuid, new StatModifier(id, stat, ModifierType.FLAT, amount, ModifierSource.BUFF));
            }
        }
        plugin.getPlayerManager().refreshResources(player);
    }

    // --------------------------------------------------
    // Movement
    // --------------------------------------------------
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        Player p = event.getPlayer();
        if (p.isDead() || p.isInsideVehicle() || p.getWorld() != event.getTo().getWorld()) {
            lastLocations.put(p.getUniqueId(), event.getTo().clone());
            return;
        }
        org.bukkit.Location from = lastLocations.put(p.getUniqueId(), event.getTo().clone());
        if (from == null) from = event.getFrom();
        double distance = from.distance(event.getTo());
        if (distance <= 0 || distance > 8.0) return; // prevents teleports from generating EXP
        OtherSkillType type = OtherSkillType.MOVEMENT;
        double expPerBlock = p.isSwimming() ? configs.get(type).getDouble("exp-per-swimming-block", 0.2) : configs.get(type).getDouble("exp-per-block", 0.1);
        double total = movementRemainder.getOrDefault(p.getUniqueId(), 0.0) + distance * expPerBlock;
        double whole = Math.floor(total * 1000.0) / 1000.0;
        movementRemainder.put(p.getUniqueId(), total - whole);
        if (whole > 0) addExperience(p, type, whole);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p) || event.getCause() != EntityDamageEvent.DamageCause.FALL || event.getFinalDamage() <= 0) return;
        double multiplier = configs.get(OtherSkillType.MOVEMENT).getDouble("fall-damage-multiplier", 2.0);
        addExperience(p, OtherSkillType.MOVEMENT, event.getFinalDamage() * multiplier);
    }

    // --------------------------------------------------
    // Natural/player-placed block tracking
    // --------------------------------------------------
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Material type = event.getBlockPlaced().getType();
        if (isTrackedMaterial(type)) {
            playerPlacedBlocks.add(key(event.getBlockPlaced().getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        var block = event.getBlock();
        Material material = block.getType();
        String key = key(block.getLocation());
        boolean placed = playerPlacedBlocks.remove(key);
        if (placed && (isWood(material) || isMiningMaterial(material) || material == Material.PUMPKIN || material == Material.MELON)) {
            return;
        }

        Player p = event.getPlayer();
        YamlConfiguration mining = configs.get(OtherSkillType.MINING);
        if (mining.getConfigurationSection("blocks") != null && mining.getConfigurationSection("blocks").contains(material.name())) {
            double exp = mining.getDouble("blocks." + material.name(), 0.0);
            if (exp > 0) addExperience(p, OtherSkillType.MINING, exp);
            return;
        }

        if (isWood(material)) {
            double exp = configs.get(OtherSkillType.WOODCUTTING).getDouble("exp-per-log", 1.0);
            if (exp > 0) addExperience(p, OtherSkillType.WOODCUTTING, exp);
            return;
        }

        OtherSkillType farm = OtherSkillType.FARMING;
        if (isFarmMaterial(material)) {
            if (isCrop(material) && block.getBlockData() instanceof Ageable age && age.getAge() < age.getMaximumAge()) return;
            if ((material == Material.PUMPKIN || material == Material.MELON) && playerPlacedBlocks.contains(key)) return;
            double exp = configs.get(farm).getDouble("blocks." + material.name(), 0.0);
            if (exp > 0) addExperience(p, farm, exp);
        }
    }

    private boolean isTrackedMaterial(Material m) { return m != null && (isWood(m) || isMiningMaterial(m) || isFarmMaterial(m)); }

    private boolean isMiningMaterial(Material m) {
        YamlConfiguration cfg = configs.get(OtherSkillType.MINING);
        return cfg != null && cfg.getConfigurationSection("blocks") != null && cfg.getConfigurationSection("blocks").contains(m.name());
    }

    private boolean isWood(Material m) {
        String n = m.name();
        return n.endsWith("_LOG") || n.endsWith("_STEM") || n.startsWith("STRIPPED_") && (n.endsWith("_LOG") || n.endsWith("_STEM")) || n.equals("BAMBOO_BLOCK") || n.equals("STRIPPED_BAMBOO_BLOCK");
    }

    private boolean isCrop(Material m) {
        return m == Material.CARROTS || m == Material.WHEAT || m == Material.POTATOES || m == Material.BEETROOTS || m == Material.NETHER_WART;
    }

    private boolean isFarmMaterial(Material m) { return isCrop(m) || m == Material.PUMPKIN || m == Material.MELON; }

    private String key(org.bukkit.Location loc) {
        return loc.getWorld().getUID() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private void loadPlacedBlocks() {
        if (!placedFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(placedFile);
        playerPlacedBlocks.addAll(cfg.getStringList("blocks"));
    }

    private void savePlacedBlocks() {
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("blocks", new ArrayList<>(playerPlacedBlocks));
        try { cfg.save(placedFile); } catch (IOException e) { plugin.getLogger().warning("Cannot save otherskills placed blocks: " + e.getMessage()); }
    }

    private void startSaveTask() {
        saveTask = Bukkit.getScheduler().runTaskTimer(plugin, this::saveAll, 600L, 600L);
    }

    public void shutdown() {
        if (saveTask != null) {
            saveTask.cancel();
            saveTask = null;
        }
        saveAll();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        getData(event.getPlayer());
        applyStatBonuses(event.getPlayer());
        lastLocations.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation().clone());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { unload(event.getPlayer()); }
}
