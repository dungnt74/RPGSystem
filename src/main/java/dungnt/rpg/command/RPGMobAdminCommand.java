package dungnt.rpg.command;

import dungnt.rpg.MyRPG;
import dungnt.rpg.mob.MobData;
import dungnt.rpg.mob.MobDefinition;
import dungnt.rpg.mob.MobStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

/**
 * /rpg mob ...  (giống style MythicMobs /mm mobs ...)
 *
 *   /rpg mob list
 *   /rpg mob spawn <id> [amount]
 *   /rpg mob create <id> <entityType> <health> <attack> <defense> [magicDefense]
 *   /rpg mob delete <id>
 */
public final class RPGMobAdminCommand {

    private final MyRPG plugin;

    public RPGMobAdminCommand(MyRPG plugin) {
        this.plugin = plugin;
    }

    public boolean handle(CommandSender sender, String[] args) {

        if (!sender.hasPermission("dungntrpg.admin")) {
            sender.sendMessage("§cBạn không có quyền dùng lệnh này.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        String action = args[0].toLowerCase(Locale.ROOT);

        switch (action) {
            case "list" -> list(sender);
            case "spawn" -> spawn(sender, args);
            case "egg" -> egg(sender, args);
            case "create" -> create(sender, args);
            case "delete", "remove" -> delete(sender, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§8§m--------------------------------");
        sender.sendMessage("§6§lDungNT RPG - Mob");
        sender.sendMessage("§e/rpg mob list §7- Liệt kê toàn bộ mob đã định nghĩa");
        sender.sendMessage("§e/rpg mob spawn <id> [amount] §7- Spawn mob tại vị trí đứng");
        sender.sendMessage("§e/rpg mob egg <id> §7- Tạo spawn egg cho mob");
        sender.sendMessage("§e/rpg mob create <id> <entityType> <health> <attack> <defense> [magicDef] §7- Tạo mob mới");
        sender.sendMessage("§e/rpg mob delete <id> §7- Xoá định nghĩa mob (chỉ mob tạo bằng /rpg mob create)");
        sender.sendMessage("§8§m--------------------------------");
    }

    private void list(CommandSender sender) {
        var defs = plugin.getMobDefinitionManager().getDefinitions();

        if (defs.isEmpty()) {
            sender.sendMessage("§7Chưa có mob nào được định nghĩa.");
            return;
        }

        sender.sendMessage("§6§lDANH SÁCH MOB §7(" + defs.size() + ")");
        for (MobDefinition def : defs.values()) {
            sender.sendMessage(
                    "§7- §e" + def.getId()
                            + " §7(" + def.getEntityType() + ") §fHP:" + def.getMaxHealth()
                            + " §cATK:" + def.getAttack()
                            + " §bDEF:" + def.getDefense()
            );
        }
    }

    private void spawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cLệnh này chỉ dùng được trong game.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage("§eCú pháp: /rpg mob spawn <id> [amount]");
            return;
        }

        MobDefinition def = plugin.getMobDefinitionManager().get(args[1]);
        if (def == null) {
            sender.sendMessage("§cKhông tìm thấy mob: §e" + args[1]);
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Math.max(1, Math.min(50, Integer.parseInt(args[2])));
            } catch (NumberFormatException ignored) {
                sender.sendMessage("§eSố lượng không hợp lệ, spawn 1.");
            }
        }

        for (int i = 0; i < amount; i++) {
            spawnOne(player, def);
        }

        sender.sendMessage("§a§l✔ Đã spawn §e" + amount + "x " + def.getId());
    }

    private void egg(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cLệnh này chỉ dùng được trong game.");
            return;
        }
        if (args.length < 2) {
            sender.sendMessage("§e/rpg mob egg <id>");
            return;
        }
        MobDefinition def = plugin.getMobDefinitionManager().get(args[1]);
        if (def == null) {
            sender.sendMessage("§cKhông tìm thấy mob: §e" + args[1]);
            return;
        }
        Material eggMaterial = Material.matchMaterial(def.getEntityType().name() + "_SPAWN_EGG");
        if (eggMaterial == null) {
            sender.sendMessage("§cMob này không có Spawn Egg vanilla.");
            return;
        }
        ItemStack egg = new ItemStack(eggMaterial);
        ItemMeta meta = egg.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', def.getDisplayName() + " Egg"));
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "rpg_mob_id"),
                    PersistentDataType.STRING,
                    def.getId()
            );
            egg.setItemMeta(meta);
        }
        player.getInventory().addItem(egg);
        player.sendMessage("§aĐã nhận Spawn Egg của mob §e" + def.getId());
    }

    private void spawnOne(Player player, MobDefinition def) {

        Location location = player.getLocation();

        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(location, def.getEntityType());

        MobStats stats = def.toStats();

        MobData mobData = new MobData(entity.getUniqueId(), def.getId(), stats);

        plugin.getMobManager().register(entity, mobData);
        plugin.getMobStatsManager().setStats(entity.getUniqueId(), stats);

        entity.setMaxHealth(stats.getMaxHealth());
        entity.setHealth(stats.getMaxHealth());

        if (def.getDisplayName() != null && !def.getDisplayName().isBlank()) {
            entity.setCustomName(def.getDisplayName());
            entity.setCustomNameVisible(true);
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 6) {
            sender.sendMessage("§eCú pháp: /rpg mob create <id> <entityType> <health> <attack> <defense> [magicDef]");
            return;
        }

        String id = args[1].toLowerCase(Locale.ROOT);
        if (plugin.getMobDefinitionManager().exists(id)) {
            sender.sendMessage("§cMob '" + id + "' đã tồn tại.");
            return;
        }

        EntityType type;
        try {
            type = EntityType.valueOf(args[2].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            sender.sendMessage("§cEntityType không hợp lệ: " + args[2]);
            return;
        }

        if (!type.isAlive()) {
            sender.sendMessage("§cEntityType phải là sinh vật sống (LivingEntity), ví dụ ZOMBIE, SKELETON, WITHER_SKELETON...");
            return;
        }

        double health;
        double attack;
        double defense;
        double magicDefense = 0.0;

        try {
            health = Double.parseDouble(args[3]);
            attack = Double.parseDouble(args[4]);
            defense = Double.parseDouble(args[5]);
            if (args.length >= 7) {
                magicDefense = Double.parseDouble(args[6]);
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cHealth/Attack/Defense phải là số.");
            return;
        }

        MobDefinition def = new MobDefinition(id, type);
        def.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&f" + id));
        def.setMaxHealth(health);
        def.setAttack(attack);
        def.setDefense(defense);
        def.setMagicDefense(magicDefense);

        plugin.getMobDefinitionManager().register(def);
        plugin.getRPGConfigManager().saveGeneratedMob(def);

        sender.sendMessage("§a§l✔ Đã tạo mob §e" + id + " §a(" + type + ", HP:" + health + ", ATK:" + attack + ", DEF:" + defense + ")");
        sender.sendMessage("§7Đã lưu vào Mobs/generated.yml — dùng /rpg mob spawn " + id + " để thử.");
    }

    private void delete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§eCú pháp: /rpg mob delete <id>");
            return;
        }

        String id = args[1].toLowerCase(Locale.ROOT);
        if (!plugin.getMobDefinitionManager().exists(id)) {
            sender.sendMessage("§cKhông tìm thấy mob: §e" + id);
            return;
        }

        plugin.getMobDefinitionManager().unregister(id);
        plugin.getRPGConfigManager().deleteGeneratedMob(id);

        sender.sendMessage("§a§l✔ Đã xoá định nghĩa mob §e" + id);
    }
}