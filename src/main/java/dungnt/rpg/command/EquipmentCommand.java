package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.gui.EquipmentGUI;
import dungnt.rpg.item.EquipmentManager;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.item.ItemManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class EquipmentCommand
        implements CommandExecutor {

    private final MyRPG plugin;
    private final EquipmentManager equipmentManager;
    private final EquipmentGUI equipmentGUI;
    private final ItemManager itemManager;

    public EquipmentCommand(
            MyRPG plugin,
            EquipmentGUI equipmentGUI
    ) {

        this.plugin = plugin;

        this.equipmentManager =
                plugin.getEquipmentManager();

        this.equipmentGUI =
                equipmentGUI;

        this.itemManager =
                plugin.getItemManager();
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player player)) {

            sender.sendMessage(
                    "§cChỉ Player mới sử dụng được."
            );

            return true;
        }

        // ==================================================
        // /EQUIPMENT
        // ==================================================

        if (args.length == 0) {

            equipmentGUI.open(player);
            return true;
        }

        // ==================================================
        // /EQUIPMENT ADDSLOT <SLOT>
        // ==================================================

        if (args[0].equalsIgnoreCase("addslot")
                || args[0].equalsIgnoreCase("slot")
                || args[0].equalsIgnoreCase("setslot")) {

            if (args.length < 2) {

                player.sendMessage(
                        "§cDùng: §e/equipment addslot <slot>"
                );

                sendSlots(player);
                return true;
            }

            EquipmentSlot slot =
                    parseSlot(args[1]);

            if (slot == null) {

                player.sendMessage(
                        "§cEquipment slot không hợp lệ."
                );

                sendSlots(player);
                return true;
            }

            ItemStack item =
                    player.getInventory()
                            .getItemInMainHand();

            if (item == null ||
                    item.getType().isAir()) {

                player.sendMessage(
                        "§cHãy cầm item cần set slot."
                );

                return true;
            }

            if (!itemManager.setEquipmentSlot(
                    item,
                    slot
            )) {

                player.sendMessage(
                        "§cKhông thể set equipment slot cho item."
                );

                return true;
            }

            player.getInventory()
                    .setItemInMainHand(item);

            player.sendMessage(
                    "§aĐã set slot §e"
                            + slot.name()
                            + " §acho item đang cầm."
            );

            return true;
        }

        // ==================================================
        // /EQUIPMENT REMOVESLOT
        // ==================================================

        if (args[0].equalsIgnoreCase("removeslot")) {

            ItemStack item =
                    player.getInventory()
                            .getItemInMainHand();

            if (item == null ||
                    item.getType().isAir()) {

                player.sendMessage(
                        "§cHãy cầm item cần xoá slot."
                );

                return true;
            }

            if (!itemManager.removeEquipmentSlot(item)) {

                player.sendMessage(
                        "§cKhông thể xoá equipment slot."
                );

                return true;
            }

            player.getInventory()
                    .setItemInMainHand(item);

            player.sendMessage(
                    "§aĐã xoá equipment slot khỏi item."
            );

            return true;
        }

        // ==================================================
        // /EQUIPMENT CLEAR
        // ==================================================

        if (args[0].equalsIgnoreCase("clear")) {

            equipmentManager.clear(
                    player.getUniqueId()
            );

            equipmentGUI.clear(
                    player.getUniqueId()
            );

            player.sendMessage(
                    "§aĐã xoá toàn bộ equipment đang trang bị."
            );

            return true;
        }

        sendHelp(player);
        return true;
    }

    // ==================================================
    // PARSE SLOT
    // ==================================================

    private EquipmentSlot parseSlot(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
                value
                        .toUpperCase(Locale.ROOT)
                        .replace("-", "")
                        .replace("_", "");

        for (EquipmentSlot slot :
                EquipmentSlot.values()) {

            String slotName =
                    slot.name()
                            .replace("_", "");

            if (slotName.equals(normalized)) {
                return slot;
            }
        }

        return null;
    }

    // ==================================================
    // SLOTS
    // ==================================================

    private void sendSlots(
            Player player
    ) {

        player.sendMessage(
                "§7Slots: §eMAIN_HAND, OFF_HAND, HELMET, CHESTPLATE, LEGGINGS, BOOTS"
        );

        player.sendMessage(
                "§7        §eBELT, GLOVES, JADE, RING1, RING2, EARRING, NECKLACE"
        );

        player.sendMessage(
                "§7        §eWINGS, BADGE, PET, MOUNT"
        );
    }

    // ==================================================
    // HELP
    // ==================================================

    private void sendHelp(
            Player player
    ) {

        player.sendMessage(
                "§8§m--------------------------"
        );

        player.sendMessage(
                "§6§lEQUIPMENT"
        );

        player.sendMessage(
                "§7/equipment"
        );

        player.sendMessage(
                "§7/equipment addslot <slot>"
        );

        player.sendMessage(
                "§7/equipment removeslot"
        );

        player.sendMessage(
                "§7/equipment clear"
        );

        player.sendMessage(
                "§8§m--------------------------"
        );
    }
}
