package dungnt.rpg.gui;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.item.RPGItem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class EquipmentGUIListener
        implements Listener {

    private final MyRPG plugin;
    private final EquipmentGUI gui;

    public EquipmentGUIListener(
            MyRPG plugin,
            EquipmentGUI gui
    ) {
        this.plugin = plugin;
        this.gui = gui;
    }

    // ==================================================
    // CHECK GUI
    // ==================================================

    private boolean isEquipmentGUI(
            Inventory inventory
    ) {

        return inventory != null
                && inventory.getHolder()
                instanceof EquipmentGUI.EquipmentHolder;
    }

    // ==================================================
    // CLICK
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        Inventory top =
                event.getView()
                        .getTopInventory();

        if (!isEquipmentGUI(top)) {
            return;
        }

        int rawSlot =
                event.getRawSlot();

        /*
         * Click inventory của player.
         */
        if (rawSlot >= top.getSize()) {

            handlePlayerInventoryClick(
                    event,
                    player,
                    top
            );

            return;
        }

        /*
         * Click ngoài inventory.
         */
        if (rawSlot < 0) {
            return;
        }

        /*
         * Chỉ equipment slot mới được thao tác.
         */
        if (!gui.isEquipmentSlot(rawSlot)) {

            event.setCancelled(true);
            return;
        }

        handleEquipmentSlot(
                event,
                player,
                top,
                rawSlot
        );
    }

    // ==================================================
    // EQUIPMENT SLOT CLICK
    // ==================================================

    private void handleEquipmentSlot(
            InventoryClickEvent event,
            Player player,
            Inventory inventory,
            int guiSlot
    ) {

        event.setCancelled(true);

        EquipmentSlot equipmentSlot =
                gui.getEquipmentSlot(guiSlot);

        if (equipmentSlot == null) {
            return;
        }

        ItemStack current =
                inventory.getItem(guiSlot);

        boolean hasCurrent =
                gui.isRealItem(current);

        ItemStack cursor =
                event.getCursor();

        boolean hasCursor =
                gui.isRealItem(cursor);

        /*
         * EMPTY SLOT + CURSOR ITEM
         */
        if (!hasCurrent) {

            if (!hasCursor) {
                return;
            }

            if (!isCompatible(
                    cursor,
                    equipmentSlot
            )) {

                player.sendMessage(
                        "§cItem này không phù hợp với slot §e"
                                + equipmentSlot.name()
                );

                return;
            }

            ItemStack equipped =
                    cursor.clone();

            /*
             * Equipment slot chỉ chứa 1 item.
             */
            equipped.setAmount(1);

            inventory.setItem(
                    guiSlot,
                    equipped
            );

            ItemStack remaining =
                    cursor.clone();

            if (remaining.getAmount() <= 1) {
                event.setCursor(null);
            } else {
                remaining.setAmount(
                        remaining.getAmount() - 1
                );
                event.setCursor(remaining);
            }

            syncEquipment(
                    player,
                    guiSlot,
                    equipped
            );

            return;
        }

        /*
         * CÓ ITEM + CURSOR RỖNG
         * => lấy item ra.
         */
        if (!hasCursor) {

            event.setCursor(
                    current.clone()
            );

            inventory.setItem(
                    guiSlot,
                    gui.createEmptySlot(
                            equipmentSlot
                    )
            );

            removeEquipment(
                    player,
                    guiSlot,
                    current
            );

            return;
        }

        /*
         * CÓ ITEM + CÓ CURSOR
         *
         * Chỉ swap khi cursor là 1 item.
         */
        if (cursor.getAmount() != 1) {

            player.sendMessage(
                    "§cMuốn đổi trang bị, cursor phải chứa 1 item."
            );

            return;
        }

        if (!isCompatible(
                cursor,
                equipmentSlot
        )) {

            player.sendMessage(
                    "§cItem này không phù hợp với slot §e"
                            + equipmentSlot.name()
            );

            return;
        }

        event.setCursor(
                current.clone()
        );

        inventory.setItem(
                guiSlot,
                cursor.clone()
        );

        removeEquipment(
                player,
                guiSlot,
                current
        );

        syncEquipment(
                player,
                guiSlot,
                cursor
        );
    }

    // ==================================================
    // PLAYER INVENTORY
    // ==================================================

    private void handlePlayerInventoryClick(
            InventoryClickEvent event,
            Player player,
            Inventory top
    ) {

        /*
         * Chỉ xử lý SHIFT CLICK.
         *
         * Click thường vẫn để Bukkit cho player
         * dùng cursor rồi kéo vào GUI.
         */
        if (!event.isShiftClick()) {
            return;
        }

        event.setCancelled(true);

        ItemStack item =
                event.getCurrentItem();

        if (!gui.isRealItem(item)) {
            return;
        }

        int targetSlot =
                findCompatibleSlot(
                        top,
                        item
                );

        if (targetSlot == -1) {

            player.sendMessage(
                    "§cKhông tìm thấy equipment slot phù hợp."
            );

            return;
        }

        /*
         * Lấy đúng 1 item từ stack.
         */
        ItemStack equipped =
                item.clone();

        equipped.setAmount(1);

        top.setItem(
                targetSlot,
                equipped
        );

        if (item.getAmount() <= 1) {

            event.setCurrentItem(null);

        } else {

            ItemStack remaining =
                    item.clone();

            remaining.setAmount(
                    remaining.getAmount() - 1
            );

            event.setCurrentItem(
                    remaining
            );
        }

        syncEquipment(
                player,
                targetSlot,
                equipped
        );
    }

    // ==================================================
    // DRAG
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onDrag(
            InventoryDragEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        Inventory top =
                event.getView()
                        .getTopInventory();

        if (!isEquipmentGUI(top)) {
            return;
        }

        boolean touchesEquipment =
                event.getRawSlots()
                        .stream()
                        .anyMatch(
                                rawSlot ->
                                        rawSlot < top.getSize()
                                                && gui.isEquipmentSlot(
                                                rawSlot
                                        )
                        );

        // Slot 4 là ô Thông tin, không phải ô chứa item.
        // Không cho drag/drop ghi đè Nether Star.
        boolean touchesInfoSlot =
                event.getRawSlots()
                        .contains(4);

        if (touchesInfoSlot) {
            event.setCancelled(true);
            return;
        }

        if (!touchesEquipment) {
            return;
        }

        event.setCancelled(true);

        ItemStack cursor =
                event.getOldCursor();

        if (!gui.isRealItem(cursor)) {
            return;
        }

        /*
         * Chỉ lấy slot equipment đầu tiên phù hợp.
         */
        for (int rawSlot :
                event.getRawSlots()) {

            if (rawSlot >= top.getSize()) {
                continue;
            }

            if (!gui.isEquipmentSlot(rawSlot)) {
                continue;
            }

            EquipmentSlot equipmentSlot =
                    gui.getEquipmentSlot(rawSlot);

            if (!isCompatible(
                    cursor,
                    equipmentSlot
            )) {
                continue;
            }

            if (gui.isRealItem(
                    top.getItem(rawSlot)
            )) {
                continue;
            }

            ItemStack equipped =
                    cursor.clone();

            equipped.setAmount(1);

            top.setItem(
                    rawSlot,
                    equipped
            );

            ItemStack remaining =
                    cursor.clone();

            if (remaining.getAmount() <= 1) {

                player.setItemOnCursor(null);

            } else {

                remaining.setAmount(
                        remaining.getAmount() - 1
                );

                player.setItemOnCursor(
                        remaining
                );
            }

            syncEquipment(
                    player,
                    rawSlot,
                    equipped
            );

            break;
        }
    }

    // ==================================================
    // CLOSE
    // ==================================================

    @EventHandler
    public void onClose(
            InventoryCloseEvent event
    ) {

        if (!(event.getPlayer()
                instanceof Player player)) {

            return;
        }

        Inventory inventory =
                event.getInventory();

        if (!isEquipmentGUI(inventory)) {
            return;
        }

        gui.save(
                player,
                inventory
        );

        gui.save(
                player.getUniqueId()
        );

        /*
         * Sau khi GUI đóng, rebuild runtime:
         * GUI equipment + main hand.
         */
        plugin.getEquipmentListener()
                .scheduleRefreshEquipment(player);
    }

    // ==================================================
    // COMPATIBLE
    // ==================================================

    private boolean isCompatible(
            ItemStack item,
            EquipmentSlot slot
    ) {

        if (item == null || slot == null) {
            return false;
        }

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(item);

        if (rpgItem == null ||
                rpgItem.getSlot() == null) {

            return false;
        }

        return rpgItem.getSlot() == slot;
    }

    // ==================================================
    // FIND COMPATIBLE SLOT
    // ==================================================

    private int findCompatibleSlot(
            Inventory inventory,
            ItemStack item
    ) {

        for (Map.Entry<Integer, EquipmentSlot> entry :
                EquipmentGUI.getGuiSlots().entrySet()) {

            int slot =
                    entry.getKey();

            if (gui.isRealItem(
                    inventory.getItem(slot)
            )) {
                continue;
            }

            if (isCompatible(
                    item,
                    entry.getValue()
            )) {

                return slot;
            }
        }

        return -1;
    }

    // ==================================================
    // SYNC
    // ==================================================

    private void syncEquipment(
            Player player,
            int guiSlot,
            ItemStack item
    ) {

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(item);

        if (rpgItem == null) {
            return;
        }

        EquipmentSlot equipmentSlot =
                gui.getEquipmentSlot(guiSlot);

        if (equipmentSlot != null && equipmentSlot.isVanillaBacked()) {

            /*
             * Helmet / chestplate / leggings / boots / off hand:
             * ghi trực tiếp vào inventory thật của player, để
             * player thực sự MẶC món đồ đó (không chỉ hiển thị ảo
             * trong GUI).
             */
            gui.setVanillaItem(
                    player,
                    equipmentSlot,
                    item
            );

        } else {

            // GUI is the persistent source for virtual accessory slots.
            gui.setItem(
                    player.getUniqueId(),
                    guiSlot,
                    item
            );
        }

        // Rebuild ALL equipment on the next tick. Do not incrementally mutate
        // EquipmentManager here because InventoryClickEvent is still in flight.
        plugin.getEquipmentListener().scheduleRefreshEquipment(player);
    }

    // ==================================================
    // REMOVE
    // ==================================================

    private void removeEquipment(
            Player player,
            int guiSlot,
            ItemStack item
    ) {

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(item);

        EquipmentSlot equipmentSlot =
                gui.getEquipmentSlot(guiSlot);

        if (equipmentSlot != null && equipmentSlot.isVanillaBacked()) {

            /*
             * Tháo giáp/tay phụ khỏi GUI => tháo luôn giáp thật
             * khỏi player. Player sẽ không còn mặc món đồ đó nữa.
             */
            gui.setVanillaItem(
                    player,
                    equipmentSlot,
                    null
            );

        } else {

            gui.removeItem(
                    player.getUniqueId(),
                    guiSlot
            );
        }

        if (rpgItem == null) {
            return;
        }

        plugin.getEquipmentManager()
                .unequip(
                        player.getUniqueId(),
                        rpgItem.getSlot()
                );
    }
}