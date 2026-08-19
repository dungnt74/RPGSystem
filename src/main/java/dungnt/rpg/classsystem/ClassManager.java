package dungnt.rpg.classsystem;

import dungnt.rpg.classsystem.classes.Archer;
import dungnt.rpg.classsystem.classes.Assassin;
import dungnt.rpg.classsystem.classes.Mage;
import dungnt.rpg.classsystem.classes.Warrior;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassManager {

    private final Map<String, RPGClass> classes =
            new HashMap<>();

    public ClassManager() {

        register(new Warrior());
        register(new Mage());
        register(new Archer());
        register(new Assassin());
    }

    // ==================================================
    // REGISTER
    // ==================================================

    public void register(RPGClass rpgClass) {

        if (rpgClass == null) {
            return;
        }

        classes.put(
                rpgClass.getId().toLowerCase(),
                rpgClass
        );
    }

    // ==================================================
    // GET
    // ==================================================

    public RPGClass getClass(String id) {

        if (id == null) {
            return null;
        }

        return classes.get(
                id.toLowerCase()
        );
    }

    public boolean hasClass(String id) {
        return getClass(id) != null;
    }

    // ==================================================
    // ALL
    // ==================================================

    public Collection<RPGClass> getClasses() {

        return Collections.unmodifiableCollection(
                classes.values()
        );
    }
}