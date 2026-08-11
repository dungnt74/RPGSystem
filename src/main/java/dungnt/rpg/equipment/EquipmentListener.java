package dungnt.rpg.equipment;

import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class EquipmentListener implements Listener {

    private final JavaPlugin plugin;

    private final StatManager statManager;
    private final EquipmentItemManager itemManager;

    public EquipmentListener(
            JavaPlugin plugin,
            StatManager statManager,
            EquipmentItemManager itemManager
    ) {

        this.plugin = plugin;

        this.statManager =
                statManager;

        this.itemManager =
                itemManager;
    }

    // ==================================================
    // JOIN
    // ==================================================

    @EventHandler
    public void onJoin(
            PlayerJoinEvent event
    ) {

        Player player =
                event.getPlayer();

        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> refreshEquipment(player)
                );
    }

    // ==================================================
    // CLICK
    // ==================================================

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> refreshEquipment(player)
                );
    }

    // ==================================================
    // DRAG
    // ==================================================

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> refreshEquipment(player)
                );
    }

    // ==================================================
    // QUIT
    // ==================================================

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_mainhand"
        );

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_offhand"
        );

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_helmet"
        );

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_chestplate"
        );

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_leggings"
        );

        statManager.removeModifier(
                event.getPlayer()
                        .getUniqueId(),
                "equipment_boots"
        );
    }

    // ==================================================
    // REFRESH
    // ==================================================

    public void refreshEquipment(
            Player player
    ) {

        if (player == null ||
                !player.isOnline()) {

            return;
        }

        UUID uuid =
                player.getUniqueId();

        // ==================================================
        // REMOVE OLD
        // ==================================================

        removeSlot(
                uuid,
                "mainhand"
        );

        removeSlot(
                uuid,
                "offhand"
        );

        removeSlot(
                uuid,
                "helmet"
        );

        removeSlot(
                uuid,
                "chestplate"
        );

        removeSlot(
                uuid,
                "leggings"
        );

        removeSlot(
                uuid,
                "boots"
        );

        // ==================================================
        // MAIN HAND
        // ==================================================

        applyItem(
                uuid,
                "mainhand",
                player.getInventory()
                        .getItemInMainHand()
        );

        // ==================================================
        // OFF HAND
        // ==================================================

        applyItem(
                uuid,
                "offhand",
                player.getInventory()
                        .getItemInOffHand()
        );

        // ==================================================
        // ARMOR
        // ==================================================

        ItemStack[] armor =
                player.getInventory()
                        .getArmorContents();

        if (armor.length >= 4) {

            applyItem(
                    uuid,
                    "boots",
                    armor[0]
            );

            applyItem(
                    uuid,
                    "leggings",
                    armor[1]
            );

            applyItem(
                    uuid,
                    "chestplate",
                    armor[2]
            );

            applyItem(
                    uuid,
                    "helmet",
                    armor[3]
            );
        }
    }

    // ==================================================
    // APPLY ITEM
    // ==================================================

    private void applyItem(
            UUID uuid,
            String slot,
            ItemStack item
    ) {

        if (!itemManager.isRPGItem(item)) {
            return;
        }

        List<StatModifier> modifiers =
                itemManager.getModifiers(item);

        for (StatModifier modifier :
                modifiers) {

            String id =
                    "equipment_" +
                            slot +
                            "_" +
                            modifier.getType()
                                    .name()
                                    .toLowerCase();

            StatModifier slotModifier =
                    new StatModifier(
                            id,
                            modifier.getType(),
                            modifier.getModifierType(),
                            modifier.getAmount()
                    );

            statManager.addModifier(
                    uuid,
                    slotModifier
            );
        }
    }

    // ==================================================
    // REMOVE SLOT
    // ==================================================

    private void removeSlot(
            UUID uuid,
            String slot
    ) {

        for (dungnt.rpg.stats.StatType type :
                dungnt.rpg.stats.StatType.values()) {

            String id =
                    "equipment_" +
                            slot +
                            "_" +
                            type.name()
                                    .toLowerCase();

            statManager.removeModifier(
                    uuid,
                    id
            );
        }
    }
}