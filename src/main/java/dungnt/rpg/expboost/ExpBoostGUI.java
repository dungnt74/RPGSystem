package dungnt.rpg.expboost;

import dungnt.rpg.MyRPG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExpBoostGUI {

    public static final String TITLE = "§8§lEXP BOOST";

    private final MyRPG plugin;
    private final ExpBoostManager manager;

    public ExpBoostGUI(MyRPG plugin, ExpBoostManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(org.bukkit.entity.Player player) {
        ExpBoostHolder holder = new ExpBoostHolder();

        Inventory inventory = Bukkit.createInventory(
                holder,
                54,
                TITLE
        );

        holder.setInventory(inventory);

        ItemStack glass = createGlass();
        for (int slot = 0; slot < 54; slot++) {
            inventory.setItem(slot, glass.clone());
        }

        int[] bonusColumns = {1, 3, 5, 7};
        int[] durationRows = {1, 2, 3, 4};

        for (int columnIndex = 0; columnIndex < bonusColumns.length; columnIndex++) {
            int bonus = ExpBoostManager.BONUSES[columnIndex];
            int column = bonusColumns[columnIndex];

            for (int rowIndex = 0; rowIndex < durationRows.length; rowIndex++) {
                int row = durationRows[rowIndex];
                int slot = row * 9 + column;

                inventory.setItem(
                        slot,
                        createBoostItem(
                                player,
                                bonus,
                                rowIndex
                        )
                );
            }
        }

        player.openInventory(inventory);
    }

    private ItemStack createBoostItem(
            org.bukkit.entity.Player player,
            int bonusPercent,
            int durationIndex
    ) {
        ItemStack item = new ItemStack(Material.POTION);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(
                    "§a§lEXP +" + bonusPercent + "%"
            );

            List<String> lore = new ArrayList<>();
            lore.add("§8§m--------------------");
            lore.add("§7Thời gian: §e" + formatDuration(durationIndex));
            lore.add("§7Giá: §6" + manager.getCost(bonusPercent, durationIndex) + " Points");
            lore.add("");

            long expiry = manager.getExpiry(player, bonusPercent);

            if (expiry > System.currentTimeMillis()) {
                lore.add("§aĐang hoạt động");
                lore.add("§7Hết hạn: §f" + formatDate(expiry));
                lore.add("");
                lore.add("§7Mua lại sẽ §eCỘNG THÊM §7thời gian.");
            } else {
                lore.add("§eNhấn để mua");
            }

            lore.add("§8§m--------------------");
            meta.setLore(lore);

            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createGlass() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }

        return item;
    }

    public int getBonusFromSlot(int slot) {
        int row = slot / 9;
        int column = slot % 9;

        if (row < 1 || row > 4) return -1;

        return switch (column) {
            case 1 -> 50;
            case 3 -> 100;
            case 5 -> 200;
            case 7 -> 300;
            default -> -1;
        };
    }

    public int getDurationIndexFromSlot(int slot) {
        int row = slot / 9;
        if (row < 1 || row > 4) return -1;
        return row - 1;
    }

    private String formatDuration(int index) {
        return switch (index) {
            case 0 -> "1 giờ";
            case 1 -> "3 ngày";
            case 2 -> "7 ngày";
            default -> "28 ngày";
        };
    }

    private String formatDate(long millis) {
        return new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.ROOT
        ).format(new Date(millis));
    }

    public static class ExpBoostHolder implements InventoryHolder {
        private Inventory inventory;

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        public void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }
    }
}
