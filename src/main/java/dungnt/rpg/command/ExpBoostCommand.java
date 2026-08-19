package dungnt.rpg.command;

import dungnt.rpg.expboost.ExpBoostGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpBoostCommand implements CommandExecutor {

    private final ExpBoostGUI gui;

    public ExpBoostCommand(ExpBoostGUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cLệnh này chỉ dùng được trong game.");
            return true;
        }

        gui.open(player);
        return true;
    }
}
