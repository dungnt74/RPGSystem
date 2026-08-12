package dungnt.rpg.stats;

public class StatModifier {

    private final String id;
    private final StatType type;
    private final ModifierType modifierType;
    private final double amount;
    private final ModifierSource source;

    // ==================================================
    // CONSTRUCTOR
    // ==================================================

    public StatModifier(
            String id,
            StatType type,
            ModifierType modifierType,
            double amount
    ) {

        this(
                id,
                type,
                modifierType,
                amount,
                ModifierSource.OTHER
        );
    }

    public StatModifier(
            String id,
            StatType type,
            ModifierType modifierType,
            double amount,
            ModifierSource source
    ) {

        this.id = id;
        this.type = type;
        this.modifierType = modifierType;
        this.amount = amount;
        this.source =
                source == null
                        ? ModifierSource.OTHER
                        : source;
    }

    // ==================================================
    // GETTERS
    // ==================================================

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

    public ModifierSource getSource() {
        return source;
    }
}