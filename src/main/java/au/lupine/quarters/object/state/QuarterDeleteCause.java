package au.lupine.quarters.object.state;

/**
 * The cause of why a {@link au.lupine.quarters.object.entity.Quarter quarter} is (being) deleted. This can be used to cancel certain deletions if needed.
 */
public enum QuarterDeleteCause {
    /** The quarter was deleted because a player used /q delete. */
    DELETE_COMMAND,

    /** The quarter was deleted because a player used /q delete all. */
    DELETE_ALL_COMMAND,

    /** The quarter was deleted because a player used /q delete plot. */
    DELETE_PLOT_COMMAND,

    /** The quarter was deleted because an admin used /qa delete. */
    ADMIN_DELETE_COMMAND,

    /** The quarter was deleted because a plot was unclaimed (Includes manual unclaim & e.g. town falling). */
    PLOT_UNCLAIM
}
