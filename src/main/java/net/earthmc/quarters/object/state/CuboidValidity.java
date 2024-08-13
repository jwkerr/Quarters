package net.earthmc.quarters.object.state;

public enum CuboidValidity {
    CONTAINS_WILDERNESS, // TODO: add this check, splitting to make checks more robust
    SPANS_MULTIPLE_TOWNS,
    INTERSECTS,
    VALID
}
