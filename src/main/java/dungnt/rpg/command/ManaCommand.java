package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManaCommand implements CommandExecutor {
    private final MyRPG plugin;
    public ManaCommand(MyRPG plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cDùng: /mana <amount> hoặc /mana add <player> <amount>");
                return true;
            }
            PlayerData data = plugin.getPlayerManager().getData(player);
            sender.sendMessage("§bMana: §f" + fmt(data.getMana()) + "§7/§f" + fmt(data.getMaxMana()));
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (!sender.hasPermission("dungntrpg.admin")) {
                sender.sendMessage("§cBạn không có quyền dùng lệnh này.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage("§cDùng: §e/mana add <player> <number>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage("§cKhông tìm thấy player đang online: §e" + args[1]);
                return true;
            }
            Double amount = parse(args[2]);
            if (amount == null || amount <= 0) {
                sender.sendMessage("§cMana phải là số lớn hơn 0.");
                return true;
            }
            PlayerData data = plugin.getPlayerManager().getData(target);
            double before = data.getMana();
            data.addMana(amount);
            plugin.getPlayerManager().saveData(target);
            sender.sendMessage("§aĐã cộng §b" + fmt(data.getMana() - before) + " Mana §acho §e" + target.getName() + "§a.");
            target.sendMessage("§b✦ Bạn nhận được §f" + fmt(data.getMana() - before) + " §bMana.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cConsole dùng: /mana add <player> <number>");
            return true;
        }
        Double amount = parse(args[0]);
        if (amount == null || amount <= 0) {
            player.sendMessage("§cDùng: §e/mana <number> §7để cộng Mana cho bạn.");
            return true;
        }
        PlayerData data = plugin.getPlayerManager().getData(player);
        double before = data.getMana();
        data.addMana(amount);
        plugin.getPlayerManager().saveData(player);
        player.sendMessage("§a+§b" + fmt(data.getMana() - before) + " Mana §7(§b" + fmt(data.getMana()) + "§7/§b" + fmt(data.getMaxMana()) + "§7)");
        return true;
    }

    private Double parse(String value) {
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) { return null; }
    }

    private String fmt(double v) {
        return Math.abs(v - Math.rint(v)) < 0.0001 ? String.valueOf((long) Math.rint(v)) : String.format(java.util.Locale.US, "%.1f", v);
    }
}
