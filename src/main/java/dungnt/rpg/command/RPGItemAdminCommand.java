package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.item.Rarity;
import dungnt.rpg.item.RPGItem;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class RPGItemAdminCommand {

    private final MyRPG plugin;

    public RPGItemAdminCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    public boolean handle(CommandSender sender, String[] args) {
        if (!sender.hasPermission("dungntrpg.admin")) {
            sender.sendMessage("§cBạn không có quyền dùng lệnh này.");
            return true;
        }
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "list" -> { list(sender); yield true; }
            case "give" -> { give(sender, args); yield true; }
            case "create" -> { create(sender, args); yield true; }
            case "delete", "remove" -> { delete(sender, args); yield true; }
            case "stat" -> { handleStat(sender, args); yield true; }
            case "rename" -> { handleRename(sender, args); yield true; }
            case "lore" -> { handleLore(sender, args); yield true; }
            case "slot" -> { handleSlot(sender, args); yield true; }
            default -> { sendUsage(sender); yield true; }
        };
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§8§m--------------------------------");
        sender.sendMessage("§6§l/rpg item");
        sender.sendMessage("§e/rpg item stat add <stat> <amount> [flat|percent]");
        sender.sendMessage("§e/rpg item stat remove <stat>");
        sender.sendMessage("§e/rpg item stat clear");
        sender.sendMessage("§e/rpg item rename <name>");
        sender.sendMessage("§e/rpg item lore add <text>");
        sender.sendMessage("§e/rpg item lore remove <text>");
        sender.sendMessage("§e/rpg item slot add <slot>");
        sender.sendMessage("§e/rpg item slot remove");
        sender.sendMessage("§7Admin registry:");
        sender.sendMessage("§e/rpg item list");
        sender.sendMessage("§e/rpg item give <id> [player] [amount]");
        sender.sendMessage("§e/rpg item create <id> <material> <slot> [rarity]");
        sender.sendMessage("§e/rpg item delete <id>");
        sender.sendMessage("§8§m--------------------------------");
    }

    private Player requirePlayer(CommandSender sender) {
        if (sender instanceof Player p) return p;
        sender.sendMessage("§cLệnh này chỉ dùng được trong game.");
        return null;
    }

    private ItemStack held(CommandSender sender) {
        Player p = requirePlayer(sender);
        return p == null ? null : p.getInventory().getItemInMainHand();
    }

    private void handleStat(CommandSender sender, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            p.sendMessage("§cHãy cầm item cần chỉnh.");
            return;
        }
        if (args.length < 2) {
            p.sendMessage("§e/rpg item stat <add|remove|clear> ...");
            return;
        }

        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "add" -> {
                if (args.length < 4) {
                    p.sendMessage("§e/rpg item stat add <stat> <amount> [flat|percent]");
                    return;
                }
                StatType stat = parseStat(args[2]);
                if (stat == null) {
                    p.sendMessage("§cStat không hợp lệ: §e" + args[2]);
                    return;
                }
                double amount;
                try {
                    amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException ex) {
                    p.sendMessage("§cAmount phải là số.");
                    return;
                }
                ModifierType type = ModifierType.FLAT;
                if (args.length >= 5) {
                    try {
                        type = ModifierType.valueOf(args[4].toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException ex) {
                        p.sendMessage("§cModifier phải là flat hoặc percent.");
                        return;
                    }
                }
                if (plugin.getItemManager().addStat(item, stat, type, amount)) {
                    p.getInventory().setItemInMainHand(item);
                    p.sendMessage("§aĐã thêm §e" + stat + " §a= §e" + amount + " " + type);
                } else {
                    p.sendMessage("§cKhông thể thêm stat.");
                }
            }
            case "remove" -> {
                if (args.length < 3) {
                    p.sendMessage("§e/rpg item stat remove <stat>");
                    return;
                }
                StatType stat = parseStat(args[2]);
                if (stat == null) {
                    p.sendMessage("§cStat không hợp lệ: §e" + args[2]);
                    return;
                }
                boolean removed = plugin.getItemManager().removeStat(item, stat);
                p.getInventory().setItemInMainHand(item);
                p.sendMessage(removed ? "§aĐã xoá stat §e" + stat : "§eItem không có stat §f" + stat);
            }
            case "clear" -> {
                plugin.getItemManager().clearStats(item);
                p.getInventory().setItemInMainHand(item);
                p.sendMessage("§aĐã xoá toàn bộ stat.");
            }
            default -> p.sendMessage("§e/rpg item stat <add|remove|clear> ...");
        }
    }

    private void handleRename(CommandSender sender, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) { p.sendMessage("§cHãy cầm item."); return; }
        if (args.length < 2) { p.sendMessage("§e/rpg item rename <name>"); return; }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.setDisplayName(color(join(args, 1)));
        item.setItemMeta(meta);
        p.sendMessage("§aĐã đổi tên item.");
    }

    private void handleLore(CommandSender sender, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) { p.sendMessage("§cHãy cầm item."); return; }
        if (args.length < 3) {
            p.sendMessage("§e/rpg item lore <add|remove> <text>");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
        String text = color(join(args, 2));

        if (args[1].equalsIgnoreCase("add")) {
            lore.add(text);
            meta.setLore(lore);
            item.setItemMeta(meta);
            p.sendMessage("§aĐã thêm lore.");
        } else if (args[1].equalsIgnoreCase("remove")) {
            boolean removed = lore.removeIf(line -> ChatColor.stripColor(line).equalsIgnoreCase(ChatColor.stripColor(text)));
            meta.setLore(lore);
            item.setItemMeta(meta);
            p.sendMessage(removed ? "§aĐã xoá lore." : "§eKhông tìm thấy lore.");
        } else {
            p.sendMessage("§e/rpg item lore <add|remove> <text>");
        }
    }

    private void handleSlot(CommandSender sender, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) { p.sendMessage("§cHãy cầm item."); return; }

        if (args.length < 2) {
            p.sendMessage("§e/rpg item slot <add|remove> [slot]");
            return;
        }

        if (args[1].equalsIgnoreCase("remove")) {
            p.getInventory().setItemInMainHand(item);
            p.sendMessage(plugin.getItemManager().removeEquipmentSlot(item)
                    ? "§aĐã xoá Equipment Slot."
                    : "§eItem không có Equipment Slot.");
            return;
        }

        if (args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) { p.sendMessage("§e/rpg item slot add <slot>"); return; }
            EquipmentSlot slot;
            try {
                slot = EquipmentSlot.valueOf(args[2].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                p.sendMessage("§cSlot không hợp lệ.");
                return;
            }
            if (plugin.getItemManager().setEquipmentSlot(item, slot)) {
                p.getInventory().setItemInMainHand(item);
                p.sendMessage("§aĐã gán slot §e" + slot);
            } else {
                p.sendMessage("§cKhông thể gán slot.");
            }
            return;
        }

        p.sendMessage("§e/rpg item slot <add|remove> [slot]");
    }

    private StatType parseStat(String value) {
        try {
            return StatType.valueOf(value.toUpperCase(Locale.ROOT).replace("-", "_"));
        } catch (Exception ex) {
            return null;
        }
    }

    private String join(String[] args, int start) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > start) b.append(' ');
            b.append(args[i]);
        }
        return b.toString();
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void list(CommandSender sender) {
        var items = plugin.getRPGItemManager().getItems();
        if (items.isEmpty()) { sender.sendMessage("§7Chưa có item nào được đăng ký."); return; }
        sender.sendMessage("§6§lDANH SÁCH ITEM §7(" + items.size() + ")");
        for (RPGItem item : items.values()) {
            sender.sendMessage("§7- §e" + item.getId() + " §7(" + item.getSlot() + ", " + item.getRarity() + ") §f" + item.getName());
        }
    }

    private void give(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage("§e/rpg item give <id> [player] [amount]"); return; }
        RPGItem rpgItem = plugin.getRPGItemManager().get(args[1]);
        if (rpgItem == null) { sender.sendMessage("§cKhông tìm thấy item: §e" + args[1]); return; }
        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null) { sender.sendMessage("§cKhông tìm thấy người chơi: §e" + args[2]); return; }
        } else if (sender instanceof Player player) target = player;
        else { sender.sendMessage("§cConsole phải chỉ định player."); return; }

        int amount = 1;
        if (args.length >= 4) {
            try { amount = Math.max(1, Integer.parseInt(args[3])); }
            catch (NumberFormatException ex) { sender.sendMessage("§cAmount không hợp lệ."); return; }
        }
        for (int i = 0; i < amount; i++) target.getInventory().addItem(plugin.getItemManager().toItemStack(rpgItem));
        sender.sendMessage("§aĐã đưa §e" + amount + "x " + rpgItem.getId() + " §acho §f" + target.getName());
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§e/rpg item create <id> <material> <slot> [rarity]");
            return;
        }
        String id = args[1].toLowerCase(Locale.ROOT);
        if (plugin.getRPGItemManager().exists(id)) { sender.sendMessage("§cItem đã tồn tại."); return; }
        Material material = Material.matchMaterial(args[2].toUpperCase(Locale.ROOT));
        if (material == null) { sender.sendMessage("§cMaterial không hợp lệ."); return; }
        EquipmentSlot slot;
        try { slot = EquipmentSlot.valueOf(args[3].toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { sender.sendMessage("§cSlot không hợp lệ."); return; }
        Rarity rarity = Rarity.COMMON;
        if (args.length >= 5) {
            try { rarity = Rarity.valueOf(args[4].toUpperCase(Locale.ROOT)); }
            catch (IllegalArgumentException ignored) { sender.sendMessage("§eRarity không hợp lệ, dùng COMMON."); }
        }
        RPGItem item = new RPGItem(id, color("&f" + id), material, slot);
        item.setRarity(rarity);
        item.setItemLevel(1);
        plugin.getRPGItemManager().register(item);
        plugin.getRPGConfigManager().saveGeneratedItem(item);
        sender.sendMessage("§aĐã tạo item §e" + id);
    }

    private void delete(CommandSender sender, String[] args) {
        if (args.length < 2) { sender.sendMessage("§e/rpg item delete <id>"); return; }
        String id = args[1].toLowerCase(Locale.ROOT);
        if (!plugin.getRPGItemManager().exists(id)) { sender.sendMessage("§cKhông tìm thấy item."); return; }
        plugin.getRPGItemManager().unregister(id);
        plugin.getRPGConfigManager().deleteGeneratedItem(id);
        sender.sendMessage("§aĐã xoá item §e" + id);
    }
}
