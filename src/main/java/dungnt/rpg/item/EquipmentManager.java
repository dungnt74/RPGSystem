package dungnt.rpg.item;

import dungnt.rpg.stats.StatManager;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentManager {

    private final StatManager statManager;

    private final Map<UUID, Map<EquipmentSlot, RPGItem>> equipment =
            new java.util.HashMap<>();

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public EquipmentManager(
            StatManager statManager
    ) {

        this.statManager = statManager;
    }

    // ==================================================
    // EQUIP
    // ==================================================

    public boolean equip(
            UUID uuid,
            RPGItem item
    ) {

        if (uuid == null || item == null) {
            return false;
        }

        EquipmentSlot slot =
                item.getSlot();

        if (slot == null) {
            return false;
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.computeIfAbsent(
                        uuid,
                        key -> new EnumMap<>(
                                EquipmentSlot.class
                        )
                );

        // ==================================================
        // REMOVE OLD ITEM
        // ==================================================

        RPGItem oldItem =
                playerEquipment.get(slot);

        if (oldItem != null) {

            statManager.removeItem(
                    uuid,
                    oldItem
            );
        }

        // ==================================================
        // EQUIP NEW ITEM
        // ==================================================

        playerEquipment.put(
                slot,
                item
        );

        // ==================================================
        // APPLY NEW ITEM STATS
        // ==================================================

        statManager.applyItem(
                uuid,
                item
        );

        return true;
    }

    // ==================================================
    // UNEQUIP
    // ==================================================

    public RPGItem unequip(
            UUID uuid,
            EquipmentSlot slot
    ) {

        if (uuid == null || slot == null) {
            return null;
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.get(uuid);

        if (playerEquipment == null) {
            return null;
        }

        RPGItem item =
                playerEquipment.remove(slot);

        if (item == null) {
            return null;
        }

        // ==================================================
        // REMOVE ITEM STATS
        // ==================================================

        statManager.removeItem(
                uuid,
                item
        );

        // ==================================================
        // CLEAN EMPTY PLAYER
        // ==================================================

        if (playerEquipment.isEmpty()) {

            equipment.remove(uuid);
        }

        return item;
    }

    // ==================================================
    // GET ITEM
    // ==================================================

    public RPGItem getItem(
            UUID uuid,
            EquipmentSlot slot
    ) {

        if (uuid == null || slot == null) {
            return null;
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.get(uuid);

        if (playerEquipment == null) {
            return null;
        }

        return playerEquipment.get(slot);
    }

    // ==================================================
    // CHECK EQUIPPED
    // ==================================================

    public boolean isEquipped(
            UUID uuid,
            EquipmentSlot slot
    ) {

        return getItem(
                uuid,
                slot
        ) != null;
    }

    // ==================================================
    // GET ALL EQUIPMENT
    // ==================================================

    public Map<EquipmentSlot, RPGItem> getEquipment(
            UUID uuid
    ) {

        if (uuid == null) {
            return Collections.emptyMap();
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.get(uuid);

        if (playerEquipment == null) {
            return Collections.emptyMap();
        }

        return Collections.unmodifiableMap(
                playerEquipment
        );
    }

    // ==================================================
    // CLEAR
    // ==================================================

    public void clear(
            UUID uuid
    ) {

        if (uuid == null) {
            return;
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.remove(uuid);

        if (playerEquipment == null) {
            return;
        }

        // ==================================================
        // REMOVE ALL ITEM STATS
        // ==================================================

        for (RPGItem item :
                playerEquipment.values()) {

            if (item == null) {
                continue;
            }

            statManager.removeItem(
                    uuid,
                    item
            );
        }
    }

    // ==================================================
    // REMOVE PLAYER
    // ==================================================

    public void remove(
            UUID uuid
    ) {

        clear(uuid);
    }

    // ==================================================
    // REFRESH ITEM
    // ==================================================

    public void refresh(
            UUID uuid,
            RPGItem item
    ) {

        if (uuid == null || item == null) {
            return;
        }

        EquipmentSlot slot =
                item.getSlot();

        if (slot == null) {
            return;
        }

        Map<EquipmentSlot, RPGItem> playerEquipment =
                equipment.get(uuid);

        if (playerEquipment == null) {
            return;
        }

        RPGItem current =
                playerEquipment.get(slot);

        if (current == null) {
            return;
        }

        if (!current.getId()
                .equalsIgnoreCase(
                        item.getId()
                )) {

            return;
        }

        statManager.removeItem(
                uuid,
                current
        );

        playerEquipment.put(
                slot,
                item
        );

        statManager.applyItem(
                uuid,
                item
        );
    }
}