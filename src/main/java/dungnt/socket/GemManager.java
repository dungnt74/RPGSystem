package dungnt.socket;

import dungnt.rpg.MyRPG;
import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatModifier;
import dungnt.rpg.stats.StatType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class GemManager {
    public static final int MAX_LEVEL = 5;
    private final MyRPG plugin;
    private final NamespacedKey gemKey, levelKey, specialKey, statKey, typeKey, amountKey, modifierKey, socketCountKey, socketPrefixKey;

    public GemManager(MyRPG plugin) {
        this.plugin=plugin;
        gemKey=key("gem");
        levelKey=key("gem_level");
        specialKey=key("gem_special");
        statKey=key("gem_stat");
        typeKey=key("gem_type");
        amountKey=key("gem_amount");
        modifierKey=key("gem_modifier");
        socketCountKey=key("socket_count");
        socketPrefixKey=key("socket_");
    }
    private NamespacedKey key(String s){ return new NamespacedKey(plugin,s); }

    public ItemStack createGem(String id,String name, boolean special) {
        ItemStack i=new ItemStack(Material.EMERALD);
        ItemMeta m=i.getItemMeta();
        m.setDisplayName(name);
        PersistentDataContainer p=m.getPersistentDataContainer();
        p.set(gemKey,PersistentDataType.BYTE,(byte)1);
        p.set(levelKey,PersistentDataType.INTEGER,1);
        p.set(specialKey,PersistentDataType.BYTE,(byte)(special?1:0));
        p.set(typeKey,PersistentDataType.STRING,id);
        p.set(amountKey,PersistentDataType.DOUBLE,0.0);
        p.set(modifierKey,PersistentDataType.STRING,ModifierType.FLAT.name());
        writeLore(i,m);
        return i;
    }

    public boolean isGem(ItemStack i){ return data(i)!=null; }
    public Gem data(ItemStack i){
        if(i==null||i.getType().isAir()||i.getItemMeta()==null)return null;
        PersistentDataContainer p=i.getItemMeta().getPersistentDataContainer();
        Byte b=p.get(gemKey,PersistentDataType.BYTE);
        String id=p.get(typeKey,PersistentDataType.STRING);
        Integer lv=p.get(levelKey,PersistentDataType.INTEGER);
        String st=p.get(statKey,PersistentDataType.STRING);
        Double amt=p.get(amountKey,PersistentDataType.DOUBLE);
        String mt=p.get(modifierKey,PersistentDataType.STRING);
        Byte sp=p.get(specialKey,PersistentDataType.BYTE);
        if(b==null||b!=1||id==null||lv==null)return null;
        StatType stat=null; ModifierType mod=ModifierType.FLAT;
        try { if(st!=null)stat=StatType.valueOf(st); if(mt!=null)mod=ModifierType.valueOf(mt); } catch(Exception ignored){}
        return new Gem(id, i.getItemMeta().getDisplayName(),lv,sp!=null&&sp==1,stat,mod,amt==null?0:amt);
    }
    public void setStat(ItemStack i,StatType stat,ModifierType mod,double amount){
        if(!isGem(i)||stat==null||mod==null)return;
        ItemMeta m=i.getItemMeta(); PersistentDataContainer p=m.getPersistentDataContainer();
        p.set(statKey,PersistentDataType.STRING,stat.name()); p.set(modifierKey,PersistentDataType.STRING,mod.name());
        p.set(amountKey,PersistentDataType.DOUBLE,amount); writeLore(i,m);
    }
    public boolean removeStat(ItemStack i, StatType stat) {
        if (!isGem(i) || stat == null) return false;
        ItemMeta m = i.getItemMeta();
        if (m == null) return false;
        PersistentDataContainer p = m.getPersistentDataContainer();
        boolean existed = p.has(statKey, PersistentDataType.STRING);
        p.remove(statKey);
        p.set(amountKey, PersistentDataType.DOUBLE, 0.0);
        p.set(modifierKey, PersistentDataType.STRING, ModifierType.FLAT.name());
        writeLore(i, m);
        return existed;
    }

    public boolean removeSocket(ItemStack i) {
        int count = getSocketCount(i);
        if (count <= 0) return false;
        int slot = count - 1;
        if (getSocketGem(i, slot) != null) return false;

        ItemMeta m = i.getItemMeta();
        if (m == null) return false;
        PersistentDataContainer p = m.getPersistentDataContainer();
        String prefix = "socket_" + slot + "_";
        for (String s : new String[]{"type","level","special","stat","amount","modifier"}) {
            p.remove(key(prefix + s));
        }
        p.set(socketCountKey, PersistentDataType.INTEGER, count - 1);
        i.setItemMeta(m);
        return true;
    }

    public void setLevel(ItemStack i,int level){ if(!isGem(i))return; ItemMeta m=i.getItemMeta(); m.getPersistentDataContainer().set(levelKey,PersistentDataType.INTEGER,level); writeLore(i,m); }
    public void setName(ItemStack i,String name){ ItemMeta m=i.getItemMeta(); m.setDisplayName(name); writeLore(i,m); }
    public void addLore(ItemStack i,String line){ ItemMeta m=i.getItemMeta(); List<String> l=m.getLore()==null?new ArrayList<>():new ArrayList<>(m.getLore()); l.add(line); m.setLore(l); i.setItemMeta(m); }
    public boolean removeLore(ItemStack i,String text){ ItemMeta m=i.getItemMeta(); List<String> l=m.getLore()==null?new ArrayList<>():new ArrayList<>(m.getLore()); boolean r=l.removeIf(x->x.contains(text)); m.setLore(l); i.setItemMeta(m); return r; }

    public int getSocketCount(ItemStack i){
        if(i==null||i.getItemMeta()==null)return 0;
        Integer n=i.getItemMeta().getPersistentDataContainer().get(socketCountKey,PersistentDataType.INTEGER);
        return n==null?0:Math.max(0,Math.min(4,n));
    }
    public boolean addSocket(ItemStack i){
        if(i==null||i.getType().isAir()||i.getItemMeta()==null||getSocketCount(i)>=4)return false;
        ItemMeta m=i.getItemMeta(); m.getPersistentDataContainer().set(socketCountKey,PersistentDataType.INTEGER,getSocketCount(i)+1); i.setItemMeta(m); return true;
    }
    public Gem getSocketGem(ItemStack item,int slot){
        if(item==null||item.getItemMeta()==null||slot<0||slot>=4)return null;
        PersistentDataContainer p=item.getItemMeta().getPersistentDataContainer();
        String type=p.get(key("socket_"+slot+"_type"),PersistentDataType.STRING);
        Integer lv=p.get(key("socket_"+slot+"_level"),PersistentDataType.INTEGER);
        if(type==null||lv==null)return null;
        String st=p.get(key("socket_"+slot+"_stat"),PersistentDataType.STRING);
        Double amt=p.get(key("socket_"+slot+"_amount"),PersistentDataType.DOUBLE);
        String mt=p.get(key("socket_"+slot+"_modifier"),PersistentDataType.STRING);
        Byte sp=p.get(key("socket_"+slot+"_special"),PersistentDataType.BYTE);
        StatType stat=null; ModifierType mod=ModifierType.FLAT;
        try {if(st!=null)stat=StatType.valueOf(st); if(mt!=null)mod=ModifierType.valueOf(mt);}catch(Exception ignored){}
        return new Gem(type,type,lv,sp!=null&&sp==1,stat,mod,amt==null?0:amt);
    }
    public boolean socket(ItemStack item,int slot,ItemStack gem){
        Gem g=data(gem); if(g==null||slot<0||slot>=getSocketCount(item))return false;
        ItemMeta m=item.getItemMeta(); PersistentDataContainer p=m.getPersistentDataContainer(); String x="socket_"+slot+"_";
        p.set(key(x+"type"),PersistentDataType.STRING,g.id()); p.set(key(x+"level"),PersistentDataType.INTEGER,g.level());
        p.set(key(x+"special"),PersistentDataType.BYTE,(byte)(g.special()?1:0));
        if(g.stat()!=null)p.set(key(x+"stat"),PersistentDataType.STRING,g.stat().name());
        p.set(key(x+"amount"),PersistentDataType.DOUBLE,g.amount()); p.set(key(x+"modifier"),PersistentDataType.STRING,g.modifierType().name());
        item.setItemMeta(m); return true;
    }
    public Gem unsocket(ItemStack item,int slot){
        Gem g=getSocketGem(item,slot); if(g==null)return null; ItemMeta m=item.getItemMeta(); PersistentDataContainer p=m.getPersistentDataContainer(); String x="socket_"+slot+"_";
        for(String s:new String[]{"type","level","special","stat","amount","modifier"})p.remove(key(x+s)); item.setItemMeta(m); return g;
    }
    public StatModifier toModifier(ItemStack item,int slot){
        Gem g=getSocketGem(item,slot); if(g==null||g.stat()==null)return null;
        double amount=g.amount()*g.level();
        return new StatModifier("socket_"+itemIdentity(item)+"_"+slot,g.stat(),g.modifierType(),amount);
    }
    public String itemIdentity(ItemStack i){
        if(i==null||i.getItemMeta()==null)return UUID.randomUUID().toString();
        String id=i.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin,"rpg_item_id"),PersistentDataType.STRING);
        return id==null?Integer.toHexString(i.hashCode()):id;
    }
    public void applySocketStats(UUID uuid,ItemStack item){
        for(int s=0;s<4;s++){StatModifier m=toModifier(item,s); if(m!=null)plugin.getStatManager().addModifier(uuid,m);}
    }
    public void removeSocketStats(UUID uuid,ItemStack item){
        for(int s=0;s<4;s++){StatModifier m=toModifier(item,s); if(m!=null)plugin.getStatManager().removeModifier(uuid,m.getId());}
    }
    private void writeLore(ItemStack i,ItemMeta m){
        Gem g=dataRaw(m); List<String> l=m.getLore()==null?new ArrayList<>():new ArrayList<>(m.getLore());
        l.removeIf(x->x.startsWith("§8Gem:")||x.startsWith("§7Level:"));
        if(g!=null){l.add("§8Gem:"+g.id());l.add("§7Level: "+g.level());}
        i.setItemMeta(m);
    }
    private Gem dataRaw(ItemMeta m){
        PersistentDataContainer p=m.getPersistentDataContainer(); String id=p.get(typeKey,PersistentDataType.STRING); Integer lv=p.get(levelKey,PersistentDataType.INTEGER);
        if(id==null||lv==null)return null; String st=p.get(statKey,PersistentDataType.STRING),mt=p.get(modifierKey,PersistentDataType.STRING);Double a=p.get(amountKey,PersistentDataType.DOUBLE);Byte sp=p.get(specialKey,PersistentDataType.BYTE);
        StatType stat=null;ModifierType mod=ModifierType.FLAT;try{if(st!=null)stat=StatType.valueOf(st);if(mt!=null)mod=ModifierType.valueOf(mt);}catch(Exception ignored){}
        return new Gem(id,id,lv,sp!=null&&sp==1,stat,mod,a==null?0:a);
    }
    public void copyGemData(ItemStack from,ItemStack to){
        ItemMeta fm=from.getItemMeta(),tm=to.getItemMeta(); if(fm==null||tm==null)return;
        for(String n:new String[]{"gem","gem_level","gem_special","gem_stat","gem_type","gem_amount","gem_modifier"}){
            NamespacedKey k=key(n);
            if(fm.getPersistentDataContainer().has(k,PersistentDataType.STRING))tm.getPersistentDataContainer().set(k,PersistentDataType.STRING,fm.getPersistentDataContainer().get(k,PersistentDataType.STRING));
            if(fm.getPersistentDataContainer().has(k,PersistentDataType.INTEGER))tm.getPersistentDataContainer().set(k,PersistentDataType.INTEGER,fm.getPersistentDataContainer().get(k,PersistentDataType.INTEGER));
            if(fm.getPersistentDataContainer().has(k,PersistentDataType.BYTE))tm.getPersistentDataContainer().set(k,PersistentDataType.BYTE,fm.getPersistentDataContainer().get(k,PersistentDataType.BYTE));
            if(fm.getPersistentDataContainer().has(k,PersistentDataType.DOUBLE))tm.getPersistentDataContainer().set(k,PersistentDataType.DOUBLE,fm.getPersistentDataContainer().get(k,PersistentDataType.DOUBLE));
        }
        tm.setDisplayName(fm.getDisplayName());tm.setLore(fm.getLore());to.setItemMeta(tm);
    }
}
