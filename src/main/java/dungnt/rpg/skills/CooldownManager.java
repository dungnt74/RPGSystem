package dungnt.rpg.skills;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<String, Long> cooldowns = new HashMap<>();

    private String createKey(UUID uuid, String skillId) {

        return uuid + ":" + skillId.toLowerCase();
    }

    public boolean isOnCooldown(
            UUID uuid,
            String skillId
    ) {

        String key = createKey(uuid, skillId);

        Long endTime = cooldowns.get(key);

        if (endTime == null) {
            return false;
        }

        if (System.currentTimeMillis() >= endTime) {

            cooldowns.remove(key);

            return false;
        }

        return true;
    }

    public long getRemaining(
            UUID uuid,
            String skillId
    ) {

        String key = createKey(uuid, skillId);

        Long endTime = cooldowns.get(key);

        if (endTime == null) {
            return 0;
        }

        long remaining =
                endTime - System.currentTimeMillis();

        return Math.max(0, remaining);
    }

    public void setCooldown(
            UUID uuid,
            String skillId,
            double seconds
    ) {

        long duration =
                (long) (seconds * 1000);

        cooldowns.put(
                createKey(uuid, skillId),
                System.currentTimeMillis() + duration
        );
    }

    public void clear(UUID uuid, String skillId) {

        cooldowns.remove(
                createKey(uuid, skillId)
        );
    }
}