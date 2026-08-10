package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.DamageResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MagicDamageTestCommand implements CommandExecutor {

    private final MyRPG plugin;

    public MagicDamageTestCommand(MyRPG plugin) {
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

        Entity targetEntity =
                player.getTargetEntity(10);

        if (!(targetEntity instanceof LivingEntity target)) {

            player.sendMessage(
                    "§cHãy nhìn vào một mob!"
            );

            return true;
        }

        // Test Magic Attack = 30
        plugin.getStatManager().addModifier(
                player.getUniqueId(),
                new dungnt.rpg.stats.StatModifier(
                        "test_magic_attack",
                        dungnt.rpg.stats.StatType.MAGIC_ATTACK,
                        dungnt.rpg.stats.ModifierType.FLAT,
                        30
                )
        );

        DamageResult result =
                plugin.getCombatService()
                        .magicDamage(
                                player,
                                target,
                                1.0
                        );

        player.sendMessage(
                "§d§l===== MAGIC TEST ====="
        );

        player.sendMessage(
                "§5Magic Damage: §f"
                        + String.format(
                        "%.1f",
                        result.getDamage()
                )
        );

        player.sendMessage(
                "§6Critical: §f"
                        + result.isCritical()
        );

        return true;
    }
}