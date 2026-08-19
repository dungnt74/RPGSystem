package dungnt.rpg.player;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.ClassLevelBonus;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.stats.StatType;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final MyRPG plugin;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final File dataFolder;

    public PlayerManager(MyRPG plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerData");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public PlayerData getData(Player player) {
        return player == null ? null : getData(player.getUniqueId());
    }

    public PlayerData getData(UUID uuid) {
        if (uuid == null) return null;
        PlayerData data = players.get(uuid);
        if (data == null) {
            data = loadData(uuid);
            players.put(uuid, data);
        }
        plugin.getStatManager().syncBaseStats(uuid, data.getStats());
        return data;
    }

    private PlayerData loadData(UUID uuid) {
        PlayerData data = new PlayerData(uuid);
        File file = new File(dataFolder, uuid + ".yml");
        if (!file.exists()) return data;

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        data.setLevel(cfg.getInt("level", 1));
        data.setExperience(cfg.getDouble("experience", 0));
        data.setMana(cfg.getDouble("mana", 20));
        data.setMaxMana(cfg.getDouble("max-mana", 20));
        data.setMaxHealth(cfg.getDouble("max-health", 20));
        data.setHealth(cfg.getDouble("health", data.getMaxHealth()));

        String classId = cfg.getString("class");
        if (classId != null) data.setRpgClass(plugin.getClassManager().getClass(classId));
        return data;
    }

    public void saveData(Player player) {
        if (player != null) saveData(player.getUniqueId());
    }

    public void saveData(UUID uuid) {
        if (uuid == null) return;
        PlayerData data = players.get(uuid);
        if (data == null) return;
        Player player = plugin.getServer().getPlayer(uuid);
        if (player != null && player.isOnline()) {
            data.setHealth(player.getHealth());
            data.setMaxHealth(getEffectiveMaxHealth(uuid));
        }

        File file = new File(dataFolder, uuid + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("uuid", uuid.toString());
        cfg.set("class", data.getRpgClass() == null ? null : data.getRpgClass().getId());
        cfg.set("level", data.getLevel());
        cfg.set("experience", data.getExperience());
        cfg.set("health", data.getHealth());
        cfg.set("max-health", data.getMaxHealth());
        cfg.set("mana", data.getMana());
        cfg.set("max-mana", data.getMaxMana());
        try { cfg.save(file); }
        catch (IOException e) { plugin.getLogger().warning("Không thể lưu player " + uuid + ": " + e.getMessage()); }
    }

    public void saveAll() {
        for (UUID uuid : players.keySet()) saveData(uuid);
    }

    public void unload(Player player) {
        if (player == null) return;
        UUID uuid = player.getUniqueId();
        saveData(uuid);
        if (plugin.getEquipmentManager() != null) plugin.getEquipmentManager().remove(uuid);
        plugin.getStatManager().clearModifiers(uuid);
        players.remove(uuid);
    }

    public void setClass(Player player, RPGClass rpgClass) {
        if (player == null || rpgClass == null) return;
        PlayerData data = getData(player);
        UUID uuid = player.getUniqueId();
        RPGClass oldClass = data.getRpgClass();
        if (oldClass != null) {
            plugin.getStatManager().removeClass(uuid, oldClass);
            plugin.getStatManager().removeClassGrowth(uuid, oldClass);
        }
        data.setRpgClass(rpgClass);
        plugin.getStatManager().applyClass(uuid, rpgClass);
        ClassLevelBonus.apply(uuid, rpgClass, data.getLevel(), plugin.getStatManager());
        if (plugin.getEquipmentListener() != null) plugin.getEquipmentListener().refreshEquipment(player);
        refreshResources(player);
        saveData(player);
    }

    public void removeClassAndResetStats(Player player) {
        if (player == null) return;
        UUID uuid = player.getUniqueId();
        PlayerData data = getData(player);

        // Chỉ gỡ modifier ĐẾN TỪ CLASS (id bắt đầu bằng "class_"/
        // "class_growth_..."). KHÔNG dùng statManager.clear()/
        // equipmentManager.clear() ở đây vì chúng xoá luôn cả
        // modifier của item/gem đang mặc — buff của trang bị phải
        // được giữ nguyên khi bỏ Class.
        RPGClass oldClass = data.getRpgClass();
        if (oldClass != null) {
            plugin.getStatManager().removeClass(uuid, oldClass);
            plugin.getStatManager().removeClassGrowth(uuid, oldClass);
        }

        // Trả BASE STATS về mặc định ban đầu (Attack 10, Max HP 20,
        // Max Mana 100, Crit Damage 150%...), không phải về 0.
        data.getStats().resetToDefaultBase();
        data.setRpgClass(null);
        plugin.getStatManager().syncBaseStats(uuid, data.getStats());

        // Đồng bộ lại toàn bộ equipment đang mặc (main hand, giáp,
        // GUI phụ kiện) để chắc chắn buff item vẫn còn nguyên sau
        // khi base stats vừa được ghi đè.
        if (plugin.getEquipmentListener() != null) {
            plugin.getEquipmentListener().refreshEquipment(player);
        }

        refreshResources(player);
        saveData(player);
    }

    public void remove(Player player) { unload(player); }

    public boolean hasData(Player player) { return player != null && players.containsKey(player.getUniqueId()); }

    public void refreshStats(Player player) {
        if (player == null) return;
        PlayerData data = getData(player);
        UUID uuid = player.getUniqueId();
        if (data.getRpgClass() != null) {
            plugin.getStatManager().removeClass(uuid, data.getRpgClass());
            plugin.getStatManager().removeClassGrowth(uuid, data.getRpgClass());
        }
        plugin.getStatManager().removeLevel(uuid);
        if (data.getRpgClass() != null) plugin.getStatManager().applyClass(uuid, data.getRpgClass());
        ClassLevelBonus.apply(uuid, data.getRpgClass(), data.getLevel(), plugin.getStatManager());
        refreshResources(player);
    }

    public double getEffectiveMaxHealth(UUID uuid) {
        return Math.max(1, plugin.getStatManager().getStat(uuid, StatType.MAX_HEALTH));
    }

    public double getEffectiveMaxMana(UUID uuid) {
        return Math.max(0, plugin.getStatManager().getStat(uuid, StatType.MAX_MANA));
    }

    /**
     * Refresh RPG health/mana while preserving the player's CURRENT live health.
     * This method must not heal the player when equipment/hand changes.
     */
    public void refreshResources(Player player) {
        refreshResources(player, false);
    }

    /**
     * Refresh resources. When restoreStoredHealth is true (join/load), the saved
     * RPG health value is restored instead of using the temporary Bukkit health.
     */
    public void refreshResources(Player player, boolean restoreStoredHealth) {
        if (player == null || !player.isOnline()) return;

        PlayerData data = getData(player);
        UUID uuid = player.getUniqueId();

        double maxHealth = getEffectiveMaxHealth(uuid);
        double maxMana = getEffectiveMaxMana(uuid);
        if (maxMana <= 0) maxMana = data.getMaxMana();

        data.setMaxHealth(maxHealth);
        data.setMaxMana(maxMana);

        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
        if (attr != null) {
            // Bukkit/Paper or another plugin may have an AttributeModifier which
            // makes 20 RPG HP appear as 20.8. Set the RPG base first, then use a
            // small correction modifier so the FINAL Bukkit value is exactly the
            // RPG value without deleting modifiers owned by other plugins.
            attr.setBaseValue(maxHealth);
            normalizeMaxHealthAttribute(attr, maxHealth);

            double hp = restoreStoredHealth ? data.getHealth() : player.getHealth();
            hp = Math.max(0.0, Math.min(hp, maxHealth));
            player.setHealth(hp);
            data.setHealth(hp);
        }

        player.setHealthScaled(true);
        player.setHealthScale(20.0);
    }

    private static final UUID RPG_HEALTH_CORRECTION_UUID =
            UUID.fromString("d6f0e0a4-5b1c-4a43-9e76-9a0f4d7c6e11");

    /**
     * Forces Bukkit's final MAX_HEALTH value to match the RPG value while keeping
     * modifiers belonging to other plugins intact.
     */
    private void normalizeMaxHealthAttribute(AttributeInstance attr, double target) {
        if (attr == null) return;

        AttributeModifier old = null;
        for (AttributeModifier modifier : attr.getModifiers()) {
            if (RPG_HEALTH_CORRECTION_UUID.equals(modifier.getUniqueId())) {
                old = modifier;
                break;
            }
        }
        if (old != null) {
            attr.removeModifier(old);
        }

        double currentWithoutCorrection = attr.getValue();
        double correction = target - currentWithoutCorrection;

        if (Math.abs(correction) > 0.000001) {
            AttributeModifier modifier = new AttributeModifier(
                    RPG_HEALTH_CORRECTION_UUID,
                    "myrpg_health_correction",
                    correction,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            attr.addModifier(modifier);
        }
    }

}