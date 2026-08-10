package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.Fireball;

public class Mage extends RPGClass {

    public Mage() {

        super(
                "mage",
                "Mage",
                "Một pháp sư sử dụng sức mạnh phép thuật."
        );

        addSkill(new Fireball());
    }
}