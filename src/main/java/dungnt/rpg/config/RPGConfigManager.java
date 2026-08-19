package dungnt.rpg.config;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.item.RPGItem;
import dungnt.rpg.item.RPGItemManager;
import dungnt.rpg.item.Rarity;
import dungnt.rpg.mob.MobDefinition;
import dungnt.rpg.mob.MobDefinitionManager;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Quản lý toàn bộ dữ liệu ngoài của DungNTRPG.
 *
 * Kể từ khi plugin đổi tên thành "DungNTRPG" (xem plugin.yml),
 * {@code plugin.getDataFolder()} đã trỏ thẳng tới
 * {@code plugins/DungNTRPG/}, nên đây cũng là thư mục gốc duy nhất
 * cho toàn bộ dữ liệu của plugin:
 *
 * <pre>
 * plugins/DungNTRPG/
 *   config.yml
 *   playerData/    -> UUID.yml của từng người chơi (PlayerManager)
 *   Mobs/          -> định nghĩa quái (nhiều file .yml, mỗi file 1 section "mobs")
 *   Items/         -> định nghĩa item (nhiều file .yml, mỗi file 1 section "items")
 *   Gems/          -> định nghĩa gem (dự trữ cho phase kế tiếp)
 *   modelItems/    -> model resource-pack gắn cho item
 *   modelMobs/     -> model resource-pack gắn cho mob
 * </pre>
 */
public final class RPGConfigManager {

    private final MyRPG plugin;

    private File folder;
    private File configFile;
    private File playerDataFolder;
    private File itemsFolder;
    private File mobsFolder;
    private File gemsFolder;
    private File modelItemsFolder;
    private File modelMobsFolder;

    public RPGConfigManager(MyRPG plugin) {
        this.plugin = plugin;
    }

    // ==================================================
    // INITIALIZE
    // ==================================================

    public void initialize() {

        folder = plugin.getDataFolder();

        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().warning("Không thể tạo thư mục " + folder.getAbsolutePath());
        }

        playerDataFolder = ensureFolder("playerData");
        itemsFolder = ensureFolder("Items");
        mobsFolder = ensureFolder("Mobs");
        gemsFolder = ensureFolder("Gems");
        modelItemsFolder = ensureFolder("modelItems");
        modelMobsFolder = ensureFolder("modelMobs");

        ensureConfigFile();
        ensureReadme(modelItemsFolder, "modelItems", "model: dragon_slayer");
        ensureReadme(modelMobsFolder, "modelMobs", "model: ancient_golem");

        // Seed each data folder with its bundled sample file the first time
        // (only if the folder is still empty, so existing servers keep
        // whatever they already put in there).
        seedFolderIfEmpty(itemsFolder, "items.yml");
        seedFolderIfEmpty(mobsFolder, "mobs.yml");
        seedFolderIfEmpty(gemsFolder, "gems.yml");
    }

    // ==================================================
    // ITEMS
    // ==================================================

    /**
     * Đọc TẤT CẢ file .yml trong Items/, mỗi file có 1 section
     * gốc "items". Cho phép chia item ra nhiều file tùy ý
     * (vd: Items/weapons.yml, Items/armor.yml, ...).
     */
    public int reloadItems() {

        if (itemsFolder == null) {
            initialize();
        }

        RPGItemManager manager = plugin.getRPGItemManager();
        manager.clear();

        int loaded = 0;

        for (File file : listYmlFiles(itemsFolder)) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection root = config.getConfigurationSection("items");

            if (root == null) {
                continue;
            }

            for (String id : root.getKeys(false)) {

                ConfigurationSection section = root.getConfigurationSection(id);

                if (section == null) {
                    continue;
                }

                try {
                    manager.register(parseItem(id, section));
                    loaded++;
                } catch (Exception ex) {
                    plugin.getLogger().warning(
                            "Không thể load item '" + id + "' (" + file.getName() + "): " + ex.getMessage()
                    );
                }
            }
        }

        return loaded;
    }

    /** Lưu (hoặc cập nhật) 1 item vào Items/generated.yml — dùng cho /rpg item create. */
    public void saveGeneratedItem(RPGItem item) {

        if (itemsFolder == null) {
            initialize();
        }

        File file = new File(itemsFolder, "generated.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "items." + item.getId().toLowerCase(Locale.ROOT);

        config.set(path + ".name", item.getName());
        config.set(path + ".material", item.getMaterial().name());
        config.set(path + ".slot", item.getSlot().name());
        config.set(path + ".rarity", item.getRarity().name());
        config.set(path + ".level", item.getItemLevel());
        config.set(path + ".sockets", item.getSocketCount());

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể lưu item '" + item.getId() + "': " + ex.getMessage());
        }
    }

    /** Xoá 1 item khỏi Items/generated.yml (nếu có) — dùng cho /rpg item delete. */
    public void deleteGeneratedItem(String id) {

        if (itemsFolder == null) {
            return;
        }

        File file = new File(itemsFolder, "generated.yml");

        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("items." + id.toLowerCase(Locale.ROOT), null);

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể xoá item '" + id + "': " + ex.getMessage());
        }
    }

    private RPGItem parseItem(String id, ConfigurationSection section) {
        String name = color(section.getString("name", id));
        Material material = Material.matchMaterial(section.getString("material", "STONE"));
        if (material == null) throw new IllegalArgumentException("Material không hợp lệ: " + section.getString("material"));
        EquipmentSlot slot = parseEnum(EquipmentSlot.class, section.getString("slot", "MAIN_HAND"));
        RPGItem item = new RPGItem(id.toLowerCase(Locale.ROOT), name, material, slot);
        item.setRarity(parseEnum(Rarity.class, section.getString("rarity", "COMMON")));
        item.setItemLevel(Math.max(1, section.getInt("level", 1)));
        ConfigurationSection upgrade = section.getConfigurationSection("upgrade");
        if (upgrade != null) {
            item.setMaxUpgradeLevel(Math.max(0, upgrade.getInt("max", 0)));
            item.setUpgradeLevel(Math.max(0, upgrade.getInt("start", 0)));
        }
        item.setModel(section.getString("model"));
        List<String> lore = new ArrayList<>();
        for (String line : section.getStringList("lore")) lore.add(color(line));
        item.setBaseLore(lore);
        item.setSocketCount(Math.max(0, Math.min(4, section.getInt("sockets", 0))));

        ConfigurationSection stats = section.getConfigurationSection("stats");
        if (stats != null) {
            // Backward-compatible map format:
            // ATTACK: { type: FLAT, amount: 25 }
            for (String statName : stats.getKeys(false)) {
                Object raw = stats.get(statName);
                if (raw instanceof ConfigurationSection statSection) {
                    addModifier(item, statName, statSection.getString("type", "FLAT"), statSection.getDouble("amount", 0.0D));
                }
            }
        }

        // New list format for future random/stat expansion:
        // - type: ATTACK
        //   modifier: FLAT
        //   amount: 25
        List<?> statList = section.getList("stats");
        if (statList != null) {
            for (Object value : statList) {
                if (!(value instanceof java.util.Map<?, ?> map)) continue;
                String statName = String.valueOf(map.get("type"));
                String modifier = String.valueOf(map.containsKey("modifier") ? map.get("modifier") : "FLAT");
                double amount = toDouble(map.get("amount"));
                addModifier(item, statName, modifier, amount);
            }
        }
        return item;
    }

    private void addModifier(RPGItem item, String statName, String modifierName, double amount) {
        StatType statType = parseEnum(StatType.class, statName);
        ModifierType modifierType = parseEnum(ModifierType.class, modifierName);
        item.addStatModifier(new StatModifier(
                "item_" + item.getId() + "_" + statType.name().toLowerCase(Locale.ROOT) + "_" + modifierType.name().toLowerCase(Locale.ROOT),
                statType, modifierType, amount));
    }

    // ==================================================
    // MOBS
    // ==================================================

    /**
     * Đọc TẤT CẢ file .yml trong Mobs/, mỗi file có 1 section
     * gốc "mobs".
     */
    public int reloadMobs() {

        if (mobsFolder == null) {
            initialize();
        }

        MobDefinitionManager manager = plugin.getMobDefinitionManager();
        manager.clear();

        int loaded = 0;

        for (File file : listYmlFiles(mobsFolder)) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection root = config.getConfigurationSection("mobs");

            if (root == null) {
                continue;
            }

            for (String id : root.getKeys(false)) {

                ConfigurationSection section = root.getConfigurationSection(id);

                if (section == null) {
                    continue;
                }

                try {
                    manager.register(parseMob(id, section));
                    loaded++;
                } catch (Exception ex) {
                    plugin.getLogger().warning(
                            "Không thể load mob '" + id + "' (" + file.getName() + "): " + ex.getMessage()
                    );
                }
            }
        }

        return loaded;
    }

    private MobDefinition parseMob(String id, ConfigurationSection section) {

        EntityType type = parseEnum(EntityType.class, section.getString("type", "ZOMBIE"));

        MobDefinition def = new MobDefinition(id.toLowerCase(Locale.ROOT), type);
        def.setDisplayName(color(section.getString("name", id)));
        def.setMaxHealth(Math.max(1.0, section.getDouble("health", 20.0)));
        def.setAttack(Math.max(0.0, section.getDouble("attack", 5.0)));
        def.setDefense(Math.max(0.0, section.getDouble("defense", 0.0)));
        def.setMagicDefense(Math.max(0.0, section.getDouble("magic-defense", 0.0)));
        def.setModel(section.getString("model"));

        return def;
    }

    /** Lưu (hoặc cập nhật) 1 mob definition vào Mobs/generated.yml — dùng cho /rpg mob create. */
    public void saveGeneratedMob(MobDefinition def) {

        if (mobsFolder == null) {
            initialize();
        }

        File file = new File(mobsFolder, "generated.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        String path = "mobs." + def.getId();

        config.set(path + ".name", def.getDisplayName());
        config.set(path + ".type", def.getEntityType().name());
        config.set(path + ".health", def.getMaxHealth());
        config.set(path + ".attack", def.getAttack());
        config.set(path + ".defense", def.getDefense());
        config.set(path + ".magic-defense", def.getMagicDefense());

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể lưu mob '" + def.getId() + "': " + ex.getMessage());
        }
    }

    /** Xoá 1 mob definition khỏi Mobs/generated.yml (nếu có) — dùng cho /rpg mob delete. */
    public void deleteGeneratedMob(String id) {

        if (mobsFolder == null) {
            return;
        }

        File file = new File(mobsFolder, "generated.yml");

        if (!file.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("mobs." + id.toLowerCase(Locale.ROOT), null);

        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể xoá mob '" + id + "': " + ex.getMessage());
        }
    }

    // ==================================================
    // HELPERS
    // ==================================================

    private double toDouble(Object value) {
        if (value instanceof Number number) return number.doubleValue();
        try { return Double.parseDouble(String.valueOf(value)); } catch (Exception ignored) { return 0.0D; }
    }

    private <E extends Enum<E>> E parseEnum(Class<E> type, String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("Giá trị enum trống cho " + type.getSimpleName());
        try { return Enum.valueOf(type, raw.trim().toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Giá trị '" + raw + "' không hợp lệ cho " + type.getSimpleName()); }
    }

    private String color(String text) { return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text); }

    private File ensureFolder(String name) {
        File dir = new File(folder, name);
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("Không thể tạo thư mục " + dir.getAbsolutePath());
        }
        return dir;
    }

    private void ensureConfigFile() {
        configFile = new File(folder, "config.yml");
        if (configFile.exists()) {
            return;
        }
        try (InputStream input = plugin.getResource("config.yml")) {
            if (input != null) {
                try (FileOutputStream output = new FileOutputStream(configFile)) {
                    input.transferTo(output);
                }
                return;
            }
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể tạo config.yml: " + ex.getMessage());
        }
        try {
            java.nio.file.Files.writeString(
                    configFile.toPath(),
                    "# DungNTRPG config.yml\n"
            );
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể tạo config.yml: " + ex.getMessage());
        }
    }

    /** Copy resource "<resourceName>" vào folder/<resourceName> chỉ khi folder đó đang trống. */
    private void seedFolderIfEmpty(File targetFolder, String resourceName) {

        File[] existing = targetFolder.listFiles();

        if (existing != null && existing.length > 0) {
            return;
        }

        File target = new File(targetFolder, resourceName);

        try (InputStream input = plugin.getResource(resourceName)) {
            if (input == null) {
                return;
            }
            try (FileOutputStream output = new FileOutputStream(target)) {
                input.transferTo(output);
            }
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể tạo " + resourceName + ": " + ex.getMessage());
        }
    }

    private void ensureReadme(File targetFolder, String folderName, String example) {
        File readme = new File(targetFolder, "README.txt");
        if (readme.exists()) return;
        try {
            java.nio.file.Files.writeString(readme.toPath(),
                    "DungNT RPG " + folderName + " folder\n\n" +
                            "Put your resource-pack model files here.\n" +
                            "Example: " + example + "\n");
        } catch (IOException ex) {
            plugin.getLogger().warning("Không thể tạo " + folderName + "/README.txt: " + ex.getMessage());
        }
    }

    private List<File> listYmlFiles(File dir) {
        List<File> result = new ArrayList<>();
        if (dir == null) return result;
        File[] files = dir.listFiles();
        if (files == null) return result;
        for (File file : files) {
            String lower = file.getName().toLowerCase(Locale.ROOT);
            if (file.isFile() && (lower.endsWith(".yml") || lower.endsWith(".yaml"))) {
                result.add(file);
            }
        }
        return result;
    }

    // ==================================================
    // GETTERS
    // ==================================================

    public File getFolder() { return folder; }
    public File getConfigFile() { return configFile; }
    public File getPlayerDataFolder() { return playerDataFolder; }
    public File getItemsFolder() { return itemsFolder; }
    public File getMobsFolder() { return mobsFolder; }
    public File getGemsFolder() { return gemsFolder; }
    public File getModelItemsFolder() { return modelItemsFolder; }
    public File getModelMobsFolder() { return modelMobsFolder; }
}