package dungnt.rpg.equipment;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EquipmentItemManager {

    private final JavaPlugin plugin;

    public EquipmentItemManager(
            JavaPlugin plugin
    ) {

        this.plugin = plugin;
    }

    // ==================================================
    // READ ITEM STATS
    // ==================================================

    public List<StatModifier> getModifiers(
            ItemStack item
    ) {

        List<StatModifier> modifiers =
                new ArrayList<>();

        if (!RPGItem.isRPGItem(
                plugin,
                item
        )) {

            return modifiers;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return modifiers;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String itemId =
                RPGItem.getItemId(
                        plugin,
                        item
                );

        if (itemId == null) {
            return modifiers;
        }

        for (StatType statType :
                StatType.values()) {

            NamespacedKey key =
                    new NamespacedKey(
                            plugin,
                            "rpg_stat_" +
                                    statType.name()
                                            .toLowerCase()
                    );

            String value =
                    pdc.get(
                            key,
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

                modifiers.add(
                        modifier
                );
            }
        }

        return modifiers;
    }

    // ==================================================
    // PARSE
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
                "equipment_" +
                        itemId +
                        "_" +
                        statType.name()
                                .toLowerCase();

        return new StatModifier(
                modifierId,
                statType,
                modifierType,
                amount
        );
    }

    // ==================================================
    // CHECK
    // ==================================================

    public boolean isRPGItem(
            ItemStack item
    ) {

        return RPGItem.isRPGItem(
                plugin,
                item
        );
    }
}