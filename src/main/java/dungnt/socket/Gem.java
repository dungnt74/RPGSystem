package dungnt.socket;

import dungnt.rpg.stats.ModifierType;
import dungnt.rpg.stats.StatType;

public record Gem(String id, String name, int level, boolean special,
                  StatType stat, ModifierType modifierType, double amount) {}
