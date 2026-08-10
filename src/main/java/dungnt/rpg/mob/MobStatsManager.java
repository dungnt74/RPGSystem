package dungnt.rpg.mob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobStatsManager {

    private final Map<UUID, MobStats> stats =
            new HashMap<>();

    public MobStats getStats(UUID uuid) {
        return stats.get(uuid);
    }

    public MobStats getOrCreate(UUID uuid) {
        return stats.computeIfAbsent(
                uuid,
                key -> new MobStats()
        );
    }

    public void remove(UUID uuid) {
        stats.remove(uuid);
    }

    public boolean hasStats(UUID uuid) {
        return stats.containsKey(uuid);
    }
}