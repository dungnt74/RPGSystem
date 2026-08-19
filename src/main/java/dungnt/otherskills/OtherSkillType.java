package dungnt.otherskills;

public enum OtherSkillType {
    MOVEMENT("movement"),
    MINING("mining"),
    WOODCUTTING("woodcutting"),
    FARMING("farming");

    private final String id;

    OtherSkillType(String id) { this.id = id; }
    public String getId() { return id; }
}
