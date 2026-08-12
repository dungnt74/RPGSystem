package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.RPGItem;
import dungnt.rpg.item.SampleItems;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    public ItemTestCommand(
            MyRPG plugin
    ) {

        this.plugin = plugin;
    }

    // ==================================================
    // COMMAND
    // ==================================================

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        // ==================================================
        // PLAYER ONLY
        // ==================================================

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "§cLệnh này chỉ dành cho Player."
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        if (args.length == 0) {

            sendHelp(player);

            return true;
        }

        String itemId =
                args[0].toLowerCase();

        // ==================================================
        // FIND SAMPLE ITEM
        // ==================================================

        RPGItem rpgItem =
                findSampleItem(itemId);

        if (rpgItem == null) {

            player.sendMessage(
                    "§cKhông tìm thấy item test: §e"
                            + itemId
            );

            sendHelp(player);

            return true;
        }

        // ==================================================
        // RPG ITEM -> ITEM STACK
        // ==================================================

        ItemStack item =
                plugin.getItemManager()
                        .toItemStack(
                                rpgItem
                        );

        if (item == null) {

            player.sendMessage(
                    "§cKhông thể tạo ItemStack."
            );

            return true;
        }

        // ==================================================
        // GIVE
        // ==================================================

        player.getInventory()
                .addItem(item);

        // ==================================================
        // MESSAGE
        // ==================================================

        player.sendMessage(
                "§a§l✔ ITEM CREATED"
        );

        player.sendMessage(
                "§7ID: §e"
                        + rpgItem.getId()
        );

        player.sendMessage(
                "§7Name: §f"
                        + rpgItem.getName()
        );

        player.sendMessage(
                "§7Slot: §e"
                        + rpgItem.getSlot().name()
        );

        player.sendMessage(
                "§7Stats: §a"
                        + rpgItem.getStatModifiers().size()
        );

        return true;
    }

    // ==================================================
    // FIND SAMPLE
    // ==================================================

    private RPGItem findSampleItem(
            String id
    ) {

        for (RPGItem item :
                SampleItems.getAll()) {

            if (item.getId()
                    .equalsIgnoreCase(id)) {

                return item;
            }
        }

        return null;
    }

    // ==================================================
    // HELP
    // ==================================================

    private void sendHelp(
            Player player
    ) {

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§l✦ ITEM TEST"
        );

        player.sendMessage(
                "§7/itemtest warrior_sword"
        );

        player.sendMessage(
                "§7/itemtest mage_staff"
        );

        player.sendMessage(
                "§7/itemtest archer_bow"
        );

        player.sendMessage(
                "§7/itemtest assassin_dagger"
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}