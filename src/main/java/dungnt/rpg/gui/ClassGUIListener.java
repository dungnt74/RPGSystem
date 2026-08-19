package dungnt.rpg.gui;

import dungnt.rpg.MyRPG;
import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.player.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClassGUIListener
        implements Listener {

    private final MyRPG plugin;

    private final ClassGUI classGUI;

    public ClassGUIListener(
            MyRPG plugin,
            ClassGUI classGUI
    ) {

        this.plugin = plugin;

        this.classGUI =
                classGUI;
    }

    // ==================================================
    // CLICK
    // ==================================================

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onClick(
            InventoryClickEvent event
    ) {

        if (!(event.getWhoClicked()
                instanceof Player player)) {

            return;
        }

        if (!event.getView()
                .getTitle()
                .equals(
                        ClassGUI.TITLE
                )) {

            return;
        }

        event.setCancelled(true);

        /*
         * Chỉ xử lý click
         * trong top inventory.
         */
        if (event.getRawSlot() < 0 ||
                event.getRawSlot() >= 27) {

            return;
        }

        String classId =
                classGUI.getClassId(
                        event.getRawSlot()
                );

        if (classId == null) {
            return;
        }

        RPGClass rpgClass =
                plugin.getClassManager()
                        .getClass(
                                classId
                        );

        if (rpgClass == null) {
            return;
        }

        PlayerData data =
                plugin.getPlayerManager()
                        .getData(
                                player
                        );

        /*
         * Không đổi nếu đang cùng class.
         */
        if (data.getRpgClass() != null &&
                data.getRpgClass()
                        .getId()
                        .equalsIgnoreCase(
                                classId
                        )) {

            player.sendMessage(
                    "§eBạn đang sử dụng Class này."
            );

            return;
        }

        RPGClass oldClass =
                data.getRpgClass();

        /*
         * SET CLASS
         *
         * PlayerManager sẽ:
         * - remove class cũ
         * - set class mới
         * - apply class stats
         * - apply level bonus
         */
        plugin.getPlayerManager()
                .setClass(
                        player,
                        rpgClass
                );

        player.closeInventory();

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§a§l✔ CLASS SELECTED"
        );

        if (oldClass != null) {

            player.sendMessage(
                    "§7Class cũ: §c"
                            + oldClass.getName()
            );
        }

        player.sendMessage(
                "§7Class mới: §e"
                        + rpgClass.getName()
        );

        player.sendMessage(
                "§7"
                        + rpgClass.getDescription()
        );

        player.sendMessage(
                "§a✦ Class Stats đã được áp dụng."
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}