package dungnt.socket;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.StatModifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.*;

import java.util.*;

public final class SocketListener implements Listener {
    private final MyRPG plugin;
    private final SocketGUI gui;
    private final Map<UUID,Long> upgradeCooldown=new HashMap<>();
    public SocketListener(MyRPG plugin,SocketGUI gui){this.plugin=plugin;this.gui=gui;}

    private SocketGUI.Holder holder(Inventory i){return i!=null&&i.getHolder() instanceof SocketGUI.Holder h?h:null;}

    @EventHandler public void join(PlayerJoinEvent e){syncPlayer(e.getPlayer());}
    @EventHandler public void held(PlayerItemHeldEvent e){Bukkit.getScheduler().runTask(plugin,()->syncPlayer(e.getPlayer()));}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void click(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        SocketGUI.Holder h=holder(e.getView().getTopInventory()); if(h==null)return;
        int raw=e.getRawSlot();
        if(raw<0)return;
        if(h.mode()==SocketGUI.Mode.MAIN){
            e.setCancelled(true);
            if(raw==10)gui.openInsert(p);
            else if(raw==16)gui.openUpgrade(p);
            return;
        }
        if(h.mode()==SocketGUI.Mode.INSERT){handleInsert(e,p);return;}
        if(h.mode()==SocketGUI.Mode.UPGRADE){handleUpgrade(e,p);return;}
    }

    private void handleInsert(InventoryClickEvent e,Player p){
        Inventory inv=e.getView().getTopInventory(); int raw=e.getRawSlot();
        if(raw>=inv.getSize())return;
        e.setCancelled(true);
        if(raw==49){returnItemAndBack(p,inv);return;}
        if(raw==13){takeInsertItem(p,inv);return;}
        if(raw==22){unlock(p,inv);return;}
        int socketIndex=socketIndex(raw);
        if(socketIndex>=0){socketClick(e,p,inv,socketIndex);return;}
        int breakIndex=breakIndex(raw);
        if(breakIndex>=0){breakGem(p,inv,breakIndex);}
    }
    private int socketIndex(int raw){int[] a={28,30,32,34};for(int i=0;i<4;i++)if(a[i]==raw)return i;return -1;}
    private int breakIndex(int raw){int[] a={37,39,41,43};for(int i=0;i<4;i++)if(a[i]==raw)return i;return -1;}

    private void takeInsertItem(Player p,Inventory inv){
        ItemStack current=inv.getItem(13); boolean empty=current==null||current.getType().isAir()||current.getType()==Material.RED_STAINED_GLASS_PANE||current.getType()==Material.GRAY_STAINED_GLASS_PANE; if(empty){ItemStack c=p.getItemOnCursor();if(c==null||c.getType().isAir()){p.sendMessage("§cĐặt item vào ô 13.");return;}if(!plugin.getItemManager().isRPGItem(c) || plugin.getGemManager().getSocketCount(c)<=0){p.sendMessage("§cItem phải là RPG item và đã có ít nhất 1 socket.");return;} inv.setItem(13,c.clone());p.setItemOnCursor(null);gui.refreshInsert(inv,inv.getItem(13));return;}
        ItemStack cursor=p.getItemOnCursor();if(cursor!=null&&!cursor.getType().isAir()){p.sendMessage("§cCursor phải trống.");return;}
        p.setItemOnCursor(current);inv.setItem(13,null);gui.refreshInsert(inv,null);
    }
    private void socketClick(InventoryClickEvent e,Player p,Inventory inv,int idx){
        ItemStack item=inv.getItem(13);if(item==null||item.getType().isAir()){p.sendMessage("§cHãy đặt item vào ô 13.");return;}
        GemManager gm=plugin.getGemManager();if(idx>=gm.getSocketCount(item)){p.sendMessage("§cSocket chưa mở.");return;}
        if(gm.getSocketGem(item,idx)!=null){p.sendMessage("§eSocket đã có gem. Dùng nút phá gem.");return;}
        ItemStack c=p.getItemOnCursor();if(!gm.isGem(c)){p.sendMessage("§cCursor phải là Gem.");return;}
        if(gm.socket(item,idx,c)){
            if(c.getAmount()>1){c=c.clone();c.setAmount(c.getAmount()-1);p.setItemOnCursor(c);}else p.setItemOnCursor(null);
            inv.setItem(13,item);
            syncStoredInsert(p,item);
            gui.refreshInsert(inv,item);
            p.sendMessage("§aĐã khảm gem vào socket "+(idx+1)+".");
        }
    }
    private void unlock(Player p,Inventory inv){
        ItemStack item=inv.getItem(13);if(item==null||item.getType().isAir()){p.sendMessage("§cĐặt item vào ô 13.");return;}
        GemManager gm=plugin.getGemManager();int count=gm.getSocketCount(item);
        if(count>=4){p.sendMessage("§eItem đã đủ 4 socket.");return;}
        if(count==0){p.sendMessage("§cSocket đầu tiên được kích hoạt bằng §f/socket addslot§c (OP).");return;}
        if(!takeNamed(p,"Đá Đục Lỗ")){p.sendMessage("§cCần 1  §fĐá Đục Lỗ§c để tiếp tục.");return;}
        gm.addSocket(item);inv.setItem(13,item);gui.refreshInsert(inv,item);p.sendMessage("§aĐã mở socket "+(count+1)+"/4.");
    }
    private void breakGem(Player p,Inventory inv,int idx){
        ItemStack item=inv.getItem(13);if(item==null||item.getType().isAir())return;
        GemManager gm=plugin.getGemManager();Gem g=gm.getSocketGem(item,idx);if(g==null){p.sendMessage("§7Socket này chưa có gem.");return;}
        if(!takeNamed(p,"Đá Phá Gem")){p.sendMessage("§cCần 1 §fĐá Phá Gem§c để tiếp tục.");return;}
        gm.unsocket(item,idx);ItemStack gem=gm.createGem(g.id(),g.name(),g.special());gm.setLevel(gem,g.level());if(g.stat()!=null)gm.setStat(gem,g.stat(),g.modifierType(),g.amount());p.getInventory().addItem(gem);
        inv.setItem(13,item);
        syncStoredInsert(p,item);
        gui.refreshInsert(inv,item);p.sendMessage("§aĐã phá gem.");
    }
    private void syncStoredInsert(Player p,ItemStack item){
        if(plugin.getEquipmentGUI()!=null){
            String id=plugin.getItemManager().getItemId(item);
            for(Map.Entry<Integer,ItemStack> en:plugin.getEquipmentGUI().getStoredItems(p.getUniqueId()).entrySet()){
                if(id!=null&&id.equals(plugin.getItemManager().getItemId(en.getValue()))){
                    plugin.getEquipmentGUI().setItem(p.getUniqueId(),en.getKey(),item);
                    syncPlayer(p);
                    return;
                }
            }
        }
    }

    private void returnItemAndBack(Player p,Inventory inv){ItemStack item=inv.getItem(13);if(item!=null&&!item.getType().isAir())p.getInventory().addItem(item);inv.setItem(13,null);gui.open(p);syncPlayer(p);}
    private boolean takeNamed(Player p,String needle){for(int x=0;x<p.getInventory().getSize();x++){ItemStack i=p.getInventory().getItem(x);if(i==null||i.getType().isAir()||!i.hasItemMeta()||!i.getItemMeta().hasDisplayName())continue;if(org.bukkit.ChatColor.stripColor(i.getItemMeta().getDisplayName()).toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT))){if(i.getAmount()==1)p.getInventory().setItem(x,null);else{i=i.clone();i.setAmount(i.getAmount()-1);p.getInventory().setItem(x,i);}return true;}}return false;}

    private void handleUpgrade(InventoryClickEvent e,Player p){
        Inventory inv=e.getView().getTopInventory();int raw=e.getRawSlot();
        if(raw==35){e.setCancelled(true);gui.open(p);return;}
        if(raw==22){e.setCancelled(true);upgrade(p,inv);return;}
        if(raw==11||raw==13||raw==15){e.setCancelled(true);gemInputClick(e,p,inv,raw);return;}
    }
    private void gemInputClick(InventoryClickEvent e,Player p,Inventory inv,int slot){
        ItemStack current=inv.getItem(slot), cursor=e.getCursor();
        boolean empty=current==null||current.getType().isAir()||current.getType()==Material.GRAY_STAINED_GLASS_PANE;
        if(empty){
            if(!plugin.getGemManager().isGem(cursor)){p.sendMessage("§cChỉ được đặt Gem.");return;}
            ItemStack x=cursor.clone();x.setAmount(1);inv.setItem(slot,x);
            if(cursor.getAmount()>1){cursor=cursor.clone();cursor.setAmount(cursor.getAmount()-1);e.setCursor(cursor);}else e.setCursor(null);
        }else{
            if(cursor!=null&&!cursor.getType().isAir()){p.sendMessage("§cCursor phải trống.");return;}
            e.setCursor(current);inv.setItem(slot,gui.button(Material.GRAY_STAINED_GLASS_PANE," "));
        }
    }

    private void upgrade(Player p,Inventory inv){
        long now=System.currentTimeMillis(),last=upgradeCooldown.getOrDefault(p.getUniqueId(),0L);
        if(now-last<3000){p.sendMessage("§cCooldown còn "+String.format("%.1f",(3000-(now-last))/1000.0)+"s.");return;}
        int[] slots={11,13,15};ItemStack[] gs=new ItemStack[3];for(int i=0;i<3;i++){gs[i]=inv.getItem(slots[i]);if(!plugin.getGemManager().isGem(gs[i])){p.sendMessage("§cCần 3 Gem.");return;}}
        Gem a=plugin.getGemManager().data(gs[0]),b=plugin.getGemManager().data(gs[1]),c=plugin.getGemManager().data(gs[2]);
        if(!a.id().equalsIgnoreCase(b.id())||!a.id().equalsIgnoreCase(c.id())||a.level()!=b.level()||a.level()!=c.level()){p.sendMessage("§c3 Gem phải cùng loại và cùng cấp.");return;}
        if(a.special()||a.level()>=5){p.sendMessage("§cGem này không thể nâng thêm.");return;}
        upgradeCooldown.put(p.getUniqueId(),now);
        double chance=switch(a.level()){case 1->.90;case 2->.50;case 3->.10;case 4->.01;default->0;};
        for(int s:slots){ItemStack x=inv.getItem(s);if(x.getAmount()>1){x=x.clone();x.setAmount(x.getAmount()-1);inv.setItem(s,x);}else inv.setItem(s,null);}
        if(Math.random()<=chance){
            ItemStack out=plugin.getGemManager().createGem(a.id(),a.name(),a.special());plugin.getGemManager().setLevel(out,a.level()+1);if(a.stat()!=null)plugin.getGemManager().setStat(out,a.stat(),a.modifierType(),a.amount());p.getInventory().addItem(out);p.sendMessage("§a§lNâng cấp thành công! §fGem cấp "+(a.level()+1)+".");
        }else p.sendMessage("§cNâng cấp thất bại, mất 3 Gem.");
    }
    @EventHandler(priority=EventPriority.HIGHEST)
    public void upgradeDrag(InventoryDragEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        SocketGUI.Holder h=holder(e.getView().getTopInventory());
        if(h==null||h.mode()!=SocketGUI.Mode.UPGRADE)return;
        Inventory inv=e.getView().getTopInventory(); int target=-1;
        for(int raw:e.getRawSlots()) if(raw==11||raw==13||raw==15){target=raw;break;}
        if(target<0)return; e.setCancelled(true);
        ItemStack c=e.getOldCursor(); if(!plugin.getGemManager().isGem(c)){p.sendMessage("§cChỉ được đặt Gem.");return;}
        ItemStack current=inv.getItem(target);
        if(current!=null&&!current.getType().isAir()&&current.getType()!=Material.GRAY_STAINED_GLASS_PANE)return;
        ItemStack x=c.clone();x.setAmount(1);inv.setItem(target,x);
        if(c.getAmount()>1){c=c.clone();c.setAmount(c.getAmount()-1);p.setItemOnCursor(c);}else p.setItemOnCursor(null);
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void drag(InventoryDragEvent e){
        if(!(e.getWhoClicked() instanceof Player p))return;
        SocketGUI.Holder h=holder(e.getView().getTopInventory());
        if(h==null||h.mode()!=SocketGUI.Mode.INSERT)return;
        Inventory inv=e.getView().getTopInventory();
        int target=-1;
        for(int raw:e.getRawSlots()) if(raw<inv.getSize()){target=raw;break;}
        if(target<0)return;
        if(target!=13 && socketIndex(target)<0)return;
        e.setCancelled(true);
        ItemStack cursor=e.getOldCursor();
        if(target==13){
            if(!plugin.getItemManager().isRPGItem(cursor)||plugin.getGemManager().getSocketCount(cursor)<=0){
                p.sendMessage("§cItem phải là RPG item và đã có ít nhất 1 socket."); return;
            }
            if(inv.getItem(13)!=null && !inv.getItem(13).getType().isAir()){p.sendMessage("§cÔ 13 đã có item.");return;}
            ItemStack x=cursor.clone();x.setAmount(1);inv.setItem(13,x);
            if(cursor.getAmount()>1){cursor=cursor.clone();cursor.setAmount(cursor.getAmount()-1);p.setItemOnCursor(cursor);}else p.setItemOnCursor(null);
            gui.refreshInsert(inv,x);
        } else {
            int idx=socketIndex(target); ItemStack item=inv.getItem(13);
            if(item==null||item.getType().isAir()){p.sendMessage("§cĐặt item vào ô 13 trước.");return;}
            if(idx>=plugin.getGemManager().getSocketCount(item)){p.sendMessage("§cSocket chưa mở.");return;}
            if(plugin.getGemManager().getSocketGem(item,idx)!=null){p.sendMessage("§cSocket đã có gem.");return;}
            if(!plugin.getGemManager().isGem(cursor)){p.sendMessage("§cCursor phải là Gem.");return;}
            plugin.getGemManager().socket(item,idx,cursor);inv.setItem(13,item);
            if(cursor.getAmount()>1){cursor=cursor.clone();cursor.setAmount(cursor.getAmount()-1);p.setItemOnCursor(cursor);}else p.setItemOnCursor(null);
            gui.refreshInsert(inv,item);
        }
    }

    @EventHandler public void close(InventoryCloseEvent e){
        if(!(e.getPlayer() instanceof Player p))return;SocketGUI.Holder h=holder(e.getInventory());if(h==null)return;
        if(h.mode()==SocketGUI.Mode.INSERT){ItemStack item=e.getInventory().getItem(13);if(item!=null&&!item.getType().isAir()&&item.getType()!=Material.RED_STAINED_GLASS_PANE&&item.getType()!=Material.GRAY_STAINED_GLASS_PANE)p.getInventory().addItem(item);e.getInventory().setItem(13,null);syncPlayer(p);}
        else if(h.mode()==SocketGUI.Mode.UPGRADE){for(int s:new int[]{11,13,15}){ItemStack i=e.getInventory().getItem(s);if(i!=null&&!i.getType().isAir()&&i.getType()!=Material.GRAY_STAINED_GLASS_PANE)p.getInventory().addItem(i);}}
    }

    public void syncPlayer(Player p){
        if(p==null||!p.isOnline())return;
        // Remove known socket modifiers, then rebuild from RPG main-hand and virtual equipment.
        plugin.getStatManager().getModifiers(p.getUniqueId()).keySet().stream().filter(k->k.startsWith("socket_")).toList().forEach(k->plugin.getStatManager().removeModifier(p.getUniqueId(),k));
        ItemStack main=p.getInventory().getItemInMainHand();if(main!=null&&!main.getType().isAir())plugin.getGemManager().applySocketStats(p.getUniqueId(),main);
        if(plugin.getEquipmentGUI()!=null)for(ItemStack i:plugin.getEquipmentGUI().getStoredItems(p.getUniqueId()).values())plugin.getGemManager().applySocketStats(p.getUniqueId(),i);
    }
}
