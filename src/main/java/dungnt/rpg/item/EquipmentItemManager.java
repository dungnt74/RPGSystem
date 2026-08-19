package dungnt.rpg.item;

import dungnt.rpg.stats.StatModifier;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EquipmentItemManager {

    private final ItemManager itemManager;

    public EquipmentItemManager(
            ItemManager itemManager
    ) {

        this.itemManager =
                itemManager;
    }

    // ==================================================
    // GET RPG ITEM
    // ==================================================

    public RPGItem getRPGItem(
            ItemStack item
    ) {

        return itemManager.getRPGItem(
                item
        );
    }

    // ==================================================
    // CHECK
    // ==================================================

    public boolean isRPGItem(
            ItemStack item
    ) {

        return itemManager.isRPGItem(
                item
        );
    }

    // ==================================================
    // GET MODIFIERS
    // ==================================================

    public List<StatModifier> getModifiers(
            ItemStack item
    ) {

        return itemManager.getModifiers(
                item
        );
    }

    // ==================================================
    // GET SLOT
    // ==================================================

    public EquipmentSlot getEquipmentSlot(
            ItemStack item
    ) {

        return itemManager.getEquipmentSlot(
                item
        );
    }
}