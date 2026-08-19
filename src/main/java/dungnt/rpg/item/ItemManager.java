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
import java.lang.reflect.Method;

public class ItemManager {

    private final JavaPlugin plugin;

    // ==================================================
    // PDC KEYS
    // ==================================================

    private final NamespacedKey itemKey;
    private final NamespacedKey slotKey;
    private final NamespacedKey socketCountKey;
    private final NamespacedKey baseLoreKey;
    private final NamespacedKey rarityKey;
    private final NamespacedKey levelKey;
    private final NamespacedKey upgradeLevelKey;
    private final NamespacedKey maxUpgradeKey;
    private final NamespacedKey modelKey;

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

        this.socketCountKey =
                new NamespacedKey(plugin, "socket_count");

        this.baseLoreKey =
                new NamespacedKey(plugin, "rpg_base_lore");
        this.rarityKey = new NamespacedKey(plugin, "rpg_rarity");
        this.levelKey = new NamespacedKey(plugin, "rpg_item_level");
        this.upgradeLevelKey = new NamespacedKey(plugin, "rpg_upgrade_level");
        this.maxUpgradeKey = new NamespacedKey(plugin, "rpg_max_upgrade");
        this.modelKey = new NamespacedKey(plugin, "rpg_model");
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

        pdc.set(
                socketCountKey,
                PersistentDataType.INTEGER,
                rpgItem.getSocketCount()
        );

        pdc.set(
                baseLoreKey,
                PersistentDataType.STRING,
                String.join("\n", rpgItem.getBaseLore())
        );

        pdc.set(rarityKey, PersistentDataType.STRING, rpgItem.getRarity().name());
        pdc.set(levelKey, PersistentDataType.INTEGER, rpgItem.getItemLevel());
        pdc.set(upgradeLevelKey, PersistentDataType.INTEGER, rpgItem.getUpgradeLevel());
        pdc.set(maxUpgradeKey, PersistentDataType.INTEGER, rpgItem.getMaxUpgradeLevel());

        if (rpgItem.getModel() != null) {
            pdc.set(modelKey, PersistentDataType.STRING, rpgItem.getModel());
            applyItemModel(meta, rpgItem.getModel());
        }

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

        Integer sockets = pdc.get(socketCountKey, PersistentDataType.INTEGER);
        rpgItem.setSocketCount(sockets == null ? 0 : sockets);

        String rarity = pdc.get(rarityKey, PersistentDataType.STRING);
        if (rarity != null) {
            try { rpgItem.setRarity(Rarity.valueOf(rarity.toUpperCase(Locale.ROOT))); }
            catch (IllegalArgumentException ignored) { }
        }
        Integer level = pdc.get(levelKey, PersistentDataType.INTEGER);
        rpgItem.setItemLevel(level == null ? 1 : level);
        Integer maxUpgrade = pdc.get(maxUpgradeKey, PersistentDataType.INTEGER);
        rpgItem.setMaxUpgradeLevel(maxUpgrade == null ? 0 : maxUpgrade);
        Integer upgrade = pdc.get(upgradeLevelKey, PersistentDataType.INTEGER);
        rpgItem.setUpgradeLevel(upgrade == null ? 0 : upgrade);
        rpgItem.setModel(pdc.get(modelKey, PersistentDataType.STRING));

        String baseLore = pdc.get(baseLoreKey, PersistentDataType.STRING);
        if (baseLore != null && !baseLore.isEmpty()) {
            rpgItem.setBaseLore(List.of(baseLore.split("\\n", -1)));
        }

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

    /**
     * Applies Paper 1.21+ item_model using reflection so this remains safe
     * if the project is opened against an older API.
     */
    private void applyItemModel(ItemMeta meta, String model) {
        if (model == null || model.isBlank()) return;
        try {
            Method method = meta.getClass().getMethod("setItemModel", NamespacedKey.class);
            method.invoke(meta, new NamespacedKey("dungntrpg", model.toLowerCase(Locale.ROOT)));
        } catch (ReflectiveOperationException ignored) {
            // The model id is still preserved in PDC for forward compatibility.
        }
    }

    // ==================================================
    // WRITE LORE
    // ==================================================

    private void writeLore(
            ItemMeta meta,
            PersistentDataContainer pdc
    ) {
        List<String> lore = new ArrayList<>();

        String baseLore = pdc.get(baseLoreKey, PersistentDataType.STRING);
        if (baseLore != null && !baseLore.isEmpty()) {
            for (String line : baseLore.split("\\n", -1)) {
                lore.add(line);
            }
        }

        lore.add("§8§m--------------------");

        String slotName = pdc.get(slotKey, PersistentDataType.STRING);
        if (slotName != null) {
            try {
                EquipmentSlot slot = EquipmentSlot.valueOf(slotName.toUpperCase(Locale.ROOT));
                lore.add("§7Slot: §e" + formatSlot(slot));
            } catch (IllegalArgumentException ignored) {
            }
        }

        String itemId = pdc.get(itemKey, PersistentDataType.STRING);
        if (itemId == null) itemId = "unknown";

        String rarity = pdc.get(rarityKey, PersistentDataType.STRING);
        if (rarity != null) {
            try {
                Rarity value = Rarity.valueOf(rarity.toUpperCase(Locale.ROOT));
                lore.add(value.getColor() + "Rarity: " + value.getDisplayName());
            } catch (IllegalArgumentException ignored) { }
        }

        Integer level = pdc.get(levelKey, PersistentDataType.INTEGER);
        if (level != null) lore.add("§7Item Level: §e" + level);

        Integer upgrade = pdc.get(upgradeLevelKey, PersistentDataType.INTEGER);
        Integer maxUpgrade = pdc.get(maxUpgradeKey, PersistentDataType.INTEGER);
        if (upgrade != null && maxUpgrade != null && maxUpgrade > 0) {
            lore.add("§7Upgrade: §e+" + upgrade + "§7/§e+" + maxUpgrade);
        }

        for (StatType statType : StatType.values()) {
            String value = pdc.get(createStatKey(statType), PersistentDataType.STRING);
            if (value == null) continue;
            StatModifier modifier = parseModifier(itemId, statType, value);
            if (modifier != null) lore.add(createStatLore(modifier));
        }

        Integer sockets = pdc.get(socketCountKey, PersistentDataType.INTEGER);
        if (sockets != null && sockets > 0) {
            lore.add("§8Socket: §e" + Math.min(4, sockets));
        }

        lore.add("§8§m--------------------");
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
