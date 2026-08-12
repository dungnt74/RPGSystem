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