package dungnt.rpg.item;

public enum EquipmentSlot {

    // =========================
    // WEAPON
    // =========================

    MAIN_HAND,
    OFF_HAND,

    // =========================
    // ARMOR
    // =========================

    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS,

    // =========================
    // ACCESSORIES
    // =========================

    BELT,
    GLOVES,
    JADE,
    RING1,
    RING2,
    EARRING,
    NECKLACE,

    // =========================
    // SPECIAL
    // =========================

    WINGS,
    BADGE,
    PET,
    MOUNT;

    /**
     * Những slot được "hậu thuẫn" bởi inventory thật của Minecraft
     * (main hand, off hand, và 4 slot giáp). Với các slot này,
     * inventory thật của player LÀ nguồn dữ liệu duy nhất — GUI
     * /equipment chỉ hiển thị/thao tác trực tiếp lên đó, không
     * lưu một bản sao ảo riêng như các slot phụ kiện khác
     * (RING1, WINGS, BELT, ...).
     */
    public boolean isVanillaBacked() {
        return this == MAIN_HAND
                || this == OFF_HAND
                || this == HELMET
                || this == CHESTPLATE
                || this == LEGGINGS
                || this == BOOTS;
    }
}