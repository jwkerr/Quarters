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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        for (Cuboid cuboid : cuboids) {
            if (!player.getWorld().equals(cuboid.getWorld())) continue;
            if (!isWithinRenderRange(player, cuboid)) continue;

            addEdges(player, cuboid, colour, glow, entityIds);
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

    private void addEdges(@NotNull Player player, @NotNull Cuboid cuboid, @NotNull Color colour, boolean glow, @NotNull List<Integer> entityIds) {
        Bounds bounds = Bounds.from(cuboid);
        double t = EDGE_THICKNESS;

        addDisplay(player, bounds.minX, bounds.minY, bounds.minZ, bounds.width, t, t, colour, glow, entityIds);
        addDisplay(player, bounds.minX, bounds.minY, bounds.maxZ - t, bounds.width, t, t, colour, glow, entityIds);
        addDisplay(player, bounds.minX, bounds.maxY - t, bounds.minZ, bounds.width, t, t, colour, glow, entityIds);
        addDisplay(player, bounds.minX, bounds.maxY - t, bounds.maxZ - t, bounds.width, t, t, colour, glow, entityIds);

        addDisplay(player, bounds.minX, bounds.minY, bounds.minZ, t, bounds.height, t, colour, glow, entityIds);
        addDisplay(player, bounds.maxX - t, bounds.minY, bounds.minZ, t, bounds.height, t, colour, glow, entityIds);
        addDisplay(player, bounds.minX, bounds.minY, bounds.maxZ - t, t, bounds.height, t, colour, glow, entityIds);
        addDisplay(player, bounds.maxX - t, bounds.minY, bounds.maxZ - t, t, bounds.height, t, colour, glow, entityIds);

        addDisplay(player, bounds.minX, bounds.minY, bounds.minZ, t, t, bounds.depth, colour, glow, entityIds);
        addDisplay(player, bounds.maxX - t, bounds.minY, bounds.minZ, t, t, bounds.depth, colour, glow, entityIds);
        addDisplay(player, bounds.minX, bounds.maxY - t, bounds.minZ, t, t, bounds.depth, colour, glow, entityIds);
        addDisplay(player, bounds.maxX - t, bounds.maxY - t, bounds.minZ, t, t, bounds.depth, colour, glow, entityIds);
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

    private record Bounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double width, double height, double depth) {

        private static @NotNull Bounds from(@NotNull Cuboid cuboid) {
            double minX = cuboid.getMinX();
            double minY = cuboid.getMinY();
            double minZ = cuboid.getMinZ();
            double maxX = cuboid.getMaxX() + 1D;
            double maxY = cuboid.getMaxY() + 1D;
            double maxZ = cuboid.getMaxZ() + 1D;

            return new Bounds(minX, minY, minZ, maxX, maxY, maxZ, maxX - minX, maxY - minY, maxZ - minZ);
        }
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

    private record DyeBlock(@NotNull Color colour, @NotNull WrappedBlockState blockState) {}
}
