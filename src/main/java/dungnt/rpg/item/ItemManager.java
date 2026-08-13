package dungnt.rpg.item;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        pdc.set(
                itemKey,
                PersistentDataType.STRING,
                rpgItem.getId()
        );

        if (rpgItem.getSlot() != null) {

            pdc.set(
                    slotKey,
                    PersistentDataType.STRING,
                    rpgItem.getSlot().name()
            );
        }

        meta.setDisplayName(
                rpgItem.getName()
        );

        writeLore(
                meta,
                pdc
        );

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

        /*
         * Equipment slot có thể chưa được set.
         *
         * Như vậy /itemstat add có thể dùng trước
         * /equipment addslot.
         */
        EquipmentSlot slot = null;

        String slotName =
                pdc.get(
                        slotKey,
                        PersistentDataType.STRING
                );

        if (slotName != null &&
                !slotName.isBlank()) {

            try {

                slot =
                        EquipmentSlot.valueOf(
                                slotName.toUpperCase(Locale.ROOT)
                        );

            } catch (IllegalArgumentException ignored) {

                return null;
            }
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
        // READ STATS FROM PDC
        // ==================================================

        for (StatType statType :
                StatType.values()) {

            String value =
                    pdc.get(
                            createStatKey(statType),
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

    // ==================================================
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
    // ENSURE RPG ITEM ID
    // ==================================================

    public boolean ensureRPGItem(
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

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String itemId =
                pdc.get(
                        itemKey,
                        PersistentDataType.STRING
                );

        if (itemId == null ||
                itemId.isBlank()) {

            pdc.set(
                    itemKey,
                    PersistentDataType.STRING,
                    "custom_" + UUID.randomUUID()
            );
        }

        item.setItemMeta(meta);

        return true;
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
    // SET EQUIPMENT SLOT
    // ==================================================

    public boolean setEquipmentSlot(
            ItemStack item,
            EquipmentSlot slot
    ) {

        if (item == null ||
                item.getType().isAir() ||
                slot == null) {

            return false;
        }

        if (!ensureRPGItem(item)) {
            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        pdc.set(
                slotKey,
                PersistentDataType.STRING,
                slot.name()
        );

        writeLore(
                meta,
                pdc
        );

        item.setItemMeta(meta);

        return true;
    }

    // ==================================================
    // REMOVE EQUIPMENT SLOT
    // ==================================================

    public boolean removeEquipmentSlot(
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

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        pdc.remove(slotKey);

        writeLore(
                meta,
                pdc
        );

        item.setItemMeta(meta);

        return true;
    }

    // ==================================================
    // ADD STAT
    // ==================================================

    public boolean addStat(
            ItemStack item,
            StatType statType,
            ModifierType modifierType,
            double amount
    ) {

        if (item == null ||
                item.getType().isAir() ||
                statType == null ||
                modifierType == null) {

            return false;
        }

        if (!ensureRPGItem(item)) {
            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String value =
                modifierType.name()
                        + ":"
                        + amount;

        /*
         * Mỗi StatType dùng một PDC key.
         * Add lại cùng stat sẽ replace stat cũ.
         */
        pdc.set(
                createStatKey(statType),
                PersistentDataType.STRING,
                value
        );

        writeLore(
                meta,
                pdc
        );

        item.setItemMeta(meta);

        return true;
    }

    // ==================================================
    // REMOVE STAT
    // ==================================================

    public boolean removeStat(
            ItemStack item,
            StatType statType
    ) {

        if (item == null ||
                item.getType().isAir() ||
                statType == null) {

            return false;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        boolean existed =
                pdc.has(
                        createStatKey(statType),
                        PersistentDataType.STRING
                );

        pdc.remove(
                createStatKey(statType)
        );

        writeLore(
                meta,
                pdc
        );

        item.setItemMeta(meta);

        return existed;
    }

    // ==================================================
    // CLEAR ALL STATS
    // ==================================================

    public void clearStats(
            ItemStack item
    ) {

        if (item == null ||
                item.getType().isAir()) {

            return;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        for (StatType statType :
                StatType.values()) {

            pdc.remove(
                    createStatKey(statType)
            );
        }

        writeLore(
                meta,
                pdc
        );

        item.setItemMeta(meta);
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
                    value.toUpperCase(Locale.ROOT)
            );

        } catch (IllegalArgumentException ignored) {

            return null;
        }
    }

    // ==================================================
    // GET MODIFIERS
    // ==================================================

    public List<StatModifier> getModifiers(
            ItemStack item
    ) {

        RPGItem rpgItem =
                fromItemStack(item);

        if (rpgItem == null) {
            return List.of();
        }

        return new ArrayList<>(
                rpgItem.getStatModifiers()
        );
    }

    // ==================================================
    // WRITE LORE
    // ==================================================

    private void writeLore(
            ItemMeta meta,
            PersistentDataContainer pdc
    ) {

        List<String> lore =
                new ArrayList<>();

        lore.add(
                "§8§m--------------------"
        );

        String slotName =
                pdc.get(
                        slotKey,
                        PersistentDataType.STRING
                );

        if (slotName != null) {

            try {

                EquipmentSlot slot =
                        EquipmentSlot.valueOf(
                                slotName.toUpperCase(Locale.ROOT)
                        );

                lore.add(
                        "§7Slot: §e"
                                + formatSlot(slot)
                );

            } catch (IllegalArgumentException ignored) {
                // Không ghi slot lỗi.
            }
        }

        String itemId =
                pdc.get(
                        itemKey,
                        PersistentDataType.STRING
                );

        if (itemId == null) {
            itemId = "unknown";
        }

        for (StatType statType :
                StatType.values()) {

            String value =
                    pdc.get(
                            createStatKey(statType),
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

                lore.add(
                        createStatLore(modifier)
                );
            }
        }

        lore.add(
                "§8§m--------------------"
        );

        meta.setLore(lore);
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
                        .toLowerCase(Locale.ROOT)
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
                value.split(":", 2);

        if (parts.length != 2) {
            return null;
        }

        try {

            ModifierType modifierType =
                    ModifierType.valueOf(
                            parts[0]
                                    .toUpperCase(Locale.ROOT)
                    );

            double amount =
                    Double.parseDouble(parts[1]);

            String modifierId =
                    "item_"
                            + itemId
                            + "_"
                            + statType.name()
                            .toLowerCase(Locale.ROOT)
                            + "_"
                            + modifierType.name()
                            .toLowerCase(Locale.ROOT);

            return new StatModifier(
                    modifierId,
                    statType,
                    modifierType,
                    amount
            );

        } catch (IllegalArgumentException ignored) {

            return null;
        }
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

        String amount =
                formatNumber(
                        modifier.getAmount()
                );

        if (modifier.getModifierType()
                == ModifierType.PERCENT) {

            return "§7"
                    + statName
                    + ": "
                    + prefix
                    + amount
                    + "%";
        }

        return "§7"
                + statName
                + ": "
                + prefix
                + amount;
    }

    // ==================================================
    // FORMAT STAT
    // ==================================================

    private String formatStatName(
            StatType type
    ) {

        String raw =
                type.name()
                        .toLowerCase(Locale.ROOT)
                        .replace("_", " ");

        StringBuilder result =
                new StringBuilder();

        for (String word : raw.split(" ")) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {
                result.append(word.substring(1));
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
                        .toLowerCase(Locale.ROOT)
                        .replace("_", " ");

        StringBuilder result =
                new StringBuilder();

        for (String word : raw.split(" ")) {

            if (word.isEmpty()) {
                continue;
            }

            result.append(
                    Character.toUpperCase(
                            word.charAt(0)
                    )
            );

            if (word.length() > 1) {
                result.append(word.substring(1));
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
                    Locale.US,
                    "%.0f",
                    value
            );
        }

        return String.format(
                Locale.US,
                "%.2f",
                value
        );
    }
}
