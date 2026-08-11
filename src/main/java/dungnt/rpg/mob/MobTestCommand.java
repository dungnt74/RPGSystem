package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.mob.MobData;
import dungnt.rpg.mob.MobStats;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class MobTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    public MobTestCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "Chỉ người chơi mới sử dụng được command này."
            );

            return true;
        }

        // =========================
        // SPAWN
        // =========================

        Location location =
                player.getLocation()
                        .add(
                                player.getLocation()
                                        .getDirection()
                                        .normalize()
                                        .multiply(3)
                        );

        Zombie zombie =
                player.getWorld()
                        .spawn(
                                location,
                                Zombie.class
                        );

        // =========================
        // MOB STATS
        // =========================

        MobStats stats =
                new MobStats();

        stats.setMaxHealth(50);

        stats.setAttack(8);

        stats.setDefense(5);

        stats.setMagicDefense(10);

        // =========================
        // MOB DATA
        // =========================

        MobData mobData =
                new MobData(
                        zombie.getUniqueId(),
                        "zombie",
                        stats
                );

        // =========================
        // REGISTER MOB
        // =========================

        plugin.getMobManager()
                .register(
                        zombie,
                        mobData
                );

        // =========================
        // REGISTER STATS
        // =========================

        plugin.getMobStatsManager()
                .setStats(
                        zombie.getUniqueId(),
                        stats
                );

        // =========================
        // MINECRAFT HP
        // =========================

        zombie.setMaxHealth(
                stats.getMaxHealth()
        );

        zombie.setHealth(
                stats.getMaxHealth()
        );

        // =========================
        // MESSAGE
        // =========================

        player.sendMessage(
                ChatColor.GREEN +
                        "Đã spawn RPG Zombie!"
        );

        player.sendMessage(
                ChatColor.GRAY +
                        "ID: " +
                        ChatColor.WHITE +
                        mobData.getId()
        );

        player.sendMessage(
                ChatColor.RED +
                        "❤ HP: " +
                        ChatColor.WHITE +
                        stats.getMaxHealth()
        );

        player.sendMessage(
                ChatColor.BLUE +
                        "🛡 Defense: " +
                        ChatColor.WHITE +
                        stats.getDefense()
        );

        player.sendMessage(
                ChatColor.DARK_PURPLE +
                        "✨ Magic Defense: " +
                        ChatColor.WHITE +
                        stats.getMagicDefense()
        );

        player.sendMessage(
                ChatColor.DARK_RED +
                        "⚔ Attack: " +
                        ChatColor.WHITE +
                        stats.getAttack()
        );

        return true;
    }
}