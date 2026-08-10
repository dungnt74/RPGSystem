package dungnt.rpg.skills;

import dungnt.rpg.player.PlayerData;
import org.bukkit.entity.Player;

public class SkillContext {

    private final Player player;
    private final PlayerData playerData;
    private final Skill skill;

    public SkillContext(
            Player player,
            PlayerData playerData,
            Skill skill
    ) {
        this.player = player;
        this.playerData = playerData;
        this.skill = skill;
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
}