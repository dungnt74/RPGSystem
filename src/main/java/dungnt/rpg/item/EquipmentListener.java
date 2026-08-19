package dungnt.rpg.item;

import dungnt.rpg.MyRPG;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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

                            // Load virtual RPG equipment (rings, belt, wings, etc.)
                            // before rebuilding EquipmentManager.
                            if (plugin.getEquipmentGUI() != null) {
                                plugin.getEquipmentGUI().load(player);
                            }

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
    // OFF HAND SWAP (F key)
    // ==================================================

    @EventHandler
    public void onSwapHands(
            PlayerSwapHandItemsEvent event
    ) {

        scheduleRefresh(
                event.getPlayer()
        );
    }

    // ==================================================
    // VANILLA ARMOR EQUIP / UNEQUIP
    // ==================================================
    //
    // Mặc/tháo giáp bằng tay (right-click, shift-click,
    // dispenser, chết drop item lại,...) đều đi qua đây.
    // Việc này khiến /equipment luôn phản ánh đúng giáp
    // đang mặc và ngược lại.
    // ==================================================

    @EventHandler
    public void onArmorChange(
            PlayerArmorChangeEvent event
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

        // Persist virtual RPG equipment before clearing runtime data.
        if (plugin.getEquipmentGUI() != null) {
            plugin.getEquipmentGUI().save(uuid);
        }

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

        if (player == null || !player.isOnline()) {
            return;
        }

        UUID uuid = player.getUniqueId();

        // Always ensure the player data/base stats exist first.
        plugin.getPlayerManager().getData(player);

        // Rebuild the runtime equipment map from the actual sources:
        // 1) vanilla-backed slots (main hand, off hand, helmet, chestplate,
        //    leggings, boots) — read straight from the player's real inventory
        // 2) virtual Equipment GUI slots (rings, wings, belt, ...)
        // This makes the EquipmentManager the single runtime source of truth.
        plugin.getEquipmentManager().clear(uuid);

        // Items whose stats actually ended up applied this refresh. Used below
        // to keep socket/gem modifiers in sync with exactly the same set.
        java.util.List<ItemStack> activeItems = new java.util.ArrayList<>();

        syncSlotItem(uuid, player.getInventory().getItemInMainHand(), EquipmentSlot.MAIN_HAND, activeItems);
        syncSlotItem(uuid, player.getInventory().getItemInOffHand(), EquipmentSlot.OFF_HAND, activeItems);
        syncSlotItem(uuid, player.getInventory().getHelmet(), EquipmentSlot.HELMET, activeItems);
        syncSlotItem(uuid, player.getInventory().getChestplate(), EquipmentSlot.CHESTPLATE, activeItems);
        syncSlotItem(uuid, player.getInventory().getLeggings(), EquipmentSlot.LEGGINGS, activeItems);
        syncSlotItem(uuid, player.getInventory().getBoots(), EquipmentSlot.BOOTS, activeItems);

        if (plugin.getEquipmentGUI() != null) {
            for (Map.Entry<Integer, ItemStack> entry :
                    plugin.getEquipmentGUI().getStoredItems(uuid).entrySet()) {

                dungnt.rpg.item.EquipmentSlot expectedSlot =
                        dungnt.rpg.gui.EquipmentGUI.getGuiSlots().get(entry.getKey());

                syncSlotItem(uuid, entry.getValue(), expectedSlot, activeItems);
            }
        }

        // Rebuild socket modifiers too, so removed/replaced items cannot leave stale gems.
        if (plugin.getGemManager() != null) {
            java.util.Map<String, dungnt.rpg.stats.StatModifier> current =
                    plugin.getStatManager().getModifiers(uuid);

            for (String id : new java.util.ArrayList<>(current.keySet())) {
                if (id.startsWith("socket_")) {
                    plugin.getStatManager().removeModifier(uuid, id);
                }
            }

            for (ItemStack item : activeItems) {
                plugin.getGemManager().applySocketStats(uuid, item);
            }
        }

        // Equipment can change MAX_HEALTH / MAX_MANA. Keep the live resource
        // values and the Bukkit max-health attribute synchronized immediately.
        plugin.getPlayerManager().refreshResources(player);
    }

    /**
     * Rebuild equipment one tick later. This is important for InventoryClickEvent:
     * Bukkit may still be finalizing the inventory transaction when the event fires.
     */
    public void scheduleRefreshEquipment(Player player) {
        if (player == null) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> refreshEquipment(player));
    }

    // ==================================================
    // SYNC SLOT ITEM
    // ==================================================
    //
    // Chỉ cộng stat khi item ĐANG NẰM ĐÚNG SLOT của nó.
    //
    // Trước đây, main hand được sync bằng slot do item tự khai
    // báo (rpgItem.getSlot()), nên một item được gắn slot RING1
    // vẫn cộng stat khi chỉ đơn giản là cầm ở tay chính. Bây giờ
    // ta yêu cầu rpgItem.getSlot() phải khớp CHÍNH XÁC với slot
    // vật lý (hoặc GUI) mà item đang nằm trong.
    // ==================================================

    private void syncSlotItem(
            UUID uuid,
            ItemStack itemStack,
            EquipmentSlot expectedSlot,
            java.util.List<ItemStack> activeItems
    ) {

        if (uuid == null ||
                expectedSlot == null ||
                itemStack == null ||
                itemStack.getType().isAir()) {

            return;
        }

        RPGItem rpgItem =
                plugin.getItemManager()
                        .fromItemStack(itemStack);

        if (rpgItem == null ||
                rpgItem.getSlot() != expectedSlot) {

            return;
        }

        plugin.getEquipmentManager()
                .equip(
                        uuid,
                        rpgItem
                );

        if (activeItems != null) {
            activeItems.add(itemStack);
        }
    }
}