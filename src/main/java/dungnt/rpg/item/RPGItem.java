package dungnt.rpg.item;

import dungnt.rpg.stats.StatModifier;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RPGItem {

    // ==================================================
    // BASIC DATA
    // ==================================================

    private final String id;

    private final String name;

    private final Material material;

    private final EquipmentSlot slot;

    // ==================================================
    // STATS
    // ==================================================

    private final List<StatModifier> statModifiers =
            new ArrayList<>();

    // ==================================================
    // CONFIG / SOCKET DATA
    // ==================================================

    private List<String> baseLore = new ArrayList<>();
    private int socketCount;

    private Rarity rarity = Rarity.COMMON;
    private int itemLevel = 1;
    private int upgradeLevel = 0;
    private int maxUpgradeLevel = 0;
    private String model;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public RPGItem(
            String id,
            String name,
            Material material,
            EquipmentSlot slot
    ) {

        this.id = id;
        this.name = name;
        this.material = material;
        this.slot = slot;
    }

    // ==================================================
    // GET ID
    // ==================================================

    public String getId() {

        return id;
    }

    // ==================================================
    // GET NAME
    // ==================================================

    public String getName() {

        return name;
    }

    // ==================================================
    // GET MATERIAL
    // ==================================================

    public Material getMaterial() {

        return material;
    }

    // ==================================================
    // GET SLOT
    // ==================================================

    public EquipmentSlot getSlot() {

        return slot;
    }


    public List<String> getBaseLore() {
        return Collections.unmodifiableList(baseLore);
    }

    public void setBaseLore(List<String> lore) {
        baseLore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity == null ? Rarity.COMMON : rarity;
    }

    public int getItemLevel() {
        return itemLevel;
    }

    public void setItemLevel(int itemLevel) {
        this.itemLevel = Math.max(1, itemLevel);
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(int upgradeLevel) {
        this.upgradeLevel = Math.max(0, Math.min(upgradeLevel, maxUpgradeLevel));
    }

    public int getMaxUpgradeLevel() {
        return maxUpgradeLevel;
    }

    public void setMaxUpgradeLevel(int maxUpgradeLevel) {
        this.maxUpgradeLevel = Math.max(0, maxUpgradeLevel);
        if (upgradeLevel > this.maxUpgradeLevel) {
            upgradeLevel = this.maxUpgradeLevel;
        }
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null || model.isBlank() ? null : model.trim();
    }

    public int getSocketCount() {
        return socketCount;
    }

    public void setSocketCount(int socketCount) {
        this.socketCount = Math.max(0, Math.min(4, socketCount));
    }

    // ==================================================
    // ADD STAT MODIFIER
    // ==================================================

    public void addStatModifier(
            StatModifier modifier
    ) {

        if (modifier == null) {
            return;
        }

        // Modifier cùng ID sẽ replace
        statModifiers.removeIf(
                existing ->
                        existing.getId()
                                .equalsIgnoreCase(
                                        modifier.getId()
                                )
        );

        statModifiers.add(
                modifier
        );
    }

    // ==================================================
    // REMOVE STAT MODIFIER
    // ==================================================

    public void removeStatModifier(
            String modifierId
    ) {

        if (modifierId == null) {
            return;
        }

        statModifiers.removeIf(
                modifier ->
                        modifier.getId()
                                .equalsIgnoreCase(
                                        modifierId
                                )
        );
    }

    // ==================================================
    // GET STAT MODIFIERS
    // ==================================================

    public List<StatModifier> getStatModifiers() {

        return Collections.unmodifiableList(
                statModifiers
        );
    }

    // ==================================================
    // HAS STATS
    // ==================================================

    public boolean hasStatModifiers() {

        return !statModifiers.isEmpty();
    }

    // ==================================================
    // CLEAR STATS
    // ==================================================

    public void clearStatModifiers() {

        statModifiers.clear();
    }
}