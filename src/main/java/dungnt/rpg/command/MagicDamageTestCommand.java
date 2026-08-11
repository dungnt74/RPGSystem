package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.DamageResult;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class MagicDamageTestCommand
        implements CommandExecutor {

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

        // =========================
        // TARGET
        // =========================

        Entity targetEntity =
                player.getTargetEntity(10);

        if (!(targetEntity instanceof LivingEntity target)) {

            player.sendMessage(
                    "§cHãy nhìn vào một mob!"
            );

            return true;
        }

        // =========================
        // TEST MAGIC ATTACK
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),

                new StatModifier(
                        "test_magic_attack",
                        StatType.MAGIC_ATTACK,
                        ModifierType.FLAT,
                        30
                )
        );

        // =========================
        // TEST MAGIC PENETRATION
        // =========================

        plugin.getStatManager().addModifier(
                player.getUniqueId(),

                new StatModifier(
                        "test_magic_penetration",
                        StatType.MAGIC_PENETRATION,
                        ModifierType.FLAT,
                        0
                )
        );

        // =========================
        // MAGIC DAMAGE
        // =========================

        DamageResult result =
                plugin.getCombatService()
                        .magicDamage(
                                player,
                                target,
                                1.0
                        );

        // =========================
        // RESULT
        // =========================

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

        player.sendMessage(
                "§7Magic Attack: §f30"
        );

        player.sendMessage(
                "§7Magic Penetration: §f0%"
        );

        // =========================
        // MOB INFO
        // =========================

        dungnt.rpg.mob.MobData mobData =
                plugin.getMobManager()
                        .getMob(target);

        if (mobData != null) {

            player.sendMessage(
                    "§7Mob: §f"
                            + mobData.getId()
            );

            player.sendMessage(
                    "§7Magic Defense: §f"
                            + mobData
                            .getStats()
                            .getMagicDefense()
            );
        }

        return true;
    }
}