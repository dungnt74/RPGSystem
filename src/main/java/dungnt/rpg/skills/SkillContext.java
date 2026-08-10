package dungnt.rpg.skills;

import dungnt.rpg.combat.CombatService;
import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class SkillContext {

    private final Player player;
    private final PlayerData playerData;
    private final Skill skill;

    private final CombatService combatService;

    public SkillContext(
            Player player,
            PlayerData playerData,
            Skill skill,
            CombatService combatService
    ) {
        this.player = player;
        this.playerData = playerData;
        this.skill = skill;
        this.combatService = combatService;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public Skill getSkill() {
        return skill;
    }

    public CombatService getCombatService() {
        return combatService;
    }
}