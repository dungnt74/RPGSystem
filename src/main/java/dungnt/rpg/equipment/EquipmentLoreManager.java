package dungnt.rpg.equipment;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class EquipmentLoreManager {

    private final EquipmentItemManager itemManager;

    public EquipmentLoreManager(
            EquipmentItemManager itemManager
    ) {

        this.itemManager =
                itemManager;
    }

    // ==================================================
    // UPDATE LORE
    // ==================================================

    public void updateLore(
            ItemStack item
    ) {

        if (!itemManager.isRPGItem(item)) {
            return;
        }

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        List<String> lore =
                new ArrayList<>();

        lore.add(
                "§8§m--------------------"
        );

        for (StatModifier modifier :
                itemManager.getModifiers(item)) {

            lore.add(
                    formatModifier(
                            modifier
                    )
            );
        }

        lore.add(
                "§8§m--------------------"
        );

        meta.setLore(lore);

        item.setItemMeta(meta);
    }

    // ==================================================
    // FORMAT
    // ==================================================

    private String formatModifier(
            StatModifier modifier
    ) {

        String name =
                getStatName(
                        modifier.getType()
                );

        double amount =
                modifier.getAmount();

        String value;

        if (modifier.getModifierType()
                == ModifierType.PERCENT) {

            value =
                    formatNumber(amount)
                            + "%";

        } else {

            value =
                    formatNumber(amount);
        }

        return "§7" +
                name +
                ": §a+" +
                value;
    }

    // ==================================================
    // STAT NAME
    // ==================================================

    private String getStatName(
            StatType type
    ) {

        return switch (type) {

            case ATTACK ->
                    "Attack";

            case MAGIC_ATTACK ->
                    "Magic Attack";

            case ATTACK_SPEED ->
                    "Attack Speed";

            case CRIT_CHANCE ->
                    "Crit Chance";

            case CRIT_DAMAGE ->
                    "Crit Damage";

            case CRIT_RESISTANCE ->
                    "Crit Resistance";

            case ARMOR_PENETRATION ->
                    "Armor Penetration";

            case MAGIC_PENETRATION ->
                    "Magic Penetration";

            case SKILL_DAMAGE ->
                    "Skill Damage";

            case DEFENSE ->
                    "Defense";

            case MAGIC_DEFENSE ->
                    "Magic Defense";

            case DAMAGE_REDUCTION ->
                    "Damage Reduction";

            case BLOCK_CHANCE ->
                    "Block Chance";

            case BLOCK_POWER ->
                    "Block Power";

            case DODGE_CHANCE ->
                    "Dodge Chance";

            case MAX_HEALTH ->
                    "Max Health";

            case MAX_MANA ->
                    "Max Mana";

            case HEALTH_REGEN ->
                    "Health Regen";

            case MANA_REGEN ->
                    "Mana Regen";

            case LIFESTEAL ->
                    "Lifesteal";

            case MANA_STEAL ->
                    "Mana Steal";

            case COOLDOWN_REDUCTION ->
                    "Cooldown Reduction";

            case MOVE_SPEED ->
                    "Move Speed";

            case EXP_BONUS ->
                    "EXP Bonus";

            case GOLD_BONUS ->
                    "Gold Bonus";

            case DROP_RATE ->
                    "Drop Rate";

            case LUCK ->
                    "Luck";
        };
    }

    // ==================================================
    // NUMBER
    // ==================================================

    private String formatNumber(
            double value
    ) {

        if (value == Math.floor(value)) {

            return String.format(
                    "%.0f",
                    value
            );
        }

        return String.format(
                "%.1f",
                value
        );
    }
}