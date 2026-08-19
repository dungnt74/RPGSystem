package dungnt.otherskills;

import java.util.EnumMap;

public class OtherSkillData {
    private final EnumMap<OtherSkillType, Integer> levels = new EnumMap<>(OtherSkillType.class);
    private final EnumMap<OtherSkillType, Double> experience = new EnumMap<>(OtherSkillType.class);

    public OtherSkillData() {
        for (OtherSkillType type : OtherSkillType.values()) {
            levels.put(type, 1);
            experience.put(type, 0.0);
        }
    }

    public int getLevel(OtherSkillType type) { return levels.getOrDefault(type, 1); }
    public void setLevel(OtherSkillType type, int level) { levels.put(type, Math.max(1, level)); }
    public double getExperience(OtherSkillType type) { return experience.getOrDefault(type, 0.0); }
    public void setExperience(OtherSkillType type, double exp) { experience.put(type, Math.max(0.0, exp)); }
}
