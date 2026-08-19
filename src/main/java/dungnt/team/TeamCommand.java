package dungnt.team;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public final class TeamCommand implements CommandExecutor {
    private final TeamManager manager;
    public TeamCommand(TeamManager manager) { this.manager = manager; }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Player only."); return true; }
        if (args.length == 0) {
            p.sendMessage(manager.info(p.getUniqueId()));
            p.sendMessage("§7/team invite <player> §f| §7/team kick <player> §f| §7/team accept §f| §7/team leave");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "invite" -> {
                if (args.length < 2) { p.sendMessage("§c/team invite <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { p.sendMessage("§cPlayer không online."); return true; }
                p.sendMessage(manager.invite(p, target) ? "§aĐã mời " + target.getName() + "." : "§cKhông thể mời.");
            }
            case "accept" -> p.sendMessage(manager.accept(p) ? "§aĐã vào team." : "§cKhông có lời mời hợp lệ.");
            case "kick" -> {
                if (args.length < 2) { p.sendMessage("§c/team kick <player>"); return true; }
                Player target = Bukkit.getPlayerExact(args[1]);
                p.sendMessage(target != null && manager.kick(p, target) ? "§aĐã đá " + target.getName() + "." : "§cKhông thể đá.");
            }
            case "leave" -> p.sendMessage(manager.leave(p) ? "§aĐã rời team." : "§cLeader không thể leave; hãy offline để giải tán team.");
            default -> p.sendMessage(manager.info(p.getUniqueId()));
        }
        return true;
    }
}
