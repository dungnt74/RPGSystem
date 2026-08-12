package dungnt.rpg.item;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    private final JavaPlugin plugin;

    // ==================================================
    // PDC KEYS
    // ==================================================

    private final NamespacedKey itemKey;
    private final NamespacedKey slotKey;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public ItemManager(
            JavaPlugin plugin
    ) {

        this.plugin = plugin;

        this.itemKey =
                new NamespacedKey(
                        plugin,
                        "rpg_item_id"
                );

        this.slotKey =
                new NamespacedKey(
                        plugin,
                        "rpg_equipment_slot"
                );
    }

    // ==================================================
    // RPG ITEM -> ITEM STACK
    // ==================================================

    public ItemStack toItemStack(
            RPGItem rpgItem
    ) {

        if (rpgItem == null) {
            return null;
        }

        ItemStack item =
                new ItemStack(
                        rpgItem.getMaterial()
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        // ==================================================
        // BASIC PDC
        // ==================================================

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        pdc.set(
                itemKey,
                PersistentDataType.STRING,
                rpgItem.getId()
        );

        pdc.set(
                slotKey,
                PersistentDataType.STRING,
                rpgItem.getSlot()
                        .name()
        );

        // ==================================================
        // DISPLAY NAME
        // ==================================================

        meta.setDisplayName(
                rpgItem.getName()
        );

        // ==================================================
        // LORE
        // ==================================================

        List<String> lore =
                new ArrayList<>();

        lore.add(
                "§8§m--------------------"
        );

        lore.add(
                "§7Slot: §e"
                        + formatSlot(
                        rpgItem.getSlot()
                )
        );

        for (StatModifier modifier :
                rpgItem.getStatModifiers()) {

            lore.add(
                    createStatLore(
                            modifier
                    )
            );
        }

        lore.add(
                "§8§m--------------------"
        );

        meta.setLore(lore);

        // ==================================================
        // STAT PDC
        // ==================================================

        for (StatModifier modifier :
                rpgItem.getStatModifiers()) {

            NamespacedKey statKey =
                    createStatKey(
                            modifier.getType()
                    );

            String value =
                    modifier.getModifierType()
                            .name()
                            + ":"
                            + modifier.getAmount();

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
    // ITEM STACK -> RPG ITEM
    // ==================================================

    public RPGItem fromItemStack(
            ItemStack item
    ) {

        if (item == null ||
                item.getType().isAir()) {

            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return null;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String itemId =
                pdc.get(
                        itemKey,
                        PersistentDataType.STRING
                );

        if (itemId == null ||
                itemId.isBlank()) {

            return null;
        }

        String slotName =
                pdc.get(
                        slotKey,
                        PersistentDataType.STRING
                );

        if (slotName == null) {
            return null;
        }

        EquipmentSlot slot;

        try {

            slot =
                    EquipmentSlot.valueOf(
                            slotName.toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            return null;
        }

        String name =
                meta.hasDisplayName()
                        ? meta.getDisplayName()
                        : itemId;

        RPGItem rpgItem =
                new RPGItem(
                        itemId,
                        name,
                        item.getType(),
                        slot
                );


        // ==================================================
        // READ STATS
        // ==================================================

        for (StatType statType :
                StatType.values()) {

            NamespacedKey statKey =
                    createStatKey(
                            statType
                    );

            String value =
                    pdc.get(
                            statKey,
                            PersistentDataType.STRING
                    );

            if (value == null) {
                continue;
            }

            StatModifier modifier =
                    parseModifier(
                            itemId,
                            statType,
                            value
                    );

            if (modifier != null) {

                rpgItem.addStatModifier(
                        modifier
                );
            }
        }

        return rpgItem;
    }
//==========================
// GET RPG ITEM
// ==================================================

    public RPGItem getRPGItem(
            ItemStack item
    ) {

        return fromItemStack(item);
    }

    // ==================================================
    // CHECK RPG ITEM
    // ==================================================

    public boolean isRPGItem(
            ItemStack item
    ) {

        if (item == null ||
                item.getType().isAir()) {

            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        return meta.getPersistentDataContainer()
                .has(
                        itemKey,
                        PersistentDataType.STRING
                );
    }

    // ==================================================
    // GET ITEM ID
    // ==================================================

    public String getItemId(
            ItemStack item
    ) {

        if (item == null) {
            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer()
                .get(
                        itemKey,
                        PersistentDataType.STRING
                );
    }

    // ==================================================
    // GET EQUIPMENT SLOT
    // ==================================================

    public EquipmentSlot getEquipmentSlot(
            ItemStack item
    ) {

        if (item == null) {
            return null;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return null;
        }

        String value =
                meta.getPersistentDataContainer()
                        .get(
                                slotKey,
                                PersistentDataType.STRING
                        );

        if (value == null) {
            return null;
        }

        try {

            return EquipmentSlot.valueOf(
                    value.toUpperCase()
            );

        } catch (IllegalArgumentException exception) {

            return null;
        }
    }

    // ==================================================
    // GET MODIFIERS
    // ==================================================

    public List<StatModifier> getModifiers(
            ItemStack item
    ) {

        List<StatModifier> modifiers =
                new ArrayList<>();

        RPGItem rpgItem =
                fromItemStack(item);

        if (rpgItem == null) {
            return modifiers;
        }

        modifiers.addAll(
                rpgItem.getStatModifiers()
        );

        return modifiers;
    }

    // ==================================================
    // CREATE STAT KEY
    // ==================================================

    private NamespacedKey createStatKey(
            StatType type
    ) {

        return new NamespacedKey(
                plugin,
                "rpg_stat_"
                        + type.name()
                        .toLowerCase()
        );
    }

    // ==================================================
    // PARSE MODIFIER
    // ==================================================

    private StatModifier parseModifier(
            String itemId,
            StatType statType,
            String value
    ) {

        String[] parts =
                value.split(
                        ":",
                        2
                );

        if (parts.length != 2) {
            return null;
        }

        ModifierType modifierType;

        double amount;

        try {

            modifierType =
                    ModifierType.valueOf(
                            parts[0]
                                    .toUpperCase()
                    );

            amount =
                    Double.parseDouble(
                            parts[1]
                    );

        } catch (IllegalArgumentException exception) {

            return null;
        }

        String modifierId =
                "item_"
                        + itemId
                        + "_"
                        + statType.name()
                        .toLowerCase()
                        + "_"
                        + modifierType.name()
                        .toLowerCase();

        return new StatModifier(
                modifierId,
                statType,
                modifierType,
                amount
        );
    }

    // ==================================================
    // STAT LORE
    // ==================================================

    private String createStatLore(
            StatModifier modifier
    ) {

        String statName =
                formatStatName(
                        modifier.getType()
                );

        String prefix =
                modifier.getAmount() >= 0
                        ? "§a+"
                        : "§c";

        double amount =
                modifier.getAmount();

        if (modifier.getModifierType()
                == ModifierType.PERCENT) {

            return "§7"
                    + statName
                    + ": "
                    + prefix
                    + formatNumber(amount)
                    + "%";

        }

        return "§7"
                + statName
                + ": "
                + prefix
                + formatNumber(amount);
    }

    // ==================================================
    // FORMAT STAT NAME
    // ==================================================

    private String formatStatName(
            StatType type
    ) {

        String raw =
                type.name()
                        .toLowerCase()
                        .replace(
                                "_",
                                " "
                        );

        String[] words =
                raw.split(" ");

        StringBuilder result =
                new StringBuilder();

        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {

                result.append(
                        word.substring(1)
                );
            }

            result.append(" ");
        }

        return result.toString().trim();
    }

    // ==================================================
    // FORMAT SLOT
    // ==================================================

    private String formatSlot(
            EquipmentSlot slot
    ) {

        String raw =
                slot.name()
                        .toLowerCase()
                        .replace(
                                "_",
                                " "
                        );

        String[] words =
                raw.split(" ");

        StringBuilder result =
                new StringBuilder();

        for (String word : words) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {

                result.append(
                        word.substring(1)
                );
            }

            result.append(" ");
        }

        return result.toString().trim();
    }

    // ==================================================
    // FORMAT NUMBER
    // ==================================================

    private String formatNumber(
            double value
    ) {

        if (value == Math.rint(value)) {

            return String.format(
                    "%.0f",
                    value
            );
        }

        return String.format(
                "%.2f",
                value
        );
    }
}