package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.RPGItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemTestCommand implements CommandExecutor {
    private final MyRPG plugin;

    public ItemTestCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cLệnh này chỉ dành cho Player.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§e/itemtest <item_id>");
            return true;
        }

        RPGItem rpgItem = plugin.getRPGItemManager().get(args[0]);
        if (rpgItem == null) {
            player.sendMessage("§cKhông tìm thấy item trong items.yml: §e" + args[0]);
            return true;
        }

        ItemStack item = plugin.getItemManager().toItemStack(rpgItem);
        if (item == null) {
            player.sendMessage("§cKhông thể tạo ItemStack.");
            return true;
        }

        player.getInventory().addItem(item);
        player.sendMessage("§a§l✔ ITEM CREATED");
        player.sendMessage("§7ID: §e" + rpgItem.getId());
        player.sendMessage("§7Stats: §a" + rpgItem.getStatModifiers().size());
        player.sendMessage("§7Sockets: §e" + rpgItem.getSocketCount());
        return true;
    }
}
