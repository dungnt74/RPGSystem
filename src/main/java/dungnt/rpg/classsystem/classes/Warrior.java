package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.PowerStrike;

public class Warrior extends RPGClass {

    public Warrior() {

        super(
                "warrior",
                "Warrior",
                "Một chiến binh mạnh mẽ chuyên chiến đấu cận chiến."
        );

        addSkill(new PowerStrike());
    }
}