package dungnt.socket;

import dungnt.rpg.MyRPG;
import dungnt.rpg.item.EquipmentSlot;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatType;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public final class SocketCommand implements CommandExecutor {
    private final MyRPG plugin;
    public SocketCommand(MyRPG plugin){this.plugin=plugin;}

    @Override public boolean onCommand(CommandSender sender,Command cmd,String label,String[] a){
        if(!(sender instanceof Player p)){sender.sendMessage("Player only.");return true;}
        if(cmd.getName().equalsIgnoreCase("socket"))return socket(p,a);
        return itemsocket(p,a);
    }
    private boolean socket(Player p,String[] a){
        if(a.length==0){plugin.getSocketGUI().open(p);return true;}
        if(a[0].equalsIgnoreCase("addslot")){
            if(!p.isOp()){p.sendMessage("§cBạn cần OP.");return true;}
            ItemStack item=p.getInventory().getItemInMainHand();
            if(item.getType().isAir()){p.sendMessage("§cHãy cầm item.");return true;}
            if(!plugin.getItemManager().ensureRPGItem(item)){p.sendMessage("§cKhông thể đánh dấu RPG item.");return true;} EquipmentSlot eq=plugin.getItemManager().getEquipmentSlot(item); if(eq!=EquipmentSlot.MAIN_HAND && eq!=EquipmentSlot.OFF_HAND){p.sendMessage("§c/socket addslot chỉ áp dụng cho vũ khí MAIN_HAND/OFF_HAND.");return true;}
            int n=plugin.getGemManager().getSocketCount(item);
            if(n>=4){p.sendMessage("§eItem đã đủ 4 socket.");return true;}
            plugin.getGemManager().addSocket(item);
            p.getInventory().setItemInMainHand(item);
            p.sendMessage("§aĐã kích hoạt socket "+(n+1)+"/4.");
            return true;
        }
        p.sendMessage("§7/socket §f| §7/socket addslot");
        return true;
    }
    private boolean itemsocket(Player p,String[] a){
        if(a.length==0){help(p);return true;}
        GemManager gm=plugin.getGemManager();
        if(a[0].equalsIgnoreCase("name")){
            if(a.length<3){p.sendMessage("§c/itemsocket name <id> <tên>");return true;}
            String id=a[1];String name=join(a,2);
            ItemStack g=gm.createGem(id,name,false);p.getInventory().addItem(g);p.sendMessage("§aĐã tạo Gem §f"+id+".");return true;
        }
        if(a[0].equalsIgnoreCase("special")){
            if(a.length<3){p.sendMessage("§c/itemsocket special <id> <tên>");return true;}
            ItemStack g=gm.createGem(a[1],join(a,2),true);p.getInventory().addItem(g);p.sendMessage("§aĐã tạo Gem boss đặc biệt.");return true;
        }
        ItemStack held=p.getInventory().getItemInMainHand();
        if(!gm.isGem(held)){p.sendMessage("§cHãy cầm Gem.");return true;}
        if(a[0].equalsIgnoreCase("addstat")){
            if(a.length<4){p.sendMessage("§c/itemsocket addstat <stat> <flat|percent> <amount>");return true;}
            try{
                StatType st=StatType.valueOf(a[1].toUpperCase(Locale.ROOT));ModifierType mt=ModifierType.valueOf(a[2].toUpperCase(Locale.ROOT));double amount=Double.parseDouble(a[3]);
                gm.setStat(held,st,mt,amount);p.getInventory().setItemInMainHand(held);p.sendMessage("§aĐã thêm stat.");
            }catch(Exception ex){p.sendMessage("§cStat/modifier/amount không hợp lệ.");}
            return true;
        }
        if(a[0].equalsIgnoreCase("lore")&&a.length>=3){
            if(a[1].equalsIgnoreCase("add")){gm.addLore(held,join(a,2));p.sendMessage("§aĐã thêm lore.");return true;}
            if(a[1].equalsIgnoreCase("remove")){p.sendMessage(gm.removeLore(held,join(a,2))?"§aĐã xoá lore.":"§cKhông tìm thấy lore.");return true;}
        }
        if(a[0].equalsIgnoreCase("setlevel")&&a.length>=2){
            try{int lv=Integer.parseInt(a[1]);Gem g=gm.data(held);if(g.special()&&lv>1||lv<1||lv>5){p.sendMessage("§cLevel không hợp lệ.");return true;}gm.setLevel(held,lv);p.getInventory().setItemInMainHand(held);p.sendMessage("§aĐã set level.");}catch(Exception ex){p.sendMessage("§cLevel không hợp lệ.");}return true;
        }
        help(p);return true;
    }
    private String join(String[] a,int start){StringBuilder s=new StringBuilder();for(int i=start;i<a.length;i++){if(i>start)s.append(" ");s.append(a[i]);}return ChatColor.translateAlternateColorCodes('&',s.toString());}
    private void help(Player p){p.sendMessage("§6/itemsocket name <id> <name>");p.sendMessage("§6/itemsocket special <id> <name>");p.sendMessage("§6/itemsocket addstat <stat> <flat|percent> <amount>");p.sendMessage("§6/itemsocket lore <add|remove> <text>");p.sendMessage("§6/itemsocket setlevel <1-5>");}
}
