package dungnt.rpg.combat;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Checks WorldGuard's PVP flag without making WorldGuard a hard dependency.
 * If WorldGuard is not installed, RPG damage is allowed normally.
 */
public final class PvPProtection {

    private PvPProtection() {
    }

    public static boolean isPvPAllowed(Location location) {
        if (location == null || location.getWorld() == null) {
            return true;
        }

        try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldguard.bukkit.BukkitAdapter");
            Class<?> flagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Class<?> stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");

            Object worldGuard = worldGuardClass.getMethod("getInstance").invoke(null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(worldGuard);
            Object regionContainer = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Object query = regionContainer.getClass().getMethod("createQuery").invoke(regionContainer);

            Object applicableRegionSet = bukkitAdapterClass
                    .getMethod("adapt", Location.class)
                    .invoke(null, location);

            Object pvpFlag = flagsClass.getField("PVP").get(null);

            Method queryState = query.getClass().getMethod(
                    "queryState",
                    Class.forName("com.sk89q.worldedit.util.Location"),
                    Class.forName("com.sk89q.worldguard.LocalPlayer"),
                    stateFlagClass
            );

            // The LocalPlayer argument may be null: this checks the region state
            // itself, which is exactly what we need for a PvP deny area.
            Object state = queryState.invoke(query, applicableRegionSet, null, pvpFlag);

            return state == null || !state.toString().equals("DENY");
        } catch (ClassNotFoundException ignored) {
            // WorldGuard is not installed.
            return true;
        } catch (ReflectiveOperationException ignored) {
            // If an incompatible WorldGuard build is installed, fail open so
            // the RPG plugin itself keeps working rather than crashing.
            return true;
        }
    }

    /**
     * Returns false when either side of a player-vs-player attack is inside
     * a WorldGuard region where PVP is explicitly denied.
     */
    public static boolean canDamagePlayer(Player attacker, Player target) {
        if (attacker == null || target == null) {
            return true;
        }

        return isPvPAllowed(attacker.getLocation())
                && isPvPAllowed(target.getLocation());
    }
}
