package dungnt.rpg.mob;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobManager {

    private final Map<UUID, MobData> mobs =
            new HashMap<>();

    /**
     * Đăng ký một RPG Mob
     */
    public void register(LivingEntity entity, MobData mobData) {

        mobs.put(
                entity.getUniqueId(),
                mobData
        );
    }

    /**
     * Lấy dữ liệu RPG Mob
     */
    public MobData getMob(UUID uuid) {

        return mobs.get(uuid);
    }

    /**
     * Lấy dữ liệu từ LivingEntity
     */
    public MobData getMob(LivingEntity entity) {

        return mobs.get(
                entity.getUniqueId()
        );
    }

    /**
     * Kiểm tra entity có phải RPG Mob không
     */
    public boolean isRPGMob(UUID uuid) {

        return mobs.containsKey(uuid);
    }

    public boolean isRPGMob(LivingEntity entity) {

        return mobs.containsKey(
                entity.getUniqueId()
        );
    }

    /**
     * Xóa RPG Mob
     */
    public void unregister(UUID uuid) {

        mobs.remove(uuid);
    }

    public void unregister(LivingEntity entity) {

        mobs.remove(
                entity.getUniqueId()
        );
    }

    /**
     * Lấy toàn bộ RPG Mob
     */
    public Map<UUID, MobData> getMobs() {

        return mobs;
    }
}