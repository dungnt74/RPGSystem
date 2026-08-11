package dungnt.rpg.equipment;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentStatManager {

    private final StatManager statManager;

    /*
     * UUID Player
     *    ↓
     * Slot
     *    ↓
     * StatModifier
     */
    private final Map<UUID, Map<String, StatModifier>> equipmentModifiers =
            new HashMap<>();

    public EquipmentStatManager(
            StatManager statManager
    ) {
        this.statManager = statManager;
    }

    // ==================================================
    // ADD EQUIPMENT STAT
    // ==================================================

    public void addEquipmentStat(
            UUID uuid,
            String slot,
            StatType statType,
            ModifierType modifierType,
            double amount
    ) {

        if (uuid == null ||
                slot == null ||
                statType == null ||
                modifierType == null) {

            return;
        }

        String modifierId =
                createModifierId(
                        slot
                );

        StatModifier modifier =
                new StatModifier(
                        modifierId,
                        statType,
                        modifierType,
                        amount
                );

        equipmentModifiers
                .computeIfAbsent(
                        uuid,
                        key -> new HashMap<>()
                )
                .put(
                        slot.toLowerCase(),
                        modifier
                );

        statManager.addModifier(
                uuid,
                modifier
        );
    }

    // ==================================================
    // REMOVE EQUIPMENT
    // ==================================================

    public void removeEquipment(
            UUID uuid,
            String slot
    ) {

        if (uuid == null || slot == null) {
            return;
        }

        String normalizedSlot =
                slot.toLowerCase();

        Map<String, StatModifier> map =
                equipmentModifiers.get(uuid);

        if (map == null) {
            return;
        }

        StatModifier modifier =
                map.remove(normalizedSlot);

        if (modifier != null) {

            statManager.removeModifier(
                    uuid,
                    modifier.getId()
            );
        }

        if (map.isEmpty()) {
            equipmentModifiers.remove(uuid);
        }
    }

    // ==================================================
    // REMOVE ALL EQUIPMENT
    // ==================================================

    public void removeAllEquipment(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        Map<String, StatModifier> map =
                equipmentModifiers.remove(uuid);

        if (map == null) {
            return;
        }

        for (StatModifier modifier :
                map.values()) {

            statManager.removeModifier(
                    uuid,
                    modifier.getId()
            );
        }
    }

    // ==================================================
    // CHECK
    // ==================================================

    public boolean hasEquipment(
            UUID uuid,
            String slot
    ) {

        if (uuid == null || slot == null) {
            return false;
        }

        Map<String, StatModifier> map =
                equipmentModifiers.get(uuid);

        return map != null &&
                map.containsKey(
                        slot.toLowerCase()
                );
    }

    // ==================================================
    // GET EQUIPMENT STAT
    // ==================================================

    public StatModifier getEquipmentStat(
            UUID uuid,
            String slot
    ) {

        if (uuid == null || slot == null) {
            return null;
        }

        Map<String, StatModifier> map =
                equipmentModifiers.get(uuid);

        if (map == null) {
            return null;
        }

        return map.get(
                slot.toLowerCase()
        );
    }

    // ==================================================
    // CLEAR PLAYER
    // ==================================================

    public void clearPlayer(
            UUID uuid
    ) {

        removeAllEquipment(uuid);
    }

    // ==================================================
    // MODIFIER ID
    // ==================================================

    private String createModifierId(
            String slot
    ) {

        return "equipment_" +
                slot.toLowerCase();
    }
}