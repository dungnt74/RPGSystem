package dungnt.rpg.skills;

import dungnt.rpg.MyRPG;
import dungnt.rpg.combat.CombatService;
import dungnt.rpg.combat.DamageResult;
import dungnt.rpg.player.PlayerData;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkillContext {

    private final MyRPG plugin;

    private final Player player;

    private final PlayerData playerData;

    private final Skill skill;

    private final CombatService combatService;

    public SkillContext(
            MyRPG plugin,
            Player player,
            PlayerData playerData,
            Skill skill,
            CombatService combatService
    ) {

        this.plugin = plugin;

        this.player = player;

        this.playerData = playerData;

        this.skill = skill;

        this.combatService = combatService;
    }

    // ==================================================
    // PLUGIN
    // ==================================================

    public MyRPG getPlugin() {
        return plugin;
    }

    // ==================================================
    // PLAYER
    // ==================================================

    public Player getPlayer() {
        return player;
    }

    // ==================================================
    // PLAYER DATA
    // ==================================================

    public PlayerData getPlayerData() {
        return playerData;
    }

    // ==================================================
    // SKILL
    // ==================================================

    public Skill getSkill() {
        return skill;
    }

    // ==================================================
    // COMBAT SERVICE
    // ==================================================

    public CombatService getCombatService() {
        return combatService;
    }

    // ==================================================
    // PHYSICAL DAMAGE
    // ==================================================

    public DamageResult damage(
            LivingEntity target,
            double multiplier
    ) {

        return combatService.damage(
                player,
                target,
                multiplier
        );
    }

    // ==================================================
    // MAGIC DAMAGE
    // ==================================================

    public DamageResult magicDamage(
            LivingEntity target,
            double multiplier
    ) {

        return combatService.magicDamage(
                player,
                target,
                multiplier
        );
    }
}