package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatType;
import dungnt.socket.Gem;
import dungnt.socket.GemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public final class RPGCommand implements CommandExecutor, TabCompleter {

    private final MyRPG plugin;
    private final RPGItemAdminCommand itemCommand;
    private final RPGMobAdminCommand mobCommand;

    public RPGCommand(MyRPG plugin) {
        this.plugin = plugin;
        this.itemCommand = new RPGItemAdminCommand(plugin);
        this.mobCommand = new RPGMobAdminCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            help(sender);
            return true;
        }
        if (!sender.hasPermission("dungntrpg.admin") &&
                (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("item")
                        || args[0].equalsIgnoreCase("mob") || args[0].equalsIgnoreCase("gem")
                        || args[0].equalsIgnoreCase("socket"))) {
            sender.sendMessage("§cBạn không có quyền dùng /rpg admin.");
            return true;
        }

        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> reload(sender);
            case "item" -> itemCommand.handle(sender, subArgs(args));
            case "mob" -> mobCommand.handle(sender, subArgs(args));
            case "gem" -> handleGem(sender, subArgs(args));
            case "socket" -> handleSocket(sender, subArgs(args));
            default -> { sender.sendMessage("§cSub-command không hợp lệ. Dùng §e/rpg help§c."); yield true; }
        };
    }

    private String[] subArgs(String[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    private boolean reload(CommandSender sender) {
        int items = plugin.getRPGConfigManager().reloadItems();
        plugin.getOtherSkillManager().reload();
        sender.sendMessage("§a✔ Reload thành công. Items: §e" + items + " §7| OtherSkills: §aOK");
        return true;
    }

    private boolean handleGem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cLệnh Gem chỉ dùng trong game.");
            return true;
        }
        GemManager gm = plugin.getGemManager();
        if (args.length == 0) { gemHelp(p); return true; }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create", "special" -> {
                if (args.length < 3) {
                    p.sendMessage("§e/rpg gem " + args[0] + " <id> <name>");
                    return true;
                }
                boolean special = args[0].equalsIgnoreCase("special");
                ItemStack gem = gm.createGem(args[1], color(join(args, 2)), special);
                p.getInventory().addItem(gem);
                p.sendMessage("§aĐã tạo Gem §e" + args[1]);
            }
            case "name" -> {
                if (args.length < 2) { p.sendMessage("§e/rpg gem name <name>"); return true; }
                ItemStack held = p.getInventory().getItemInMainHand();
                if (!gm.isGem(held)) { p.sendMessage("§cHãy cầm Gem."); return true; }
                gm.setName(held, color(join(args, 1)));
                p.getInventory().setItemInMainHand(held);
                p.sendMessage("§aĐã đổi tên Gem.");
            }
            case "lore" -> {
                ItemStack held = p.getInventory().getItemInMainHand();
                if (!gm.isGem(held)) { p.sendMessage("§cHãy cầm Gem."); return true; }
                if (args.length < 3) { p.sendMessage("§e/rpg gem lore <add|remove> <text>"); return true; }
                if (args[1].equalsIgnoreCase("add")) {
                    gm.addLore(held, color(join(args, 2)));
                    p.getInventory().setItemInMainHand(held);
                    p.sendMessage("§aĐã thêm lore Gem.");
                } else if (args[1].equalsIgnoreCase("remove")) {
                    boolean ok = gm.removeLore(held, join(args, 2));
                    p.getInventory().setItemInMainHand(held);
                    p.sendMessage(ok ? "§aĐã xoá lore Gem." : "§eKhông tìm thấy lore.");
                } else p.sendMessage("§e/rpg gem lore <add|remove> <text>");
            }
            case "stat" -> {
                ItemStack held = p.getInventory().getItemInMainHand();
                if (!gm.isGem(held)) { p.sendMessage("§cHãy cầm Gem."); return true; }
                if (args.length < 2) { p.sendMessage("§e/rpg gem stat <add|remove> ..."); return true; }
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 5) { p.sendMessage("§e/rpg gem stat add <stat> <flat|percent> <amount>"); return true; }
                    StatType stat = parseStat(args[2]);
                    ModifierType mod = parseModifier(args[3]);
                    if (stat == null || mod == null) { p.sendMessage("§cStat/modifier không hợp lệ."); return true; }
                    try {
                        gm.setStat(held, stat, mod, Double.parseDouble(args[4]));
                    } catch (NumberFormatException ex) {
                        p.sendMessage("§cAmount phải là số."); return true;
                    }
                    p.getInventory().setItemInMainHand(held);
                    p.sendMessage("§aĐã đặt stat Gem.");
                } else if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length < 3) { p.sendMessage("§e/rpg gem stat remove <stat>"); return true; }
                    StatType stat = parseStat(args[2]);
                    if (stat == null) { p.sendMessage("§cStat không hợp lệ."); return true; }
                    boolean ok = gm.removeStat(held, stat);
                    p.getInventory().setItemInMainHand(held);
                    p.sendMessage(ok ? "§aĐã xoá stat Gem." : "§eGem không có stat.");
                } else p.sendMessage("§e/rpg gem stat <add|remove> ...");
            }
            case "level" -> {
                if (args.length < 2) { p.sendMessage("§e/rpg gem level <1-5>"); return true; }
                ItemStack held = p.getInventory().getItemInMainHand();
                if (!gm.isGem(held)) { p.sendMessage("§cHãy cầm Gem."); return true; }
                try {
                    Gem g = gm.data(held);
                    int lv = Integer.parseInt(args[1]);
                    if (lv < 1 || lv > 5 || (g.special() && lv > 1)) {
                        p.sendMessage("§cLevel không hợp lệ.");
                        return true;
                    }
                    gm.setLevel(held, lv);
                    p.getInventory().setItemInMainHand(held);
                    p.sendMessage("§aĐã set Gem level " + lv);
                } catch (Exception ex) { p.sendMessage("§cLevel không hợp lệ."); }
            }
            default -> gemHelp(p);
        }
        return true;
    }

    private boolean handleSocket(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cLệnh Socket chỉ dùng trong game.");
            return true;
        }
        GemManager gm = plugin.getGemManager();
        if (args.length == 0) {
            plugin.getSocketGUI().open(p);
            return true;
        }
        if (!args[0].equalsIgnoreCase("slot") || args.length < 2) {
            p.sendMessage("§e/rpg socket §7- mở GUI");
            p.sendMessage("§e/rpg socket slot <add|remove>");
            return true;
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.getType().isAir()) { p.sendMessage("§cHãy cầm item."); return true; }
        if (!plugin.getItemManager().ensureRPGItem(item)) { p.sendMessage("§cKhông thể đánh dấu RPG item."); return true; }

        if (args[1].equalsIgnoreCase("add")) {
            if (!p.isOp()) { p.sendMessage("§cBạn cần OP."); return true; }
            if (!gm.addSocket(item)) { p.sendMessage("§cKhông thể mở thêm socket (tối đa 4)."); return true; }
            p.getInventory().setItemInMainHand(item);
            p.sendMessage("§aĐã thêm socket. Hiện có §e" + gm.getSocketCount(item) + "/4");
        } else if (args[1].equalsIgnoreCase("remove")) {
            if (!p.isOp()) { p.sendMessage("§cBạn cần OP."); return true; }
            if (!gm.removeSocket(item)) {
                p.sendMessage("§cKhông thể xoá socket cuối: có thể item chưa có socket hoặc socket đang chứa Gem.");
                return true;
            }
            p.getInventory().setItemInMainHand(item);
            p.sendMessage("§aĐã xoá socket cuối.");
        } else {
            p.sendMessage("§e/rpg socket slot <add|remove>");
        }
        return true;
    }

    private void help(CommandSender sender) {
        sender.sendMessage("§8§m--------------------------------");
        sender.sendMessage("§6§l/rpg");
        sender.sendMessage("§e/rpg item §7- quản lý Item");
        sender.sendMessage("§e/rpg mob §7- quản lý Mob");
        sender.sendMessage("§e/rpg gem §7- tạo/chỉnh Gem");
        sender.sendMessage("§e/rpg socket §7- quản lý Socket");
        sender.sendMessage("§e/rpg reload §7- reload config");
        sender.sendMessage("§8§m--------------------------------");
    }

    private void gemHelp(Player p) {
        p.sendMessage("§6§l/rpg gem");
        p.sendMessage("§e/rpg gem create <id> <name>");
        p.sendMessage("§e/rpg gem special <id> <name>");
        p.sendMessage("§e/rpg gem name <name>");
        p.sendMessage("§e/rpg gem lore <add|remove> <text>");
        p.sendMessage("§e/rpg gem stat <add|remove> ...");
        p.sendMessage("§e/rpg gem level <1-5>");
    }

    private StatType parseStat(String s) {
        try { return StatType.valueOf(s.toUpperCase(Locale.ROOT).replace("-", "_")); }
        catch (Exception ex) { return null; }
    }

    private ModifierType parseModifier(String s) {
        try { return ModifierType.valueOf(s.toUpperCase(Locale.ROOT)); }
        catch (Exception ex) { return null; }
    }

    private String join(String[] a, int start) {
        StringBuilder b = new StringBuilder();
        for (int i=start; i<a.length; i++) { if (i>start) b.append(' '); b.append(a[i]); }
        return b.toString();
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return partial(args[0], List.of("help","reload","item","mob","gem","socket"));
        if (args[0].equalsIgnoreCase("item")) return itemTabs(args);
        if (args[0].equalsIgnoreCase("mob")) return mobTabs(args);
        if (args[0].equalsIgnoreCase("gem")) return gemTabs(args);
        if (args[0].equalsIgnoreCase("socket")) return socketTabs(args);
        return Collections.emptyList();
    }

    private List<String> itemTabs(String[] a) {
        if (a.length == 2) return partial(a[1], List.of("stat","rename","lore","slot","list","give","create","delete"));
        if (a.length == 3 && a[1].equalsIgnoreCase("stat")) return partial(a[2], List.of("add","remove","clear"));
        if (a.length == 3 && a[1].equalsIgnoreCase("lore")) return partial(a[2], List.of("add","remove"));
        if (a.length == 3 && a[1].equalsIgnoreCase("slot")) return partial(a[2], List.of("add","remove"));
        if (a.length == 5 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("add"))
            return partial(a[4], Arrays.stream(StatType.values()).map(Enum::name).collect(Collectors.toList()));
        if (a.length == 5 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("remove"))
            return partial(a[4], Arrays.stream(StatType.values()).map(Enum::name).collect(Collectors.toList()));
        if (a.length == 5 && a[1].equalsIgnoreCase("slot") && a[2].equalsIgnoreCase("add"))
            return partial(a[4], Arrays.stream(EquipmentSlot.values()).map(Enum::name).collect(Collectors.toList()));
        if (a.length == 6 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("add"))
            return partial(a[5], List.of("flat","percent"));
        if (a.length == 3 && a[1].equalsIgnoreCase("give"))
            return partial(a[2], plugin.getRPGItemManager().getItems().keySet());
        return Collections.emptyList();
    }

    private List<String> mobTabs(String[] a) {
        if (a.length == 2) return partial(a[1], List.of("list","spawn","egg","create","delete"));
        if (a.length == 3 && (a[1].equalsIgnoreCase("spawn") || a[1].equalsIgnoreCase("egg") || a[1].equalsIgnoreCase("delete")))
            return partial(a[2], plugin.getMobDefinitionManager().getDefinitions().keySet());
        if (a.length == 3 && a[1].equalsIgnoreCase("create"))
            return partial(a[2], Arrays.stream(org.bukkit.entity.EntityType.values()).filter(org.bukkit.entity.EntityType::isAlive).map(Enum::name).collect(Collectors.toList()));
        return Collections.emptyList();
    }

    private List<String> gemTabs(String[] a) {
        if (a.length == 2) return partial(a[1], List.of("create","special","name","lore","stat","level"));
        if (a.length == 3 && a[1].equalsIgnoreCase("lore")) return partial(a[2], List.of("add","remove"));
        if (a.length == 3 && a[1].equalsIgnoreCase("stat")) return partial(a[2], List.of("add","remove"));
        if (a.length == 5 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("add"))
            return partial(a[4], Arrays.stream(StatType.values()).map(Enum::name).collect(Collectors.toList()));
        if (a.length == 6 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("add"))
            return partial(a[5], List.of("flat","percent"));
        if (a.length == 5 && a[1].equalsIgnoreCase("stat") && a[2].equalsIgnoreCase("remove"))
            return partial(a[4], Arrays.stream(StatType.values()).map(Enum::name).collect(Collectors.toList()));
        if (a.length == 3 && a[1].equalsIgnoreCase("level"))
            return partial(a[2], List.of("1","2","3","4","5"));
        return Collections.emptyList();
    }

    private List<String> socketTabs(String[] a) {
        if (a.length == 2) return partial(a[1], List.of("slot"));
        if (a.length == 3 && a[1].equalsIgnoreCase("slot")) return partial(a[2], List.of("add","remove"));
        return Collections.emptyList();
    }

    private List<String> partial(String token, Collection<String> values) {
        String lower = token.toLowerCase(Locale.ROOT);
        return values.stream().filter(v -> v.toLowerCase(Locale.ROOT).startsWith(lower)).sorted().collect(Collectors.toList());
    }
}
