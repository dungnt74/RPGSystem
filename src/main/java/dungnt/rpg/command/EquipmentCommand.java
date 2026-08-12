package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.item.EquipmentManager;
import dungnt.rpg.item.RPGItem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class EquipmentCommand implements CommandExecutor {

    private final MyRPG plugin;
    private final EquipmentManager equipmentManager;

    public EquipmentCommand(MyRPG plugin) {

        this.plugin = plugin;

        this.equipmentManager =
                plugin.getEquipmentManager();
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

            showEquipment(player);

            return true;
        }

        // ==================================================
        // /EQUIPMENT CLEAR
        // ==================================================

        if (args[0].equalsIgnoreCase("clear")) {

            equipmentManager.clear(
                    player.getUniqueId()
            );

            player.sendMessage(
                    "§aĐã tháo toàn bộ trang bị."
            );

            return true;
        }

        // ==================================================
        // HELP
        // ==================================================

        player.sendMessage(
                "§cDùng:"
        );

        player.sendMessage(
                "§7/equipment"
        );

        player.sendMessage(
                "§7/equipment clear"
        );

        return true;
    }

    // ==================================================
    // SHOW EQUIPMENT
    // ==================================================

    private void showEquipment(
            Player player
    ) {

        Map<EquipmentSlot, RPGItem> equipment =
                equipmentManager.getEquipment(
                        player.getUniqueId()
                );

        player.sendMessage(
                "§8§m--------------------------------"
        );

        player.sendMessage(
                "§6§l✦ EQUIPMENT"
        );

        for (EquipmentSlot slot :
                EquipmentSlot.values()) {

            RPGItem item =
                    equipment.get(slot);

            if (item == null) {

                player.sendMessage(
                        "§7"
                                + getSlotName(slot)
                                + ": §8Trống"
                );

            } else {

                player.sendMessage(
                        "§7"
                                + getSlotName(slot)
                                + ": §a"
                                + item.getName()
                );
            }
        }

        player.sendMessage(
                "§8§m--------------------------------"
        );
    }

    // ==================================================
    // SLOT NAME
    // ==================================================

    private String getSlotName(
            EquipmentSlot slot
    ) {

        return switch (slot) {

            case MAIN_HAND ->
                    "Tay chính";

            case OFF_HAND ->
                    "Tay phụ";

            case HELMET ->
                    "Mũ";

            case CHESTPLATE ->
                    "Áo";

            case LEGGINGS ->
                    "Quần";

            case BOOTS ->
                    "Giày";

            case BELT ->
                    "Đai lưng";

            case GLOVES ->
                    "Găng tay";

            case JADE ->
                    "Ngọc bội";

            case RING ->
                    "Nhẫn";

            case EARRING ->
                    "Khuyên tai";

            case NECKLACE ->
                    "Vòng cổ";

            case WINGS ->
                    "Cánh";

            case BADGE ->
                    "Huy hiệu";

            case PET ->
                    "Thú cưng";

            case MOUNT ->
                    "Thú cưỡi";
        };
    }
}