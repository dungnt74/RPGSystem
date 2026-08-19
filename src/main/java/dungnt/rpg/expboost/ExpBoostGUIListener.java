package dungnt.rpg.expboost;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class ExpBoostGUIListener implements Listener {

    private final ExpBoostManager manager;
    private final ExpBoostGUI gui;

    public ExpBoostGUIListener(
            ExpBoostManager manager,
            ExpBoostGUI gui
    ) {
        this.manager = manager;
        this.gui = gui;
    }

    private boolean isGUI(InventoryClickEvent event) {
        return event.getView()
                .getTopInventory()
                .getHolder() instanceof ExpBoostGUI.ExpBoostHolder;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!isGUI(event)) return;

        int rawSlot = event.getRawSlot();

        // Không cho lấy item / đặt item vào GUI.
        if (rawSlot < 0 || rawSlot < event.getView().getTopInventory().getSize()) {
            event.setCancelled(true);
        }

        if (rawSlot < 0 || rawSlot >= 54) return;

        int bonus = gui.getBonusFromSlot(rawSlot);
        int durationIndex = gui.getDurationIndexFromSlot(rawSlot);

        if (bonus <= 0 || durationIndex < 0) return;

        if (!manager.isAvailable()) {
            player.sendMessage("§cPlayerPoints chưa được cài hoặc chưa sẵn sàng.");
            return;
        }

        int cost = manager.getCost(bonus, durationIndex);
        int balance = manager.getPoints(player);

        if (balance < cost) {
            player.sendMessage(
                    "§cBạn không đủ Points! §7Cần §e"
                            + cost
                            + " §7nhưng bạn chỉ có §e"
                            + balance
                            + "§7."
            );
            return;
        }

        if (!manager.purchase(player, bonus, durationIndex)) {
            player.sendMessage("§cKhông thể mua EXP Boost. Vui lòng thử lại.");
            return;
        }

        player.sendMessage(
                "§aĐã mua §eEXP +"
                        + bonus
                        + "% §atrong §e"
                        + formatDuration(durationIndex)
                        + "§a!"
        );

        player.sendMessage(
                "§7Points còn lại: §6"
                        + manager.getPoints(player)
        );

        player.closeInventory();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView()
                .getTopInventory()
                .getHolder() instanceof ExpBoostGUI.ExpBoostHolder) {
            event.setCancelled(true);
        }
    }

    private String formatDuration(int index) {
        return switch (index) {
            case 0 -> "1 giờ";
            case 1 -> "3 ngày";
            case 2 -> "7 ngày";
            default -> "28 ngày";
        };
    }
}
