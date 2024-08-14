package net.earthmc.quarters.object.wrapper;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyPermission;
import net.earthmc.quarters.object.entity.Quarter;
import net.earthmc.quarters.object.state.ActionType;
import net.earthmc.quarters.object.state.PermLevel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Towny's internal permission are intentionally not used to avoid risk of data corruption from internal Towny changes
 * <br>
 * <br>
 * Use {@link #fromTownyActionType(TownyPermission.ActionType) fromTownyActionType} and {@link #fromTownyPermissionLevel(TownyPermission.PermLevel) fromTownyPermissionLevel} to convert them
 */
public class QuarterPermissions {

    private Map<PermLevel, Boolean> buildPerms = makeDefaultPerms();
    private Map<PermLevel, Boolean> destroyPerms = makeDefaultPerms();
    private Map<PermLevel, Boolean> switchPerms = makeDefaultPerms();
    private Map<PermLevel, Boolean> itemUsePerms = makeDefaultPerms();

    public Map<PermLevel, Boolean> getPermissions(@NotNull ActionType type) {
        return switch (type) {
            case BUILD -> buildPerms;
            case DESTROY -> destroyPerms;
            case SWITCH -> switchPerms;
            case ITEM_USE -> itemUsePerms;
        };
    }

    /**
     * @return True if the resident can perform the specified action under these permissions
     */
    public boolean testPermission(@NotNull Quarter quarter, @NotNull ActionType type, @NotNull Resident resident) {
        Map<PermLevel, Boolean> perms = getPermissions(type);

        boolean isResidentAllowed = perms.get(PermLevel.RESIDENT);
        boolean isNationAllowed = perms.get(PermLevel.NATION);
        boolean isAllyAllowed = perms.get(PermLevel.ALLY);
        boolean isOutsiderAllowed = perms.get(PermLevel.OUTSIDER);

        if (isOutsiderAllowed) return true; // Outsider extends to every other user

        if (isResidentAllowed && quarter.getTown().hasResident(resident)) return true;

        Nation nation = quarter.getNation();
        if (nation != null) {
            Nation residentNation = resident.getNationOrNull();

            if (isNationAllowed && nation.equals(residentNation)) return true;

            return isAllyAllowed && nation.hasAlly(residentNation);
        }

        return false;
    }

    public void setPermission(@NotNull ActionType type, @NotNull PermLevel level, boolean allowed) {
        switch (type) {
            case BUILD -> buildPerms.put(level, allowed);
            case DESTROY -> destroyPerms.put(level, allowed);
            case SWITCH -> switchPerms.put(level, allowed);
            case ITEM_USE -> itemUsePerms.put(level, allowed);
        }
    }

    public void setPermissions(@NotNull ActionType type, @NotNull Map<PermLevel, Boolean> perms) {
        verifyPermissionsOrThrow(perms);

        switch(type) {
            case BUILD -> buildPerms = perms;
            case DESTROY -> destroyPerms = perms;
            case SWITCH -> switchPerms = perms;
            case ITEM_USE -> itemUsePerms = perms;
        }
    }

    public void resetPermissions(@NotNull ActionType type) {
        switch(type) {
            case BUILD -> buildPerms = makeDefaultPerms();
            case DESTROY -> destroyPerms = makeDefaultPerms();
            case SWITCH -> switchPerms = makeDefaultPerms();
            case ITEM_USE -> itemUsePerms = makeDefaultPerms();
        }
    }

    public static @NotNull ActionType fromTownyActionType(@NotNull TownyPermission.ActionType type) {
        return switch (type) {
            case BUILD -> ActionType.BUILD;
            case DESTROY -> ActionType.DESTROY;
            case SWITCH -> ActionType.SWITCH;
            case ITEM_USE -> ActionType.ITEM_USE;
        };
    }

    public static @NotNull PermLevel fromTownyPermissionLevel(@NotNull TownyPermission.PermLevel level) {
        return switch (level) {
            case RESIDENT -> PermLevel.RESIDENT;
            case NATION -> PermLevel.NATION;
            case ALLY -> PermLevel.ALLY;
            case OUTSIDER -> PermLevel.OUTSIDER;
        };
    }

    public static void verifyPermissionsOrThrow(Map<PermLevel, Boolean> perms) {
        for (PermLevel level : PermLevel.values()) {
            if (perms.get(level) == null) throw new IllegalArgumentException("Permission map is missing value " + level);
        }
    }

    private static Map<PermLevel, Boolean> makeDefaultPerms() {
        Map<PermLevel, Boolean> perms = new HashMap<>();

        perms.put(PermLevel.RESIDENT, false);
        perms.put(PermLevel.NATION, false);
        perms.put(PermLevel.ALLY, false);
        perms.put(PermLevel.OUTSIDER,false);

        return perms;
    }

    public String createPermissionLine(@NotNull ActionType type) {
        Map<PermLevel, Boolean> perms = getPermissions(type);

        return (perms.get(PermLevel.RESIDENT) ? "r" : "-") +
                (perms.get(PermLevel.NATION) ? "n" : "-") +
                (perms.get(PermLevel.ALLY) ? "a" : "-") +
                (perms.get(PermLevel.OUTSIDER) ? "o" : "-");
    }
}
