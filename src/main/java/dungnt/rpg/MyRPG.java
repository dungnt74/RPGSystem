package dungnt.rpg;

import dungnt.rpg.classsystem.ClassManager;
import dungnt.rpg.command.ClassCommand;
import dungnt.rpg.command.SkillCommand;
import dungnt.rpg.player.PlayerManager;
import dungnt.rpg.skills.CooldownManager;
import dungnt.rpg.skills.SkillManager;
import dungnt.rpg.skills.SkillService;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyRPG extends JavaPlugin {

    private SkillService skillService;
    private SkillManager skillManager;
    private CooldownManager cooldownManager;
    private ClassManager classManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {

        // Managers
        classManager = new ClassManager();
        playerManager = new PlayerManager();

        skillManager = new SkillManager();
        cooldownManager = new CooldownManager();

        skillService = new SkillService(this);

        // Commands
        getCommand("class").setExecutor(
                new ClassCommand(this)
        );

        getCommand("skilltest").setExecutor(
                new SkillCommand(this)
        );

        getLogger().info("================================");
        getLogger().info("       DungNT RPG ENABLED");
        getLogger().info("================================");
        getLogger().info("Registered Classes: "
                + classManager.getClasses().size());
    }

    @Override
    public void onDisable() {

        getLogger().info("DungNT RPG DISABLED");
    }

    public ClassManager getClassManager() {
        return classManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public SkillService getSkillService() {
        return skillService;
    }
}