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

        // Spawn trước mặt player
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
        // REGISTER
        // =========================

        plugin.getMobManager()
                .register(
                        zombie,
                        mobData
                );

        // Đặt HP Minecraft = 50
        zombie.setMaxHealth(50);
        zombie.setHealth(50);

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
                ChatColor.DARK_RED +
                        "⚔ Attack: " +
                        ChatColor.WHITE +
                        stats.getAttack()
        );

        return true;
    }
}