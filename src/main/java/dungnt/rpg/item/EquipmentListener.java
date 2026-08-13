package dungnt.rpg.item;

import dungnt.rpg.MyRPG;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class EquipmentListener
        implements Listener {

    private final MyRPG plugin;

    public EquipmentListener(
            MyRPG plugin
    ) {
        this.plugin = plugin;
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
                        () -> {

                            /*
                             * Quan trọng:
                             *
                             * PlayerStats mặc định Attack = 10.
                             * Nếu không gọi getData() trước combat,
                             * StatManager chưa có base stat.
                             */
                            plugin.getPlayerManager()
                                    .getData(player);

                            refreshEquipment(player);
                        }
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

        /*
         * Equipment GUI tự quản lý runtime.
         * Không để listener này clear GUI equipment.
         */
        if (event.getView()
                .getTopInventory()
                .getHolder()
                instanceof dungnt.rpg.gui.EquipmentGUI.EquipmentHolder) {

            return;
        }

        scheduleRefresh(player);
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

        if (event.getView()
                .getTopInventory()
                .getHolder()
                instanceof dungnt.rpg.gui.EquipmentGUI.EquipmentHolder) {

            return;
        }

        scheduleRefresh(player);
    }

    // ==================================================
    // HELD ITEM
    // ==================================================

    @EventHandler
    public void onItemHeld(
            PlayerItemHeldEvent event
    ) {

        scheduleRefresh(
                event.getPlayer()
        );
    }

    // ==================================================
    // CLOSE
    // ==================================================

    @EventHandler
    public void onInventoryClose(
            InventoryCloseEvent event
    ) {

        if (!(event.getPlayer()
                instanceof Player player)) {

            return;
        }

        /*
         * Không cần refresh ở đây cho GUI.
         * EquipmentGUIListener đã xử lý.
         */
        if (event.getInventory()
                .getHolder()
                instanceof dungnt.rpg.gui.EquipmentGUI.EquipmentHolder) {

            return;
        }
    }

    // ==================================================
    // QUIT
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
    // SCHEDULE REFRESH
    // ==================================================

    private void scheduleRefresh(
            Player player
    ) {

        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> refreshEquipment(player)
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

        /*
         * Đảm bảo BASE STATS luôn tồn tại.
         *
         * Đây là fix lỗi:
         * "chưa chọn class thì đánh không gây damage".
         */
        plugin.getPlayerManager()
                .getData(player);

        // ==================================================
        // CLEAR RUNTIME
        // ==================================================

        plugin.getEquipmentManager()
                .clear(uuid);

        // ==================================================
        // MAIN HAND
        // ==================================================
        //
        // GUI không có MAIN_HAND.
        // Tuy nhiên vũ khí cầm tay chính vẫn cộng damage.
        //
        // Item phải có equipment slot MAIN_HAND.
        // ==================================================

        syncItem(
                uuid,
                player.getInventory()
                        .getItemInMainHand()
        );

        // ==================================================
        // VIRTUAL EQUIPMENT GUI
        // ==================================================

        if (plugin.getEquipmentGUI() != null) {

            Map<Integer, ItemStack> stored =
                    plugin.getEquipmentGUI()
                            .getStoredItems(uuid);

            for (ItemStack item :
                    stored.values()) {

                syncItem(
                        uuid,
                        item
                );
            }
        }

        // Socket stats are separate from RPGItem stats.
        // Rebuild them after every equipment refresh so removing
        // an item cannot leave stale gem modifiers behind.
        if (plugin.getGemManager() != null) {
            java.util.Map<String, dungnt.rpg.stats.StatModifier> current =
                    plugin.getStatManager().getModifiers(uuid);

            for (String id : new java.util.ArrayList<>(current.keySet())) {
                if (id.startsWith("socket_")) {
                    plugin.getStatManager().removeModifier(uuid, id);
                }
            }

            ItemStack mainHand =
                    player.getInventory().getItemInMainHand();

            if (mainHand != null &&
                    !mainHand.getType().isAir()) {
                plugin.getGemManager()
                        .applySocketStats(uuid, mainHand);
            }

            if (plugin.getEquipmentGUI() != null) {
                for (ItemStack item :
                        plugin.getEquipmentGUI()
                                .getStoredItems(uuid)
                                .values()) {
                    plugin.getGemManager()
                            .applySocketStats(uuid, item);
                }
            }
        }
    }

    // ==================================================
    // SYNC ITEM
    // ==================================================

    private void syncItem(
            UUID uuid,
            ItemStack itemStack
    ) {

        if (uuid == null ||
                itemStack == null ||
                itemStack.getType().isAir()) {

            return;
        }

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(itemStack);

        if (rpgItem == null ||
                rpgItem.getSlot() == null) {

            return;
        }

        plugin.getEquipmentManager()
                .equip(
                        uuid,
                        rpgItem
                );
    }
}
