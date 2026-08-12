package dungnt.rpg.item;

import dungnt.rpg.stats.StatType;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SampleItems {

    private static final List<RPGItem> ITEMS =
            new ArrayList<>();

    private SampleItems() {
        // Utility class
    }

    // ==================================================
    // REGISTER ALL
    // ==================================================

    public static void registerAll() {

        ITEMS.clear();

        ITEMS.add(
                createWarriorSword()
        );

        ITEMS.add(
                createMageStaff()
        );

        ITEMS.add(
                createArcherBow()
        );

        ITEMS.add(
                createAssassinDagger()
        );
    }

    // ==================================================
    // WARRIOR
    // ==================================================

    public static RPGItem createWarriorSword() {

        RPGItem item =
                RPGItemFactory.create(
                        "warrior_sword",
                        "§c§lWarrior Sword",
                        Material.IRON_SWORD,
                        EquipmentSlot.MAIN_HAND
                );

        RPGItemFactory.addFlatStat(
                item,
                StatType.ATTACK,
                10
        );

        RPGItemFactory.addFlatStat(
                item,
                StatType.DEFENSE,
                5
        );

        RPGItemFactory.addFlatStat(
                item,
                StatType.MAX_HEALTH,
                20
        );

        return item;
    }

    // ==================================================
    // MAGE
    // ==================================================

    public static RPGItem createMageStaff() {

        RPGItem item =
                RPGItemFactory.create(
                        "mage_staff",
                        "§5§lMage Staff",
                        Material.BLAZE_ROD,
                        EquipmentSlot.MAIN_HAND
                );

        RPGItemFactory.addFlatStat(
                item,
                StatType.MAGIC_ATTACK,
                15
        );

        RPGItemFactory.addFlatStat(
                item,
                StatType.MAGIC_DEFENSE,
                5
        );

        RPGItemFactory.addFlatStat(
                item,
                StatType.MAX_MANA,
                30
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.SKILL_DAMAGE,
                5
        );

        return item;
    }

    // ==================================================
    // ARCHER
    // ==================================================

    public static RPGItem createArcherBow() {

        RPGItem item =
                RPGItemFactory.create(
                        "archer_bow",
                        "§a§lArcher Bow",
                        Material.BOW,
                        EquipmentSlot.MAIN_HAND
                );

        RPGItemFactory.addFlatStat(
                item,
                StatType.ATTACK,
                8
        );

        RPGItemFactory.addFlatStat(
                item,
                StatType.ATTACK_SPEED,
                5
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.CRIT_CHANCE,
                10
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.CRIT_DAMAGE,
                10
        );

        return item;
    }

    // ==================================================
    // ASSASSIN
    // ==================================================

    public static RPGItem createAssassinDagger() {

        RPGItem item =
                RPGItemFactory.create(
                        "assassin_dagger",
                        "§8§lAssassin Dagger",
                        Material.IRON_SWORD,
                        EquipmentSlot.MAIN_HAND
                );

        RPGItemFactory.addFlatStat(
                item,
                StatType.ATTACK,
                7
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.CRIT_CHANCE,
                15
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.CRIT_DAMAGE,
                20
        );

        RPGItemFactory.addPercentStat(
                item,
                StatType.DODGE_CHANCE,
                10
        );

        return item;
    }

    // ==================================================
    // GET ALL
    // ==================================================

    public static List<RPGItem> getAll() {

        return Collections.unmodifiableList(
                ITEMS
        );
    }
}