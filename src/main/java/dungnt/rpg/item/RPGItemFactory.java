package dungnt.rpg.item;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.Material;

public final class RPGItemFactory {

    private RPGItemFactory() {
        // Utility class
    }

    // ==================================================
    // CREATE
    // ==================================================

    public static RPGItem create(
            String id,
            String name,
            Material material,
            EquipmentSlot slot
    ) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "RPGItem id không được null hoặc rỗng."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "RPGItem name không được null hoặc rỗng."
            );
        }

        if (material == null) {
            throw new IllegalArgumentException(
                    "RPGItem material không được null."
            );
        }

        if (slot == null) {
            throw new IllegalArgumentException(
                    "RPGItem slot không được null."
            );
        }

        return new RPGItem(
                id,
                name,
                material,
                slot
        );
    }

    // ==================================================
    // ADD FLAT STAT
    // ==================================================

    public static RPGItem addFlatStat(
            RPGItem item,
            StatType type,
            double amount
    ) {

        if (item == null || type == null) {
            return item;
        }

        item.addStatModifier(
                new StatModifier(
                        createModifierId(
                                item,
                                type,
                                ModifierType.FLAT
                        ),
                        type,
                        ModifierType.FLAT,
                        amount
                )
        );

        return item;
    }

    // ==================================================
    // ADD PERCENT STAT
    // ==================================================

    public static RPGItem addPercentStat(
            RPGItem item,
            StatType type,
            double amount
    ) {

        if (item == null || type == null) {
            return item;
        }

        item.addStatModifier(
                new StatModifier(
                        createModifierId(
                                item,
                                type,
                                ModifierType.PERCENT
                        ),
                        type,
                        ModifierType.PERCENT,
                        amount
                )
        );

        return item;
    }

    // ==================================================
    // MODIFIER ID
    // ==================================================

    private static String createModifierId(
            RPGItem item,
            StatType type,
            ModifierType modifierType
    ) {

        return "item_"
                + item.getId()
                + "_"
                + type.name().toLowerCase()
                + "_"
                + modifierType.name().toLowerCase();
    }
}