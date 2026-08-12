package dungnt.rpg;

import dungnt.rpg.classsystem.ClassManager;

import dungnt.rpg.combat.CombatService;
import dungnt.rpg.combat.DamageCalculator;
import dungnt.rpg.combat.FloatingDamage;
import dungnt.rpg.combat.RPGCombatListener;
import dungnt.rpg.combat.RPGMobCombatListener;

import dungnt.rpg.command.ClassCommand;
import dungnt.rpg.command.EquipmentCommand;
import dungnt.rpg.command.ItemTestCommand;
import dungnt.rpg.command.LevelCommand;
import dungnt.rpg.command.MagicDamageTestCommand;
import dungnt.rpg.command.MobTestCommand;
import dungnt.rpg.command.SkillCommand;

import dungnt.rpg.item.EquipmentListener;
import dungnt.rpg.item.EquipmentManager;
import dungnt.rpg.item.ItemManager;
import dungnt.rpg.item.RPGItemManager;
import dungnt.rpg.item.SampleItems;

import dungnt.rpg.level.LevelManager;

import dungnt.rpg.mob.MobManager;
import dungnt.rpg.mob.MobStatsManager;

import dungnt.rpg.player.PlayerManager;

import dungnt.rpg.skills.CooldownManager;
import dungnt.rpg.skills.FireballListener;
import dungnt.rpg.skills.SkillManager;
import dungnt.rpg.skills.SkillService;

import dungnt.rpg.stats.StatManager;
import dungnt.rpg.stats.StatTestCommand;

import org.bukkit.plugin.java.JavaPlugin;

public final class MyRPG extends JavaPlugin {

    // ==================================================
    // CLASS
    // ==================================================

    private ClassManager classManager;

    // ==================================================
    // PLAYER
    // ==================================================

    private PlayerManager playerManager;

    // ==================================================
    // STATS
    // ==================================================

    private StatManager statManager;

    // ==================================================
    // LEVEL
    // ==================================================

    private LevelManager levelManager;

    // ==================================================
    // ITEM
    // ==================================================

    private RPGItemManager rpgItemManager;
    private ItemManager itemManager;
    private EquipmentManager equipmentManager;

    // ==================================================
    // SKILL
    // ==================================================

    private SkillManager skillManager;
    private SkillService skillService;
    private CooldownManager cooldownManager;

    // ==================================================
    // COMBAT
    // ==================================================

    private DamageCalculator damageCalculator;
    private CombatService combatService;
    private FloatingDamage floatingDamage;

    // ==================================================
    // MOB
    // ==================================================

    private MobStatsManager mobStatsManager;
    private MobManager mobManager;

    // ==================================================
    // ENABLE
    // ==================================================

    @Override
    public void onEnable() {

        // ==================================================
        // CORE
        // ==================================================

        statManager =
                new StatManager();

        classManager =
                new ClassManager();

        playerManager =
                new PlayerManager(this);

        // ==================================================
        // SKILL
        // ==================================================

        skillManager =
                new SkillManager();

        cooldownManager =
                new CooldownManager();

        // ==================================================
        // ITEM SYSTEM
        // ==================================================

        rpgItemManager =
                new RPGItemManager();

        itemManager =
                new ItemManager(this);

        equipmentManager =
                new EquipmentManager(
                        statManager
                );

        // ==================================================
        // REGISTER SAMPLE ITEMS
        // ==================================================

        SampleItems.registerAll();

        // ==================================================
        // LEVEL
        // ==================================================

        levelManager =
                new LevelManager(this);

        // ==================================================
        // COMBAT
        // ==================================================

        damageCalculator =
                new DamageCalculator();

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
        // MOB
        // ==================================================

        mobStatsManager =
                new MobStatsManager();

        mobManager =
                new MobManager();

        // ==================================================
        // EVENTS
        // ==================================================

        registerEvents();

        // ==================================================
        // COMMANDS
        // ==================================================

        registerCommands();

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

        getLogger().info(
                "RPG Item System: ENABLED"
        );

        getLogger().info(
                "Equipment System: ENABLED"
        );
    }

    // ==================================================
    // EVENTS
    // ==================================================

    private void registerEvents() {

        // --------------------------------------------------
        // SKILL
        // --------------------------------------------------

        getServer()
                .getPluginManager()
                .registerEvents(
                        new FireballListener(this),
                        this
                );

        // --------------------------------------------------
        // RPG COMBAT
        // --------------------------------------------------

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

        // --------------------------------------------------
        // EQUIPMENT
        // --------------------------------------------------

        getServer()
                .getPluginManager()
                .registerEvents(
                        new EquipmentListener(this),
                        this
                );
    }

    // ==================================================
    // COMMANDS
    // ==================================================

    private void registerCommands() {

        // --------------------------------------------------
        // CLASS
        // --------------------------------------------------

        if (getCommand("class") != null) {

            getCommand("class")
                    .setExecutor(
                            new ClassCommand(this)
                    );
        }

        // --------------------------------------------------
        // SKILL
        // --------------------------------------------------

        if (getCommand("skilltest") != null) {

            getCommand("skilltest")
                    .setExecutor(
                            new SkillCommand(this)
                    );
        }

        // --------------------------------------------------
        // STAT
        // --------------------------------------------------

        if (getCommand("stattest") != null) {

            getCommand("stattest")
                    .setExecutor(
                            new StatTestCommand(this)
                    );
        }

        // --------------------------------------------------
        // MOB
        // --------------------------------------------------

        if (getCommand("mobtest") != null) {

            getCommand("mobtest")
                    .setExecutor(
                            new MobTestCommand(this)
                    );
        }

        // --------------------------------------------------
        // MAGIC DAMAGE
        // --------------------------------------------------

        if (getCommand("magicdamage") != null) {

            getCommand("magicdamage")
                    .setExecutor(
                            new MagicDamageTestCommand(this)
                    );
        }

        // --------------------------------------------------
        // ITEM TEST
        // --------------------------------------------------

        if (getCommand("itemtest") != null) {

            getCommand("itemtest")
                    .setExecutor(
                            new ItemTestCommand(this)
                    );
        }

        // --------------------------------------------------
        // EQUIPMENT
        // --------------------------------------------------

        if (getCommand("equipment") != null) {

            getCommand("equipment")
                    .setExecutor(
                            new EquipmentCommand(this)
                    );
        }

        // --------------------------------------------------
        // LEVEL
        // --------------------------------------------------

        if (getCommand("level") != null) {

            getCommand("level")
                    .setExecutor(
                            new LevelCommand(this)
                    );
        }
    }

    // ==================================================
    // DISABLE
    // ==================================================

    @Override
    public void onDisable() {

        if (equipmentManager != null) {

            /*
             * EquipmentManager chỉ giữ runtime data.
             *
             * Không cần lưu khi server shutdown
             * ở phiên bản hiện tại.
             */
        }

        getLogger().info(
                "DungNT RPG DISABLED"
        );
    }

    // ==================================================
    // CLASS GETTER
    // ==================================================

    public ClassManager getClassManager() {

        return classManager;
    }

    // ==================================================
    // PLAYER GETTER
    // ==================================================

    public PlayerManager getPlayerManager() {

        return playerManager;
    }

    // ==================================================
    // STAT GETTER
    // ==================================================

    public StatManager getStatManager() {

        return statManager;
    }

    // ==================================================
    // LEVEL GETTER
    // ==================================================

    public LevelManager getLevelManager() {

        return levelManager;
    }

    // ==================================================
    // ITEM GETTERS
    // ==================================================

    public RPGItemManager getRPGItemManager() {

        return rpgItemManager;
    }

    public ItemManager getItemManager() {

        return itemManager;
    }

    public EquipmentManager getEquipmentManager() {

        return equipmentManager;
    }

    // ==================================================
    // SKILL GETTERS
    // ==================================================

    public SkillManager getSkillManager() {

        return skillManager;
    }

    public SkillService getSkillService() {

        return skillService;
    }

    public CooldownManager getCooldownManager() {

        return cooldownManager;
    }

    // ==================================================
    // COMBAT GETTERS
    // ==================================================

    public DamageCalculator getDamageCalculator() {

        return damageCalculator;
    }

    public CombatService getCombatService() {

        return combatService;
    }

    public FloatingDamage getFloatingDamage() {

        return floatingDamage;
    }

    // ==================================================
    // MOB GETTERS
    // ==================================================

    public MobStatsManager getMobStatsManager() {

        return mobStatsManager;
    }

    public MobManager getMobManager() {

        return mobManager;
    }
}