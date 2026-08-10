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

        // TEST +5 ATTACK
        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new StatModifier(
                        "test_attack",
                        StatType.ATTACK,
                        ModifierType.FLAT,
                        5
                )
        );

        // TEST +10% ATTACK
        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new StatModifier(
                        "test_attack_percent",
                        StatType.ATTACK,
                        ModifierType.PERCENT,
                        10
                )
        );

        double attack =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        data.getStats(),
                        StatType.ATTACK
                );

        double defense =
                plugin.getStatManager().getStat(
                        player.getUniqueId(),
                        data.getStats(),
                        StatType.DEFENSE
                );

        player.sendMessage(
                "§6§l===== RPG STATS ====="
        );

        player.sendMessage(
                "§c⚔ Attack: §f"
                        + String.format("%.1f", attack)
        );

        player.sendMessage(
                "§9🛡 Defense: §f"
                        + String.format("%.1f", defense)
        );

        return true;
    }
}