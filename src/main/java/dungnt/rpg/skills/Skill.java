package dungnt.rpg.skills;

public abstract class Skill {

    private final String id;
    private final String name;
    private final double manaCost;
    private final double cooldown;

    protected Skill(
            String id,
            String name,
            double manaCost,
            double cooldown
    ) {
        this.id = id;
        this.name = name;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getManaCost() {
        return manaCost;
    }

    public double getCooldown() {
        return cooldown;
    }

    public abstract void execute(SkillContext context);
}