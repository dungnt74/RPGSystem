package dungnt.rpg.item;

import dungnt.rpg.MyRPG;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EquipmentListener implements Listener {

    private final MyRPG plugin;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public EquipmentListener(
            MyRPG plugin
    ) {

        this.plugin = plugin;
    }

    // ==================================================
    // PLAYER JOIN
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
    // INVENTORY CLICK
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
    // INVENTORY DRAG
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
    // PLAYER QUIT
    // ==================================================

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {

        UUID uuid =
                event.getPlayer()
                        .getUniqueId();

        plugin.getEquipmentManager()
                .remove(uuid);
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
        // CLEAR CURRENT RPG EQUIPMENT
        // ==================================================

        plugin.getEquipmentManager()
                .clear(uuid);

        // ==================================================
        // MAIN HAND
        // ==================================================

        syncItem(
                uuid,
                player.getInventory()
                        .getItemInMainHand()
        );

        // ==================================================
        // OFF HAND
        // ==================================================

        syncItem(
                uuid,
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

            // Bukkit armor order:
            // 0 = boots
            // 1 = leggings
            // 2 = chestplate
            // 3 = helmet

            syncItem(
                    uuid,
                    armor[0]
            );

            syncItem(
                    uuid,
                    armor[1]
            );

            syncItem(
                    uuid,
                    armor[2]
            );

            syncItem(
                    uuid,
                    armor[3]
            );
        }

        // ==================================================
        // FUTURE RPG SLOTS
        // ==================================================
        //
        // Đai lưng
        // Găng tay
        // Ngọc bội
        // Nhẫn
        // Khuyên tai
        // Vòng cổ
        // Cánh
        // Huy hiệu
        // Thú cưng
        // Thú cưỡi
        //
        // Các slot này KHÔNG nằm trong
        // Bukkit PlayerInventory mặc định.
        //
        // Khi có RPG Inventory riêng,
        // listener sẽ sync chúng ở đây.
        //
        // ==================================================
    }

    // ==================================================
    // SYNC ITEM
    // ==================================================

    private void syncItem(
            UUID uuid,
            ItemStack itemStack
    ) {

        if (uuid == null) {
            return;
        }

        if (itemStack == null ||
                itemStack.getType().isAir()) {

            return;
        }

        // ==================================================
        // ITEMSTACK -> RPG ITEM
        // ==================================================

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(
                                itemStack
                        );

        if (rpgItem == null) {
            return;
        }

        // ==================================================
        // EQUIP
        // ==================================================

        plugin.getEquipmentManager()
                .equip(
                        uuid,
                        rpgItem
                );
    }
}