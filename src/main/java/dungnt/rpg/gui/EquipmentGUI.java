package dungnt.rpg.gui;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;

import dungnt.rpg.item.RPGItem;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.level.LevelManager;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatType;
import dungnt.rpg.player.PlayerData;
import dungnt.otherskills.OtherSkillManager;
import dungnt.otherskills.OtherSkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentGUI {

    public static final String TITLE =
            "§8§lEquipment";

    private final MyRPG plugin;

    /*
     * Vanilla-backed slots are saved by Minecraft itself.
     * Virtual RPG equipment slots (RING1, RING2, BELT, etc.)
     * need their own persistent storage.
     */
    private final File saveFolder;

    /*
     * Equipment trong GUI là virtual equipment.
     *
     * Main hand KHÔNG có ô trong GUI nhưng được xử lý
     * bởi EquipmentListener và vẫn cộng stat.
     */
    private final Map<UUID, Map<Integer, ItemStack>>
            equipmentItems =
            new java.util.HashMap<>();

    private static final Map<Integer, EquipmentSlot>
            GUI_SLOTS =
            new LinkedHashMap<>();

    static {

        GUI_SLOTS.put(10, EquipmentSlot.HELMET);
        GUI_SLOTS.put(12, EquipmentSlot.OFF_HAND);
        GUI_SLOTS.put(14, EquipmentSlot.RING1);
        GUI_SLOTS.put(16, EquipmentSlot.WINGS);

        GUI_SLOTS.put(19, EquipmentSlot.CHESTPLATE);
        GUI_SLOTS.put(21, EquipmentSlot.GLOVES);
        GUI_SLOTS.put(23, EquipmentSlot.RING2);
        GUI_SLOTS.put(25, EquipmentSlot.BADGE);

        GUI_SLOTS.put(28, EquipmentSlot.LEGGINGS);
        GUI_SLOTS.put(30, EquipmentSlot.JADE);
        GUI_SLOTS.put(32, EquipmentSlot.EARRING);
        GUI_SLOTS.put(34, EquipmentSlot.PET);

        GUI_SLOTS.put(37, EquipmentSlot.BOOTS);
        GUI_SLOTS.put(39, EquipmentSlot.BELT);
        GUI_SLOTS.put(41, EquipmentSlot.NECKLACE);
        GUI_SLOTS.put(43, EquipmentSlot.MOUNT);
    }

    public EquipmentGUI(
            MyRPG plugin
    ) {
        this.plugin = plugin;

        this.saveFolder =
                new File(
                        plugin.getDataFolder(),
                        "equipment"
                );

        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
    }

    // ==================================================
    // PERSISTENCE
    // ==================================================

    private File getSaveFile(UUID uuid) {
        return new File(
                saveFolder,
                uuid.toString() + ".yml"
        );
    }

    public void load(Player player) {

        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        File file = getSaveFile(uuid);

        Map<Integer, ItemStack> items =
                new java.util.HashMap<>();

        if (file.exists()) {

            YamlConfiguration config =
                    YamlConfiguration.loadConfiguration(file);

            for (Map.Entry<Integer, EquipmentSlot> entry :
                    GUI_SLOTS.entrySet()) {

                EquipmentSlot slot = entry.getValue();

                if (slot.isVanillaBacked()) {
                    continue;
                }

                ItemStack item =
                        config.getItemStack(
                                "slots." + entry.getKey()
                        );

                if (isCompatibleStoredItem(item, slot)) {
                    items.put(
                            entry.getKey(),
                            item.clone()
                    );
                }
            }
        }

        if (items.isEmpty()) {
            equipmentItems.remove(uuid);
        } else {
            equipmentItems.put(uuid, items);
        }
    }

    public void save(UUID uuid) {

        if (uuid == null) {
            return;
        }

        File file = getSaveFile(uuid);
        YamlConfiguration config =
                new YamlConfiguration();

        Map<Integer, ItemStack> items =
                equipmentItems.get(uuid);

        if (items != null) {

            for (Map.Entry<Integer, ItemStack> entry :
                    items.entrySet()) {

                EquipmentSlot slot =
                        GUI_SLOTS.get(entry.getKey());

                if (slot == null ||
                        slot.isVanillaBacked() ||
                        !isCompatibleStoredItem(
                                entry.getValue(),
                                slot)) {

                    continue;
                }

                config.set(
                        "slots." + entry.getKey(),
                        entry.getValue().clone()
                );
            }
        }

        try {
            config.save(file);
        } catch (IOException exception) {
            plugin.getLogger().warning(
                    "Không thể lưu equipment của "
                            + uuid
                            + ": "
                            + exception.getMessage()
            );
        }
    }

    public void saveAll() {

        for (UUID uuid :
                new java.util.ArrayList<>(
                        equipmentItems.keySet()
                )) {

            save(uuid);
        }
    }

    private boolean isCompatibleStoredItem(
            ItemStack item,
            EquipmentSlot expectedSlot
    ) {

        if (item == null ||
                item.getType().isAir() ||
                expectedSlot == null) {

            return false;
        }

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(item);

        return rpgItem != null &&
                rpgItem.getSlot() == expectedSlot;
    }

    // ==================================================
    // OPEN
    // ==================================================

    public void open(
            Player player
    ) {

        if (player == null) {
            return;
        }

        UUID uuid =
                player.getUniqueId();

        Inventory inventory =
                Bukkit.createInventory(
                        new EquipmentHolder(uuid),
                        54,
                        TITLE
                );

        fillBackground(inventory);

        // Slot 4: thông tin nhân vật, luôn được tạo lại khi mở GUI
        // để HP/Mana/Stats phản ánh giá trị hiện tại của player.
        inventory.setItem(
                4,
                createInfoItem(player)
        );

        // Slot 8: secondary skill levels.
        inventory.setItem(
                8,
                createLevelItem(player)
        );

        Map<Integer, ItemStack> items =
                equipmentItems.computeIfAbsent(
                        uuid,
                        key -> new java.util.HashMap<>()
                );

        for (Map.Entry<Integer, EquipmentSlot> entry :
                GUI_SLOTS.entrySet()) {

            int slot =
                    entry.getKey();

            EquipmentSlot equipmentSlot =
                    entry.getValue();

            /*
             * Các slot "vanilla-backed" (main hand không có ô riêng,
             * off hand, helmet, chestplate, leggings, boots) luôn
             * hiển thị TRỰC TIẾP từ inventory thật của player,
             * không dùng bản lưu ảo.
             */
            ItemStack item =
                    equipmentSlot.isVanillaBacked()
                            ? getVanillaItem(player, equipmentSlot)
                            : items.get(slot);

            if (isRealItem(item)) {

                inventory.setItem(
                        slot,
                        item.clone()
                );

            } else {

                inventory.setItem(
                        slot,
                        createEmptySlot(
                                equipmentSlot
                        )
                );
            }
        }

        player.openInventory(inventory);
    }

    // ==================================================
    // VANILLA ITEM (helmet/chestplate/leggings/boots/off-hand)
    // ==================================================

    public ItemStack getVanillaItem(
            Player player,
            EquipmentSlot slot
    ) {

        if (player == null || slot == null) {
            return null;
        }

        return switch (slot) {
            case HELMET -> player.getInventory().getHelmet();
            case CHESTPLATE -> player.getInventory().getChestplate();
            case LEGGINGS -> player.getInventory().getLeggings();
            case BOOTS -> player.getInventory().getBoots();
            case OFF_HAND -> player.getInventory().getItemInOffHand();
            default -> null;
        };
    }

    // ==================================================
    // SET VANILLA ITEM
    // ==================================================

    public void setVanillaItem(
            Player player,
            EquipmentSlot slot,
            ItemStack item
    ) {

        if (player == null || slot == null) {
            return;
        }

        ItemStack toSet =
                item == null
                        ? new ItemStack(Material.AIR)
                        : item.clone();

        switch (slot) {
            case HELMET -> player.getInventory().setHelmet(toSet);
            case CHESTPLATE -> player.getInventory().setChestplate(toSet);
            case LEGGINGS -> player.getInventory().setLeggings(toSet);
            case BOOTS -> player.getInventory().setBoots(toSet);
            case OFF_HAND -> player.getInventory().setItemInOffHand(toSet);
            default -> { /* not vanilla-backed, nothing to do */ }
        }
    }

    // ==================================================
    // PLAYER INFO
    // ==================================================

    private ItemStack createInfoItem(
            Player player
    ) {

        ItemStack item =
                new ItemStack(Material.NETHER_STAR);

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName("§e§lThông tin");

        UUID uuid =
                player.getUniqueId();

        StatManager statManager =
                plugin.getStatManager();

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        LevelManager levelManager =
                plugin.getLevelManager();

        double maxHealth =
                statManager.getStat(
                        uuid,
                        StatType.MAX_HEALTH
                );

        double currentHealth =
                Math.max(
                        0.0,
                        Math.min(
                                player.getHealth(),
                                maxHealth
                        )
                );

        double maxMana =
                statManager.getStat(
                        uuid,
                        StatType.MAX_MANA
                );

        double currentMana =
                data != null
                        ? data.getMana()
                        : maxMana;

        double currentExp =
                data != null
                        ? data.getExperience()
                        : 0.0;

        double requiredExp =
                levelManager.getRequiredExperience(
                        player
                );

        double percentage =
                levelManager.getExperiencePercentage(
                        player
                );



        RPGClass rpgClass =
                data != null
                        ? data.getRpgClass()
                        : null;

        String classId =
                rpgClass != null
                        ? rpgClass.getId()
                        : "";

        StatType offensiveType;

        if ("mage".equalsIgnoreCase(classId)) {

            offensiveType =
                    StatType.MAGIC_ATTACK;

        } else if ("archer".equalsIgnoreCase(classId)) {

            offensiveType =
                    StatType.BOW_ATTACK;

        } else {

            // Warrior, Assassin và người chưa chọn class
            // dùng Attack.
            offensiveType =
                    StatType.ATTACK;
        }


        double offensiveValue =
                statManager.getStat(
                        uuid,
                        offensiveType
                );

        double defense =
                statManager.getStat(
                        uuid,
                        StatType.DEFENSE
                );

        double critDamage =
                statManager.getStat(
                        uuid,
                        StatType.CRIT_DAMAGE
                );

        double critChance =
                statManager.getStat(
                        uuid,
                        StatType.CRIT_CHANCE
                );

        String offensiveName =
                switch (offensiveType) {
                    case MAGIC_ATTACK -> "Magic Attack";
                    case BOW_ATTACK -> "Bow Attack";
                    default -> "Attack";
                };

        java.util.List<String> lore =
                new java.util.ArrayList<>();

        String className =
                rpgClass != null && rpgClass.getName() != null
                        ? rpgClass.getName()
                        : "Chưa chọn";

        lore.add("§e⚔ Class: §f" + className);

        lore.add("§c❤ HP: §f"
                + formatNumber(currentHealth)
                + " / "
                + formatNumber(maxHealth));

        lore.add("§b✦ Mana: §f"
                + formatNumber(currentMana)
                + " / "
                + formatNumber(maxMana));

        lore.add("§6⚔ " + offensiveName + ": §f"
                + formatNumber(offensiveValue));

        lore.add("§7🛡 Defense: §f"
                + formatNumber(defense));

        lore.add("§d✹ Crit Damage: §f"
                + formatNumber(critDamage)
                + "%");

        lore.add("§e✦ Crit Chance: §f"
                + formatNumber(critChance)
                + "%");

        lore.add("");

        lore.add("§7EXP: §a"
                + String.format(
                "%.1f",
                currentExp
        )
                + " §7/ §a"
                + String.format(
                "%.1f",
                requiredExp
        )
                + " §7("
                + String.format(
                "%.1f",
                percentage
        )
                + "%)");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createLevelItem(Player player) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName("§e§lLevel");
        java.util.List<String> lore = new java.util.ArrayList<>();
        PlayerData data = plugin.getPlayerManager().getData(player);
        LevelManager levelManager = plugin.getLevelManager();
        OtherSkillManager other = plugin.getOtherSkillManager();

        int classLevel = data != null ? data.getLevel() : 1;
        double classExp = data != null ? data.getExperience() : 0.0;
        double classReq = levelManager.getRequiredExperience(player);
        double classPct = classReq <= 0 ? 100.0 : Math.max(0, Math.min(100, classExp * 100.0 / classReq));

        lore.add("§eLevel class: §f" + classLevel);
        lore.add("§7EXP: §a" + formatNumber(classExp) + "§7/§a" + formatNumber(classReq) + " §7(" + String.format(java.util.Locale.US, "%.1f", classPct) + "%)");
        lore.add("");

        for (OtherSkillType type : OtherSkillType.values()) {
            var skillData = other.getData(player);
            int level = skillData.getLevel(type);
            double exp = skillData.getExperience(type);
            double req = other.getRequiredExperience(type, level);
            double pct = req <= 0 ? 100.0 : Math.max(0, Math.min(100, exp * 100.0 / req));
            String name = switch (type) {
                case MOVEMENT -> "Di chuyển";
                case MINING -> "Mining";
                case WOODCUTTING -> "Chặt gỗ";
                case FARMING -> "Farming";
            };
            lore.add("§eLevel " + name + ": §f" + level);
            lore.add("§7EXP: §a" + formatNumber(exp) + "§7/§a" + formatNumber(req) + " §7(" + String.format(java.util.Locale.US, "%.1f", pct) + "%)");
            lore.add("");
        }

        if (!lore.isEmpty()) lore.remove(lore.size() - 1);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private String formatNumber(
            double value
    ) {

        if (Math.abs(value - Math.rint(value)) < 0.0001) {
            return String.format(
                    java.util.Locale.US,
                    "%.0f",
                    value
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.1f",
                value
        );
    }

    // ==================================================
    // BACKGROUND
    // ==================================================

    private void fillBackground(
            Inventory inventory
    ) {

        ItemStack glass =
                createGlass(
                        Material.GRAY_STAINED_GLASS_PANE,
                        " "
                );

        for (int slot = 0; slot < 54; slot++) {

            inventory.setItem(
                    slot,
                    glass.clone()
            );
        }
    }

    // ==================================================
    // EMPTY SLOT
    // ==================================================

    public ItemStack createEmptySlot(
            EquipmentSlot slot
    ) {

        return createGlass(
                Material.RED_STAINED_GLASS_PANE,
                "§c§l" + formatSlot(slot)
        );
    }

    // ==================================================
    // GLASS
    // ==================================================

    private ItemStack createGlass(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(material);

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }

    // ==================================================
    // SAVE GUI
    // ==================================================

    public void save(
            Player player,
            Inventory inventory
    ) {

        if (player == null || inventory == null) {
            return;
        }

        UUID uuid =
                player.getUniqueId();

        Map<Integer, ItemStack> items =
                equipmentItems.computeIfAbsent(
                        uuid,
                        key -> new java.util.HashMap<>()
                );

        items.clear();

        for (Map.Entry<Integer, EquipmentSlot> entry :
                GUI_SLOTS.entrySet()) {

            /*
             * Slot vanilla-backed không lưu bản ảo — inventory thật
             * của player đã là nguồn dữ liệu (xem syncEquipment /
             * removeEquipment trong EquipmentGUIListener).
             */
            if (entry.getValue().isVanillaBacked()) {
                continue;
            }

            int slot =
                    entry.getKey();

            ItemStack item =
                    inventory.getItem(slot);

            if (isRealItem(item)) {

                items.put(
                        slot,
                        item.clone()
                );
            }
        }
    }

    // ==================================================
    // STORED ITEMS
    // ==================================================

    public Map<Integer, ItemStack> getStoredItems(
            UUID uuid
    ) {

        if (uuid == null) {
            return Map.of();
        }

        Map<Integer, ItemStack> items =
                equipmentItems.get(uuid);

        if (items == null || items.isEmpty()) {
            return Map.of();
        }

        Map<Integer, ItemStack> copy =
                new LinkedHashMap<>();

        for (Map.Entry<Integer, ItemStack> entry :
                items.entrySet()) {

            if (isRealItem(entry.getValue())) {

                copy.put(
                        entry.getKey(),
                        entry.getValue().clone()
                );
            }
        }

        return Collections.unmodifiableMap(copy);
    }

    // ==================================================
    // SET STORED ITEM
    // ==================================================

    public void setItem(
            UUID uuid,
            int slot,
            ItemStack item
    ) {

        if (uuid == null ||
                !GUI_SLOTS.containsKey(slot)) {

            return;
        }

        Map<Integer, ItemStack> items =
                equipmentItems.computeIfAbsent(
                        uuid,
                        key -> new java.util.HashMap<>()
                );

        if (!isRealItem(item)) {

            items.remove(slot);

            return;
        }

        items.put(
                slot,
                item.clone()
        );

        // Persist immediately so a server restart/crash cannot
        // silently discard virtual equipment.
        save(uuid);
    }

    // ==================================================
    // GET ITEM
    // ==================================================

    public ItemStack getItem(
            UUID uuid,
            int slot
    ) {

        Map<Integer, ItemStack> items =
                equipmentItems.get(uuid);

        if (items == null) {
            return null;
        }

        ItemStack item =
                items.get(slot);

        return isRealItem(item)
                ? item.clone()
                : null;
    }

    // ==================================================
    // REMOVE ITEM
    // ==================================================

    public ItemStack removeItem(
            UUID uuid,
            int slot
    ) {

        Map<Integer, ItemStack> items =
                equipmentItems.get(uuid);

        if (items == null) {
            return null;
        }

        ItemStack item =
                items.remove(slot);

        save(uuid);

        return isRealItem(item)
                ? item.clone()
                : null;
    }

    // ==================================================
    // CLEAR STORED EQUIPMENT
    // ==================================================

    public void clear(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        equipmentItems.remove(uuid);
    }

    // ==================================================
    // SLOT
    // ==================================================

    public EquipmentSlot getEquipmentSlot(
            int guiSlot
    ) {

        return GUI_SLOTS.get(guiSlot);
    }

    public boolean isEquipmentSlot(
            int slot
    ) {

        return GUI_SLOTS.containsKey(slot);
    }

    public static Map<Integer, EquipmentSlot> getGuiSlots() {

        return Collections.unmodifiableMap(
                GUI_SLOTS
        );
    }

    // ==================================================
    // REAL ITEM
    // ==================================================

    public boolean isRealItem(
            ItemStack item
    ) {

        if (item == null ||
                item.getType().isAir()) {

            return false;
        }

        Material type =
                item.getType();

        return type != Material.RED_STAINED_GLASS_PANE
                && type != Material.GRAY_STAINED_GLASS_PANE;
    }

    // ==================================================
    // FORMAT
    // ==================================================

    private String formatSlot(
            EquipmentSlot slot
    ) {

        if (slot == null) {
            return "Equipment";
        }

        String raw =
                slot.name()
                        .toLowerCase()
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
    // HOLDER
    // ==================================================

    public static class EquipmentHolder
            implements InventoryHolder {

        private final UUID uuid;

        public EquipmentHolder(
                UUID uuid
        ) {
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}