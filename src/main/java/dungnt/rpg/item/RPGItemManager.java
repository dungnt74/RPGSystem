package dungnt.rpg.item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RPGItemManager {

    private final Map<String, RPGItem> items =
            new HashMap<>();

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public RPGItemManager() {
    }

    // ==================================================
    // REGISTER
    // ==================================================

    public void register(
            RPGItem item
    ) {

        if (item == null ||
                item.getId() == null) {

            return;
        }

        items.put(
                item.getId().toLowerCase(),
                item
        );
    }

    // ==================================================
    // GET
    // ==================================================

    public RPGItem get(
            String id
    ) {

        if (id == null) {
            return null;
        }

        return items.get(
                id.toLowerCase()
        );
    }

    // ==================================================
    // CHECK
    // ==================================================

    public boolean exists(
            String id
    ) {

        return get(id) != null;
    }

    // ==================================================
    // ALL
    // ==================================================

    public Map<String, RPGItem> getItems() {

        return Collections.unmodifiableMap(
                items
        );
    }

    // ==================================================
    // REMOVE
    // ==================================================

    public void unregister(
            String id
    ) {

        if (id == null) {
            return;
        }

        items.remove(
                id.toLowerCase()
        );
    }

    // ==================================================
    // CLEAR
    // ==================================================

    public void clear() {

        items.clear();
    }
}