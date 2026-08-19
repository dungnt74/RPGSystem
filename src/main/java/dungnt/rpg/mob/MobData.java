package dungnt.rpg.mob;

import java.util.UUID;

public class MobData {

    private final UUID uuid;
    private final String id;
    private final MobStats stats;

    public MobData(
            UUID uuid,
            String id,
            MobStats stats
    ) {
        this.uuid = uuid;
        this.id = id;
        this.stats = stats;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }

    public MobStats getStats() {
        return stats;
    }
}