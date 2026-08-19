package dungnt.team;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public final class TeamManager implements Listener {
    private final Map<UUID, Team> teams = new HashMap<>();
    private final Map<UUID, UUID> invites = new HashMap<>();

    public Team getTeam(UUID uuid) {
        for (Team t : teams.values()) if (t.contains(uuid)) return t;
        return null;
    }

    public Team getOrCreate(UUID leader) {
        Team t = getTeam(leader);
        if (t != null) return t;
        t = new Team(leader);
        teams.put(leader, t);
        return t;
    }

    /**
     * /team create <tên>
     *
     * Trả về null nếu player đã có team rồi, hoặc tên team đã bị
     * người khác dùng.
     */
    public Team create(Player leader, String name) {
        if (getTeam(leader.getUniqueId()) != null) return null;
        if (name == null || name.isBlank()) return null;
        if (findByName(name) != null) return null;

        Team t = new Team(leader.getUniqueId());
        t.setName(name);
        teams.put(leader.getUniqueId(), t);
        return t;
    }

    /**
     * /team delete — chỉ leader mới xoá được. Trả về false nếu
     * player không phải leader của team nào.
     */
    public boolean delete(Player leader) {
        Team t = getTeam(leader.getUniqueId());
        if (t == null || !t.getLeader().equals(leader.getUniqueId())) return false;
        disband(t);
        return true;
    }

    public Team findByName(String name) {
        if (name == null) return null;
        for (Team t : teams.values()) {
            if (name.equalsIgnoreCase(t.getName())) return t;
        }
        return null;
    }

    public boolean invite(Player leader, Player target) {
        Team t = getTeam(leader.getUniqueId());
        if (t == null) t = getOrCreate(leader.getUniqueId());
        if (!t.getLeader().equals(leader.getUniqueId()) || t.size() >= 4) return false;
        if (getTeam(target.getUniqueId()) != null) return false;
        invites.put(target.getUniqueId(), leader.getUniqueId());
        target.sendMessage("§aBạn được §e" + leader.getName() + " §amời vào team. §7/team accept");
        return true;
    }

    public boolean accept(Player player) {
        UUID leaderId = invites.remove(player.getUniqueId());
        if (leaderId == null) return false;
        Team t = teams.get(leaderId);
        if (t == null || t.size() >= 4 || getTeam(player.getUniqueId()) != null) return false;
        t.add(player.getUniqueId());
        return true;
    }

    public boolean kick(Player leader, Player target) {
        Team t = getTeam(leader.getUniqueId());
        if (t == null || !t.getLeader().equals(leader.getUniqueId()) || !t.contains(target.getUniqueId())
                || target.getUniqueId().equals(leader.getUniqueId())) return false;
        t.remove(target.getUniqueId());
        target.sendMessage("§cBạn đã bị đá khỏi team.");
        if (t.size() == 1) teams.remove(t.getLeader());
        return true;
    }

    public boolean leave(Player player) {
        Team t = getTeam(player.getUniqueId());
        if (t == null || t.getLeader().equals(player.getUniqueId())) return false;
        t.remove(player.getUniqueId());
        return true;
    }

    public void disband(Team t) {
        if (t == null) return;
        for (UUID member : t.getMembers()) {
            Player p = Bukkit.getPlayer(member);
            if (p != null) p.sendMessage("§cTeam đã giải tán.");
        }
        teams.remove(t.getLeader());
    }

    public String info(UUID uuid) {
        Team t = getTeam(uuid);
        if (t == null) return "§7Bạn chưa có team.";
        StringBuilder s = new StringBuilder("§6§l=== TEAM ===\n");
        if (t.getName() != null && !t.getName().isBlank()) {
            s.append("§eTên: §f").append(t.getName()).append("\n");
        }
        s.append("§eLeader: §f").append(name(t.getLeader())).append("\n");
        s.append("§7Thành viên (").append(t.size()).append("/4):\n");
        for (UUID id : t.getMembers()) {
            Player p = Bukkit.getPlayer(id);
            s.append("§f- ").append(name(id));
            if (p != null) s.append(" §8[").append(p.getWorld().getName()).append(" ")
                    .append(p.getLocation().getBlockX()).append(", ")
                    .append(p.getLocation().getBlockY()).append(", ")
                    .append(p.getLocation().getBlockZ()).append("]");
            else s.append(" §7[offline]");
            s.append("\n");
        }
        return s.toString().trim();
    }

    private String name(UUID id) {
        Player p = Bukkit.getPlayer(id);
        return p != null ? p.getName() : id.toString().substring(0, 8);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Team t = getTeam(e.getPlayer().getUniqueId());
        if (t != null && t.getLeader().equals(e.getPlayer().getUniqueId())) {
            disband(t);
        } else if (t != null) {
            t.remove(e.getPlayer().getUniqueId());
        }
        invites.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeamDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player a) || !(e.getEntity() instanceof Player b)) return;
        Team ta = getTeam(a.getUniqueId());
        if (ta != null && ta == getTeam(b.getUniqueId())) {
            e.setCancelled(true);
            a.sendMessage("§cKhông thể gây sát thương cho thành viên cùng team.");
        }
    }
}