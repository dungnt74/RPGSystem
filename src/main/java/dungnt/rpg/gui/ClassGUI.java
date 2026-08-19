package dungnt.rpg.gui;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClassGUI {

    public static final String TITLE =
            "§8✦ Choose Class";

    private final MyRPG plugin;

    public ClassGUI(MyRPG plugin) {

        this.plugin = plugin;
    }

    // ==================================================
    // OPEN
    // ==================================================

    public void open(
            Player player
    ) {

        Inventory inventory =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                );

        fillBackground(
                inventory
        );

        addClass(
                inventory,
                10,
                "warrior",
                Material.IRON_SWORD
        );

        addClass(
                inventory,
                12,
                "mage",
                Material.BLAZE_ROD
        );

        addClass(
                inventory,
                14,
                "archer",
                Material.BOW
        );

        addClass(
                inventory,
                16,
                "assassin",
                Material.IRON_SWORD
        );

        player.openInventory(
                inventory
        );
    }

    // ==================================================
    // BACKGROUND
    // ==================================================

    private void fillBackground(
            Inventory inventory
    ) {

        ItemStack glass =
                createItem(
                        Material.BLACK_STAINED_GLASS_PANE,
                        "§7"
                );

        for (int i = 0; i < 27; i++) {

            inventory.setItem(
                    i,
                    glass
            );
        }
    }

    // ==================================================
    // CLASS ITEM
    // ==================================================

    private void addClass(
            Inventory inventory,
            int slot,
            String classId,
            Material material
    ) {

        RPGClass rpgClass =
                plugin.getClassManager()
                        .getClass(
                                classId
                        );

        if (rpgClass == null) {
            return;
        }

        ItemStack item =
                new ItemStack(
                        material
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        meta.setDisplayName(
                "§e§l"
                        + rpgClass.getName()
        );

        meta.setLore(
                java.util.List.of(
                        "§7"
                                + rpgClass
                                .getDescription(),
                        "",
                        "§aClick để chọn!"
                )
        );

        item.setItemMeta(
                meta
        );

        inventory.setItem(
                slot,
                item
        );
    }

    // ==================================================
    // CREATE ITEM
    // ==================================================

    private ItemStack createItem(
            Material material,
            String name
    ) {

        ItemStack item =
                new ItemStack(
                        material
                );

        ItemMeta meta =
                item.getItemMeta();

        if (meta != null) {

            meta.setDisplayName(
                    name
            );

            item.setItemMeta(
                    meta
            );
        }

        return item;
    }

    // ==================================================
    // CLASS SLOT
    // ==================================================

    public String getClassId(
            int slot
    ) {

        return switch (slot) {

            case 10 ->
                    "warrior";

            case 12 ->
                    "mage";

            case 14 ->
                    "archer";

            case 16 ->
                    "assassin";

            default ->
                    null;
        };
    }
}