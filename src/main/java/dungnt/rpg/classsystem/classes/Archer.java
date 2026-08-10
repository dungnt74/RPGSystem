package dungnt.rpg.classsystem.classes;

import dungnt.rpg.classsystem.RPGClass;
import dungnt.rpg.skills.skills.Dash;

public class Archer extends RPGClass {

    public Archer() {

        super(
                "archer",
                "Archer",
                "Một xạ thủ chuyên chiến đấu từ khoảng cách xa."
        );

        addSkill(new Dash());
    }
}