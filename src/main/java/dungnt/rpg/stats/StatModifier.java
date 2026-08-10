package dungnt.rpg.stats;

public class StatModifier {

    private final String id;
    private final StatType type;
    private final ModifierType modifierType;
    private final double amount;

    public StatModifier(
            String id,
            StatType type,
            ModifierType modifierType,
            double amount
    ) {
        this.id = id;
        this.type = type;
        this.modifierType = modifierType;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public StatType getType() {
        return type;
    }

    public ModifierType getModifierType() {
        return modifierType;
    }

    public double getAmount() {
        return amount;
    }
}