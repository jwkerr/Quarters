package au.lupine.quarters.object.render;

import au.lupine.quarters.Quarters;
import au.lupine.quarters.object.base.SelectionRenderer;
import au.lupine.quarters.object.entity.Cuboid;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class PacketEventsSelectionRenderer implements SelectionRenderer {

    private static final double EDGE_THICKNESS = 0.04;
    private static final Quaternion4f IDENTITY_ROTATION = new Quaternion4f(0, 0, 0, 1);
    private static final DyeBlock[] DYE_BLOCKS = {
            new DyeBlock(new Color(249, 255, 254), WrappedBlockState.getDefaultState(StateTypes.WHITE_CONCRETE)),
            new DyeBlock(new Color(157, 157, 151), WrappedBlockState.getDefaultState(StateTypes.LIGHT_GRAY_CONCRETE)),
            new DyeBlock(new Color(71, 79, 82), WrappedBlockState.getDefaultState(StateTypes.GRAY_CONCRETE)),
            new DyeBlock(new Color(29, 29, 33), WrappedBlockState.getDefaultState(StateTypes.BLACK_CONCRETE)),
            new DyeBlock(new Color(176, 46, 38), WrappedBlockState.getDefaultState(StateTypes.RED_CONCRETE)),
            new DyeBlock(new Color(249, 128, 29), WrappedBlockState.getDefaultState(StateTypes.ORANGE_CONCRETE)),
            new DyeBlock(new Color(254, 216, 61), WrappedBlockState.getDefaultState(StateTypes.YELLOW_CONCRETE)),
            new DyeBlock(new Color(128, 199, 31), WrappedBlockState.getDefaultState(StateTypes.LIME_CONCRETE)),
            new DyeBlock(new Color(94, 124, 22), WrappedBlockState.getDefaultState(StateTypes.GREEN_CONCRETE)),
            new DyeBlock(new Color(22, 156, 156), WrappedBlockState.getDefaultState(StateTypes.CYAN_CONCRETE)),
            new DyeBlock(new Color(58, 179, 218), WrappedBlockState.getDefaultState(StateTypes.LIGHT_BLUE_CONCRETE)),
            new DyeBlock(new Color(60, 68, 170), WrappedBlockState.getDefaultState(StateTypes.BLUE_CONCRETE)),
            new DyeBlock(new Color(137, 50, 184), WrappedBlockState.getDefaultState(StateTypes.PURPLE_CONCRETE)),
            new DyeBlock(new Color(199, 78, 189), WrappedBlockState.getDefaultState(StateTypes.MAGENTA_CONCRETE)),
            new DyeBlock(new Color(243, 139, 170), WrappedBlockState.getDefaultState(StateTypes.PINK_CONCRETE)),
            new DyeBlock(new Color(131, 84, 50), WrappedBlockState.getDefaultState(StateTypes.BROWN_CONCRETE))
    };

    private final AtomicInteger nextEntityId = new AtomicInteger(2_000_000_000);
    private final Map<UUID, List<Integer>> visibleEntities = new ConcurrentHashMap<>();

    @Override
    public void show(@NotNull Player player, @NotNull Collection<Cuboid> cuboids, @NotNull Color colour, boolean glow) {
        List<Integer> entityIds = visibleEntities.computeIfAbsent(player.getUniqueId(), key -> new ArrayList<>());
        List<Cuboid> visibleCuboids = cuboids.stream()
                .filter(cuboid -> player.getWorld().equals(cuboid.getWorld()))
                .toList();

        if (visibleCuboids.isEmpty()) return;
        if (visibleCuboids.stream().noneMatch(cuboid -> isWithinRenderRange(player, cuboid))) return;

        for (EdgeSegment edge : getMergedEdges(visibleCuboids)) {
            addDisplay(player, edge.renderX(), edge.renderY(), edge.renderZ(), edge.scaleX(), edge.scaleY(), edge.scaleZ(), colour, glow, entityIds);
        }
    }

    @Override
    public void hide(@NotNull Player player) {
        List<Integer> entityIds = visibleEntities.remove(player.getUniqueId());
        if (entityIds == null || entityIds.isEmpty()) return;

        int[] ids = entityIds.stream().mapToInt(Integer::intValue).toArray();
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerDestroyEntities(ids));
    }

    private boolean isWithinRenderRange(@NotNull Player player, @NotNull Cuboid cuboid) {
        int range = Quarters.getInstance().config().renderer.maxDistanceForOutlines;
        int viewerX = player.getLocation().getBlockX();
        int viewerY = player.getLocation().getBlockY();
        int viewerZ = player.getLocation().getBlockZ();

        return viewerX > cuboid.getMinX() - range
                && viewerX < cuboid.getMaxX() + range
                && viewerY > cuboid.getMinY() - range
                && viewerY < cuboid.getMaxY() + range
                && viewerZ > cuboid.getMinZ() - range
                && viewerZ < cuboid.getMaxZ() + range;
    }

    private void addDisplay(@NotNull Player player, double x, double y, double z, double scaleX, double scaleY, double scaleZ, @NotNull Color colour, boolean glow, @NotNull List<Integer> entityIds) {
        int entityId = nextEntityId.getAndDecrement();

        WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                entityId,
                Optional.of(UUID.randomUUID()),
                EntityTypes.BLOCK_DISPLAY,
                new Vector3d(x, y, z),
                0F,
                0F,
                0F,
                0,
                Optional.empty()
        );

        List<EntityData<?>> data = new ArrayList<>();
        if (glow) data.add(new EntityData<>(0, EntityDataTypes.BYTE, (byte) 0x40));
        data.add(new EntityData<>(12, EntityDataTypes.VECTOR3F, new Vector3f((float) scaleX, (float) scaleY, (float) scaleZ)));
        data.add(new EntityData<>(13, EntityDataTypes.QUATERNION, IDENTITY_ROTATION));
        data.add(new EntityData<>(14, EntityDataTypes.QUATERNION, IDENTITY_ROTATION));
        data.add(new EntityData<>(17, EntityDataTypes.FLOAT, 128F));
        if (glow) data.add(new EntityData<>(22, EntityDataTypes.INT, colour.getRGB()));
        data.add(new EntityData<>(23, EntityDataTypes.BLOCK_STATE, getClosestBlockState(colour).getGlobalId()));

        PacketEvents.getAPI().getPlayerManager().sendPacket(player, spawn);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, new WrapperPlayServerEntityMetadata(entityId, data));
        entityIds.add(entityId);
    }

    private @NotNull List<EdgeSegment> getMergedEdges(@NotNull Collection<Cuboid> cuboids) {
        Set<BlockPos> occupied = new HashSet<>();
        for (Cuboid cuboid : cuboids) {
            for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x++) {
                for (int y = cuboid.getMinY(); y <= cuboid.getMaxY(); y++) {
                    for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z++) {
                        occupied.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        Map<Direction, Set<UnitEdge>> faceEdges = new HashMap<>();
        for (BlockPos block : occupied) {
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.X_NEG);
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.X_POS);
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.Y_NEG);
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.Y_POS);
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.Z_NEG);
            addFaceEdgesIfExposed(faceEdges, occupied, block, Direction.Z_POS);
        }

        Set<UnitEdge> boundaryEdges = new HashSet<>();
        for (Set<UnitEdge> edges : faceEdges.values()) {
            boundaryEdges.addAll(edges);
        }

        return mergeUnitEdges(boundaryEdges);
    }

    private void addFaceEdgesIfExposed(@NotNull Map<Direction, Set<UnitEdge>> faceEdges, @NotNull Set<BlockPos> occupied, @NotNull BlockPos block, @NotNull Direction direction) {
        BlockPos neighbour = new BlockPos(block.x() + direction.dx, block.y() + direction.dy, block.z() + direction.dz);
        if (occupied.contains(neighbour)) return;

        Set<UnitEdge> edges = faceEdges.computeIfAbsent(direction, key -> new HashSet<>());
        for (UnitEdge edge : direction.getFaceEdges(block)) {
            if (!edges.add(edge)) edges.remove(edge);
        }
    }

    private @NotNull List<EdgeSegment> mergeUnitEdges(@NotNull Set<UnitEdge> unitEdges) {
        Map<EdgeLine, List<Integer>> startsByLine = new HashMap<>();
        for (UnitEdge edge : unitEdges) {
            startsByLine.computeIfAbsent(edge.line(), key -> new ArrayList<>()).add(edge.start());
        }

        List<EdgeSegment> merged = new ArrayList<>();
        for (Map.Entry<EdgeLine, List<Integer>> entry : startsByLine.entrySet()) {
            List<Integer> starts = entry.getValue();
            Collections.sort(starts);

            int runStart = starts.getFirst();
            int previous = runStart;
            for (int index = 1; index < starts.size(); index++) {
                int current = starts.get(index);
                if (current == previous + 1) {
                    previous = current;
                    continue;
                }

                merged.add(entry.getKey().toSegment(runStart, previous + 1 - runStart));
                runStart = current;
                previous = current;
            }

            merged.add(entry.getKey().toSegment(runStart, previous + 1 - runStart));
        }

        return merged;
    }

    private @NotNull WrappedBlockState getClosestBlockState(@NotNull Color colour) {
        DyeBlock closest = DYE_BLOCKS[0];
        double closestDistance = Double.MAX_VALUE;

        for (DyeBlock dyeBlock : DYE_BLOCKS) {
            double distance = distanceSquared(colour, dyeBlock.colour());
            if (distance >= closestDistance) continue;

            closest = dyeBlock;
            closestDistance = distance;
        }

        return closest.blockState();
    }

    private double distanceSquared(@NotNull Color first, @NotNull Color second) {
        int red = first.getRed() - second.getRed();
        int green = first.getGreen() - second.getGreen();
        int blue = first.getBlue() - second.getBlue();

        return red * red + green * green + blue * blue;
    }

    private enum Direction {
        X_NEG(-1, 0, 0),
        X_POS(1, 0, 0),
        Y_NEG(0, -1, 0),
        Y_POS(0, 1, 0),
        Z_NEG(0, 0, -1),
        Z_POS(0, 0, 1);

        private final int dx;
        private final int dy;
        private final int dz;

        Direction(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        private @NotNull List<UnitEdge> getFaceEdges(@NotNull BlockPos block) {
            return switch (this) {
                case X_NEG -> getXFaceEdges(block.x(), block.y(), block.z());
                case X_POS -> getXFaceEdges(block.x() + 1, block.y(), block.z());
                case Y_NEG -> getYFaceEdges(block.x(), block.y(), block.z());
                case Y_POS -> getYFaceEdges(block.x(), block.y() + 1, block.z());
                case Z_NEG -> getZFaceEdges(block.x(), block.y(), block.z());
                case Z_POS -> getZFaceEdges(block.x(), block.y(), block.z() + 1);
            };
        }

        private @NotNull List<UnitEdge> getXFaceEdges(int x, int y, int z) {
            return List.of(
                    UnitEdge.y(x, y, z),
                    UnitEdge.y(x, y, z + 1),
                    UnitEdge.z(x, y, z),
                    UnitEdge.z(x, y + 1, z)
            );
        }

        private @NotNull List<UnitEdge> getYFaceEdges(int x, int y, int z) {
            return List.of(
                    UnitEdge.x(x, y, z),
                    UnitEdge.x(x, y, z + 1),
                    UnitEdge.z(x, y, z),
                    UnitEdge.z(x + 1, y, z)
            );
        }

        private @NotNull List<UnitEdge> getZFaceEdges(int x, int y, int z) {
            return List.of(
                    UnitEdge.x(x, y, z),
                    UnitEdge.x(x, y + 1, z),
                    UnitEdge.y(x, y, z),
                    UnitEdge.y(x + 1, y, z)
            );
        }
    }

    private enum Axis {
        X,
        Y,
        Z
    }

    private record BlockPos(int x, int y, int z) {}

    private record EdgeLine(@NotNull Axis axis, int firstFixed, int secondFixed) {

        private @NotNull EdgeSegment toSegment(int start, int length) {
            return switch (axis) {
                case X -> EdgeSegment.x(start, firstFixed, secondFixed, length);
                case Y -> EdgeSegment.y(firstFixed, start, secondFixed, length);
                case Z -> EdgeSegment.z(firstFixed, secondFixed, start, length);
            };
        }
    }

    private record UnitEdge(@NotNull Axis axis, int x, int y, int z) {

        private static @NotNull UnitEdge x(int x, int y, int z) {
            return new UnitEdge(Axis.X, x, y, z);
        }

        private static @NotNull UnitEdge y(int x, int y, int z) {
            return new UnitEdge(Axis.Y, x, y, z);
        }

        private static @NotNull UnitEdge z(int x, int y, int z) {
            return new UnitEdge(Axis.Z, x, y, z);
        }

        private @NotNull EdgeLine line() {
            return switch (axis) {
                case X -> new EdgeLine(axis, y, z);
                case Y -> new EdgeLine(axis, x, z);
                case Z -> new EdgeLine(axis, x, y);
            };
        }

        private int start() {
            return switch (axis) {
                case X -> x;
                case Y -> y;
                case Z -> z;
            };
        }
    }

    private record EdgeSegment(@NotNull Axis axis, int x, int y, int z, int length) {

        private static @NotNull EdgeSegment x(int x, int y, int z, int length) {
            return new EdgeSegment(Axis.X, x, y, z, length);
        }

        private static @NotNull EdgeSegment y(int x, int y, int z, int length) {
            return new EdgeSegment(Axis.Y, x, y, z, length);
        }

        private static @NotNull EdgeSegment z(int x, int y, int z, int length) {
            return new EdgeSegment(Axis.Z, x, y, z, length);
        }

        private double scaleX() {
            return axis == Axis.X ? length : EDGE_THICKNESS;
        }

        private double scaleY() {
            return axis == Axis.Y ? length : EDGE_THICKNESS;
        }

        private double scaleZ() {
            return axis == Axis.Z ? length : EDGE_THICKNESS;
        }

        private double renderX() {
            return axis == Axis.X ? x : x - EDGE_THICKNESS / 2;
        }

        private double renderY() {
            return axis == Axis.Y ? y : y - EDGE_THICKNESS / 2;
        }

        private double renderZ() {
            return axis == Axis.Z ? z : z - EDGE_THICKNESS / 2;
        }
    }

    private record DyeBlock(@NotNull Color colour, @NotNull WrappedBlockState blockState) {}
}
