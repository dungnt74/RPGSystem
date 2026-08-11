package dungnt.rpg;

import dungnt.rpg.classsystem.ClassManager;
import dungnt.rpg.combat.CombatService;
import dungnt.rpg.combat.DamageCalculator;
import dungnt.rpg.combat.DamageListener;
import dungnt.rpg.combat.FloatingDamage;
import dungnt.rpg.combat.RPGCombatListener;
import dungnt.rpg.combat.RPGMobCombatListener;
import dungnt.rpg.equipment.EquipmentItemManager;
import dungnt.rpg.equipment.EquipmentLoreManager;
import dungnt.rpg.equipment.EquipmentStatManager;
import dungnt.rpg.equipment.EquipmentListener;

import dungnt.rpg.command.ClassCommand;
import dungnt.rpg.command.LevelCommand;
import dungnt.rpg.command.MagicDamageTestCommand;
import dungnt.rpg.command.MobTestCommand;
import dungnt.rpg.command.SkillCommand;

import dungnt.rpg.mob.MobManager;
import dungnt.rpg.mob.MobStatsManager;

import dungnt.rpg.player.PlayerManager;

import dungnt.rpg.skills.CooldownManager;
import dungnt.rpg.skills.FireballListener;
import dungnt.rpg.skills.SkillManager;
import dungnt.rpg.skills.SkillService;

import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatTestCommand;

import dungnt.rpg.level.LevelManager;

import org.bukkit.plugin.java.JavaPlugin;

public final class MyRPG extends JavaPlugin {

    // ==================================================
    // SKILL
    // ==================================================

    private SkillManager skillManager;
    private SkillService skillService;
    private CooldownManager cooldownManager;

    // ==================================================
    // CLASS
    // ==================================================

    private ClassManager classManager;

    // ==================================================
    // PLAYER
    // ==================================================

    private PlayerManager playerManager;

    // ==================================================
    // COMBAT
    // ==================================================

    private DamageCalculator damageCalculator;
    private CombatService combatService;
    private FloatingDamage floatingDamage;

    // ==================================================
    // STATS
    // ==================================================

    private StatManager statManager;

    // ==================================================
    // LEVEL
    // ==================================================

    private LevelManager levelManager;

    // ==================================================
    // MOB
    // ==================================================

    private MobStatsManager mobStatsManager;
    private MobManager mobManager;

    private EquipmentStatManager equipmentStatManager;
    private EquipmentItemManager equipmentItemManager;
    private EquipmentLoreManager equipmentLoreManager;
    // ==================================================
    // ENABLE
    // ==================================================

    @Override
    public void onEnable() {

        // ==================================================
        // MANAGERS
        // ==================================================

        classManager =
                new ClassManager();

        playerManager =
                new PlayerManager(this);

        skillManager =
                new SkillManager();

        cooldownManager =
                new CooldownManager();

        statManager =
                new StatManager();

        equipmentItemManager =
                new EquipmentItemManager(this);

        equipmentLoreManager =
                new EquipmentLoreManager(
                        equipmentItemManager
                );

        equipmentStatManager =
                new EquipmentStatManager(
                        statManager
                );

        damageCalculator =
                new DamageCalculator();

        mobStatsManager =
                new MobStatsManager();

        mobManager =
                new MobManager();

        // ==================================================
        // LEVEL
        // ==================================================

        levelManager =
                new LevelManager(this);

        // ==================================================
        // COMBAT
        // ==================================================

        floatingDamage =
                new FloatingDamage(this);

        combatService =
                new CombatService(this);

        // ==================================================
        // SKILL SERVICE
        // ==================================================

        skillService =
                new SkillService(this);

        // ==================================================
        // EVENTS
        // ==================================================

        getServer()
                .getPluginManager()
                .registerEvents(
                        new DamageListener(),
                        this
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new FireballListener(this),
                        this
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new RPGCombatListener(this),
                        this
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new RPGMobCombatListener(this),
                        this
                );

        getServer()
                .getPluginManager()
                .registerEvents(
                        new EquipmentListener(
                                this,
                                statManager,
                                equipmentItemManager
                        ),
                        this
                );

        // ==================================================
        // COMMANDS
        // ==================================================

        if (getCommand("class") != null) {

            getCommand("class")
                    .setExecutor(
                            new ClassCommand(this)
                    );
        }

        if (getCommand("skilltest") != null) {

            getCommand("skilltest")
                    .setExecutor(
                            new SkillCommand(this)
                    );
        }

        if (getCommand("stattest") != null) {

            getCommand("stattest")
                    .setExecutor(
                            new StatTestCommand(this)
                    );
        }

        if (getCommand("mobtest") != null) {

            getCommand("mobtest")
                    .setExecutor(
                            new MobTestCommand(this)
                    );
        }

        if (getCommand("magicdamage") != null) {

            getCommand("magicdamage")
                    .setExecutor(
                            new MagicDamageTestCommand(this)
                    );
        }

        // ==================================================
        // LEVEL COMMAND
        // ==================================================

        if (getCommand("level") != null) {

            getCommand("level")
                    .setExecutor(
                            new LevelCommand(this)
                    );
        }

        // ==================================================
        // LOG
        // ==================================================

        getLogger().info(
                "================================"
        );

        getLogger().info(
                "       DungNT RPG ENABLED"
        );

        getLogger().info(
                "================================"
        );

        getLogger().info(
                "Registered Classes: "
                        + classManager
                        .getClasses()
                        .size()
        );
    }

    // ==================================================
    // DISABLE
    // ==================================================

    @Override
    public void onDisable() {

        getLogger().info(
                "DungNT RPG DISABLED"
        );
    }

    // ==================================================
    // GETTERS
    // ==================================================

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

    public FloatingDamage getFloatingDamage() {
        return floatingDamage;
    }

    public StatManager getStatManager() {
        return statManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public MobStatsManager getMobStatsManager() {
        return mobStatsManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    public EquipmentStatManager getEquipmentStatManager() {
        return equipmentStatManager;
    }

    public EquipmentItemManager getEquipmentItemManager() {
        return equipmentItemManager;
    }

    public EquipmentLoreManager getEquipmentLoreManager() {
        return equipmentLoreManager;
    }
}