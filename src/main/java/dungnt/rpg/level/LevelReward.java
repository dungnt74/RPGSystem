package dungnt.rpg.level;

public class LevelReward {

    private final int level;

    public LevelReward(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public String getMessage() {

        return "§6§l✦ LEVEL UP! §fBạn đã đạt Level §e"
                + level
                + "§f!";
    }
}