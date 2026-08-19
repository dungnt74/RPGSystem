package dungnt.rpg.mob;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Registry cho các {@link MobDefinition} đọc từ Mobs/*.yml (mirror của RPGItemManager). */
public class MobDefinitionManager {

    private final Map<String, MobDefinition> definitions = new HashMap<>();

    public void register(MobDefinition def) {
        if (def == null || def.getId() == null) return;
        definitions.put(def.getId().toLowerCase(Locale.ROOT), def);
    }

    public MobDefinition get(String id) {
        if (id == null) return null;
        return definitions.get(id.toLowerCase(Locale.ROOT));
    }

    public boolean exists(String id) {
        return get(id) != null;
    }

    public void unregister(String id) {
        if (id == null) return;
        definitions.remove(id.toLowerCase(Locale.ROOT));
    }

    public Map<String, MobDefinition> getDefinitions() {
        return Collections.unmodifiableMap(definitions);
    }

    public void clear() {
        definitions.clear();
    }
}