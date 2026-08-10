package dungnt.rpg.classsystem;

import dungnt.rpg.classsystem.classes.Archer;
import dungnt.rpg.classsystem.classes.Mage;
import dungnt.rpg.classsystem.classes.Warrior;

import java.util.HashMap;
import java.util.Map;

public class ClassManager {

    private final Map<String, RPGClass> classes = new HashMap<>();

    public ClassManager() {
        registerDefaults();
    }

    private void registerDefaults() {

        register(new Warrior());
        register(new Mage());
        register(new Archer());
    }

    public void register(RPGClass rpgClass) {
        classes.put(rpgClass.getId().toLowerCase(), rpgClass);
    }

    public RPGClass getClass(String id) {
        return classes.get(id.toLowerCase());
    }

    public boolean exists(String id) {
        return classes.containsKey(id.toLowerCase());
    }

    public Map<String, RPGClass> getClasses() {
        return classes;
    }
}