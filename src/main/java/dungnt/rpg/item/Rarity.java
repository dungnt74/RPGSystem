package dungnt.rpg.item;

import org.bukkit.ChatColor;

public enum Rarity {
    COMMON("Common", ChatColor.WHITE),
    UNCOMMON("Uncommon", ChatColor.GREEN),
    RARE("Rare", ChatColor.BLUE),
    EPIC("Epic", ChatColor.DARK_PURPLE),
    LEGENDARY("Legendary", ChatColor.GOLD),
    MYTHIC("Mythic", ChatColor.LIGHT_PURPLE);

    private final String displayName;
    private final ChatColor color;

    Rarity(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
}
