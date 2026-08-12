package dungnt.rpg.stats;

import dungnt.rpg.MyRPG;
import dungnt.rpg.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    public StatTestCommand(MyRPG plugin) {
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
            return true;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(player);

        if (data == null) {
            player.sendMessage(
                    "§cKhông tìm thấy PlayerData!"
            );
            return true;
        }

        // =========================
        // LẤY FINAL STATS
        // =========================

        double attack =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        StatType.ATTACK
                );

        double defense =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        StatType.DEFENSE
                );

        double critChance =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        StatType.CRIT_CHANCE
                );

        double critDamage =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        StatType.CRIT_DAMAGE
                );

        // =========================
        // HIỂN THỊ
        // =========================

        player.sendMessage(
                "§6§l===== RPG STATS ====="
        );

        player.sendMessage(
                "§c⚔ Attack: §f"
                        + String.format(
                        "%.1f",
                        attack
                )
        );

        player.sendMessage(
                "§9🛡 Defense: §f"
                        + String.format(
                        "%.1f",
                        defense
                )
        );

        player.sendMessage(
                "§e✦ Crit Chance: §f"
                        + String.format(
                        "%.1f%%",
                        critChance
                )
        );

        player.sendMessage(
                "§6✦ Crit Damage: §f"
                        + String.format(
                        "%.1f%%",
                        critDamage * 100
                )
        );

        return true;
    }
}