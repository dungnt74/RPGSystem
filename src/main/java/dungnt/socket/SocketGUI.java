package dungnt.socket;

import dungnt.rpg.MyRPG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public final class SocketGUI {
    public static final String TITLE="§8§lSocket";
    public static final String INSERT_TITLE="§8§lKhảm Gem";
    public static final String UPGRADE_TITLE="§8§lNâng cấp Gem";
    private final MyRPG plugin;
    public SocketGUI(MyRPG plugin){this.plugin=plugin;}

    public void open(Player p){
        Inventory inv=Bukkit.createInventory(new Holder(Mode.MAIN,p.getUniqueId()),27,TITLE);
        fill(inv);
        inv.setItem(10,button(Material.EMERALD,"§a§lKhảm Gem","§7Mở giao diện khảm gem."));
        inv.setItem(13,button(Material.BOOK,"§f§lHướng dẫn","§7§fKhảm Gem: đặt item rồi đưa gem vào socket.","§7§fMở socket: dùng Đá Đục Lỗ.","§7§fPhá gem: dùng Đá Phá Gem.","§7§fTối đa 4 socket."));
        inv.setItem(16,button(Material.ANVIL,"§e§lNâng cấp Gem","§7Ghép 3 gem cùng loại/cấp."));
        p.openInventory(inv);
    }
    public void openInsert(Player p){
        Inventory inv=Bukkit.createInventory(new Holder(Mode.INSERT,p.getUniqueId()),54,INSERT_TITLE);
        fill(inv);
        p.openInventory(inv); refreshInsert(inv,null);
    }
    public void openUpgrade(Player p){
        Inventory inv=Bukkit.createInventory(new Holder(Mode.UPGRADE,p.getUniqueId()),36,UPGRADE_TITLE);
        fill(inv);
        inv.setItem(22,button(Material.ANVIL,"§e§lNâng cấp","§7Cần 3 gem giống loại và cùng cấp.","§7Cooldown: 3 giây."));
        inv.setItem(35,button(Material.BARRIER,"§cQuay lại","§7Về /socket."));
        p.openInventory(inv);
    }
    public void refreshInsert(Inventory inv, ItemStack item){
        for(int s:new int[]{28,30,32,34}) inv.setItem(s,button(Material.ORANGE_STAINED_GLASS_PANE,"§6§lSocket chưa mở","§7Cần mở socket trước."));
        if(item==null){ inv.setItem(13,button(Material.RED_STAINED_GLASS_PANE,"§c§lĐặt vật phẩm","§7Item phải có ít nhất 1 socket.")); inv.setItem(22,button(Material.ANVIL,"§e§lMở socket","§7Cần item đã có socket đầu tiên.")); for(int x=0;x<4;x++) inv.setItem(37+x*2,button(Material.OAK_BUTTON,"§c§lPhá Gem "+(x+1),"§7Cần §fĐá Phá Gem")); inv.setItem(49,button(Material.BARRIER,"§cQuay lại","§7Về /socket.")); return;}
        GemManager gm=plugin.getGemManager();
        for(int x=0;x<4;x++){
            int s=28+x*2; if(x>=gm.getSocketCount(item))continue;
            Gem g=gm.getSocketGem(item,x);
            inv.setItem(s,g==null?button(Material.EMERALD,"§a§lSocket "+(x+1),"§7Chưa khảm gem."):gemIcon(g,x));
        }
        inv.setItem(13,item);
        inv.setItem(22,button(Material.ANVIL,"§e§lMở socket","§7Dùng §fĐá Đục Lỗ §7để mở.","§7Đã mở: "+gm.getSocketCount(item)+"/4"));
        for(int x=0;x<4;x++) inv.setItem(37+x*2,button(Material.OAK_BUTTON,"§cPhá Gem "+(x+1),"§7Cần §fĐá Phá Gem"));
        inv.setItem(49,button(Material.BARRIER,"§cQuay lại","§7Về /socket."));
    }
    private ItemStack gemIcon(Gem g,int x){
        ItemStack i=button(Material.EMERALD,"§a§lSocket "+(x+1),"§7Gem: §f"+g.id(),"§7Level: §e"+g.level(),"§7Stat: §f"+(g.stat()==null?"none":g.stat().name()),"§7Amount: §f"+g.amount());
        return i;
    }
    private void fill(Inventory i){ItemStack g=button(Material.GRAY_STAINED_GLASS_PANE," ");for(int x=0;x<i.getSize();x++)i.setItem(x,g.clone());}
    public ItemStack button(Material m,String name,String... lore){ItemStack i=new ItemStack(m);ItemMeta meta=i.getItemMeta();if(meta!=null){meta.setDisplayName(name);meta.setLore(java.util.Arrays.asList(lore));i.setItemMeta(meta);}return i;}
    public static final class Holder implements InventoryHolder {
        final Mode mode; final UUID uuid; Holder(Mode mode,UUID uuid){this.mode=mode;this.uuid=uuid;}
        public Mode mode(){return mode;} public UUID uuid(){return uuid;}
        @Override public Inventory getInventory(){return null;}
    }
    public enum Mode { MAIN,INSERT,UPGRADE }
}
