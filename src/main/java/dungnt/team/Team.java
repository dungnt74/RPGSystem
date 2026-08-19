package dungnt.team;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public final class Team {
    private final UUID leader;
    private final Set<UUID> members = new LinkedHashSet<>();
    private String name;

    public Team(UUID leader) {
        this.leader = leader;
        members.add(leader);
    }

    public UUID getLeader() { return leader; }
    public Set<UUID> getMembers() { return Collections.unmodifiableSet(members); }
    public boolean contains(UUID uuid) { return members.contains(uuid); }
    public boolean add(UUID uuid) { return members.size() < 4 && members.add(uuid); }
    public boolean remove(UUID uuid) { return members.remove(uuid); }
    public int size() { return members.size(); }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}