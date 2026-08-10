package dungnt.rpg;

import dungnt.rpg.classsystem.ClassManager;
import dungnt.rpg.combat.*;
import dungnt.rpg.command.ClassCommand;
import dungnt.rpg.command.MagicDamageTestCommand;
import dungnt.rpg.command.MobTestCommand;
import dungnt.rpg.command.SkillCommand;
import dungnt.rpg.mob.MobManager;
import dungnt.rpg.mob.MobStatsManager;
import dungnt.rpg.player.PlayerManager;
import dungnt.rpg.skills.CooldownManager;
import dungnt.rpg.skills.SkillManager;
import dungnt.rpg.skills.SkillService;
import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatTestCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyRPG extends JavaPlugin {

    private SkillManager skillManager;
    private SkillService skillService;

    private CooldownManager cooldownManager;
    private ClassManager classManager;

    private PlayerManager playerManager;
    private DamageCalculator damageCalculator;
    private CombatService combatService;

    private StatManager statManager;

    private MobStatsManager mobStatsManager;
    private MobManager mobManager;

    @Override
    public void onEnable() {

        // Managers
        classManager = new ClassManager();
        playerManager = new PlayerManager();

        skillManager = new SkillManager();
        cooldownManager = new CooldownManager();

        statManager = new StatManager();

        damageCalculator = new DamageCalculator();

        combatService = new CombatService(this);

        skillService = new SkillService(this);

        mobStatsManager = new MobStatsManager();
        mobManager = new MobManager();

        getServer().getPluginManager().registerEvents(
                new DamageListener(),
                this
        );

        getServer().getPluginManager().registerEvents(
                new RPGCombatListener(this),
                this
        );

        getServer().getPluginManager().registerEvents(
                new RPGMobCombatListener(this),
                this
        );

        // Commands
        getCommand("class").setExecutor(
                new ClassCommand(this)
        );

        getCommand("skilltest").setExecutor(
                new SkillCommand(this)
        );

        getCommand("stattest")
                .setExecutor(
                        new StatTestCommand(this)
                );

        getCommand("mobtest")
                .setExecutor(
                        new MobTestCommand(this)
                );

        getCommand("magicdamage")
                .setExecutor(
                        new MagicDamageTestCommand(this)
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

    public DamageCalculator getDamageCalculator() {
        return damageCalculator;
    }

    public CombatService getCombatService() {
        return combatService;
    }

    public StatManager getStatManager() {
        return statManager;
    }

    public MobStatsManager getMobStatsManager() {
        return mobStatsManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }
}