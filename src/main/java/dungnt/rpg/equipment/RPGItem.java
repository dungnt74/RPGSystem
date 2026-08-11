package dungnt.rpg.equipment;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RPGItem {

    private final JavaPlugin plugin;

    private final String itemId;
    private final String name;
    private final Material material;

    private final List<StatModifier> modifiers =
            new ArrayList<>();

    public RPGItem(
            JavaPlugin plugin,
            String itemId,
            String name,
            Material material
    ) {

        this.plugin = plugin;
        this.itemId = itemId;
        this.name = name;
        this.material = material;
    }

    // ==================================================
    // BASIC
    // ==================================================

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    // ==================================================
    // STAT
    // ==================================================

    public RPGItem addStat(
            StatType statType,
            ModifierType modifierType,
            double amount
    ) {

        if (statType == null ||
                modifierType == null) {

            return this;
        }

        String id =
                "item_" +
                        itemId +
                        "_" +
                        statType.name().toLowerCase();

        modifiers.add(
                new StatModifier(
                        id,
                        statType,
                        modifierType,
                        amount
                )
        );

        return this;
    }

    public List<StatModifier> getModifiers() {

        return Collections.unmodifiableList(
                modifiers
        );
    }

    // ==================================================
    // CREATE ITEM
    // ==================================================

    public ItemStack createItem() {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(
                "§f" + name
        );

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        // ==================================================
        // ITEM ID
        // ==================================================

        NamespacedKey itemIdKey =
                new NamespacedKey(
                        plugin,
                        "rpg_item_id"
                );

        pdc.set(
                itemIdKey,
                PersistentDataType.STRING,
                itemId
        );

        // ==================================================
        // STAT DATA
        // ==================================================

        for (StatModifier modifier :
                modifiers) {

            NamespacedKey statKey =
                    new NamespacedKey(
                            plugin,
                            "rpg_stat_" +
                                    modifier.getType()
                                            .name()
                                            .toLowerCase()
                    );

            String value =
                    modifier.getModifierType()
                            .name()
                            + ":" +
                            modifier.getAmount();

            pdc.set(
                    statKey,
                    PersistentDataType.STRING,
                    value
            );
        }

        item.setItemMeta(meta);

        return item;
    }

    // ==================================================
    // CHECK RPG ITEM
    // ==================================================

    public static boolean isRPGItem(
            JavaPlugin plugin,
            ItemStack item
    ) {

        if (item == null ||
                item.getType() == Material.AIR) {

            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        NamespacedKey key =
                new NamespacedKey(
                        plugin,
                        "rpg_item_id"
                );

        return meta.getPersistentDataContainer()
                .has(
                        key,
                        PersistentDataType.STRING
                );
    }

    // ==================================================
    // GET ITEM ID
    // ==================================================

    public static String getItemId(
            JavaPlugin plugin,
            ItemStack item
    ) {

        if (!isRPGItem(plugin, item)) {
            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return null;
        }

        NamespacedKey key =
                new NamespacedKey(
                        plugin,
                        "rpg_item_id"
                );

        return meta.getPersistentDataContainer()
                .get(
                        key,
                        PersistentDataType.STRING
                );
    }
}