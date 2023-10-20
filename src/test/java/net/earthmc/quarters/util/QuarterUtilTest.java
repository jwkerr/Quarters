package net.earthmc.quarters.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import net.earthmc.quarters.object.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class QuarterUtilTest {
    @BeforeAll
    static void setUp() {
        MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void serializeCuboidsReturnsCorrectValue() {
        World world = new WorldMock(Material.DIRT, 3);

        Location cuboid1Pos1 = new Location(world, 2423, 74, -9734);
        Location cuboid1Pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid1 = new Cuboid(cuboid1Pos1, cuboid1Pos2);

        Location cuboid2pos1 = new Location(world, 2398, 77, -9734);
        Location cuboid2pos2 = new Location(world, 2388, 73, -9738);
        Cuboid cuboid2 = new Cuboid(cuboid2pos1, cuboid2pos2);

        List<Cuboid> inputList = new ArrayList<>();
        inputList.add(cuboid1);
        inputList.add(cuboid2);

        String expectedString = "World+2423+74+-9734;World+2413+82+-9722:World+2398+77+-9734;World+2388+73+-9738";

        assertEquals(expectedString, QuarterUtil.serializeCuboids(inputList));
    }

    @Test
    void serializeLocationReturnsCorrectValue() {
        World world = new WorldMock(Material.DIRT, 3);

        Location location = new Location(world, 2423, 74, -9734);

        String expectedString = "World+2423+74+-9734";

        assertEquals(expectedString, QuarterUtil.serializeLocation(location));
    }

    @Test
    void deserializeUUIDListReturnsCorrectValue() {
        String uuid1 = "fed0ec4a-f1ad-4b97-9443-876391668b34";
        String uuid2 = "f17d77ab-aed4-44e7-96ef-ec9cd473eda3";

        String inputString = uuid1 + "+" + uuid2;

        List<UUID> expectedUUIDList = new ArrayList<>();
        expectedUUIDList.add(UUID.fromString(uuid1));
        expectedUUIDList.add(UUID.fromString(uuid2));

        assertEquals(expectedUUIDList, QuarterUtil.deserializeUUIDList(inputString));
    }

    @Test
    void serializeUUIDListReturnsCorrectValue() {
        String uuid1 = "fed0ec4a-f1ad-4b97-9443-876391668b34";
        String uuid2 = "f17d77ab-aed4-44e7-96ef-ec9cd473eda3";

        List<UUID> inputList = new ArrayList<>();
        inputList.add(UUID.fromString(uuid1));
        inputList.add(UUID.fromString(uuid2));

        String expectedString = uuid1 + "+" + uuid2;

        assertEquals(expectedString, QuarterUtil.serializeUUIDList(inputList));
    }

    @Test
    void deserializeDoubleReturnsNullWhenStringIsNull() {
        assertNull(QuarterUtil.deserializeDouble("null"));
    }

    @Test
    void deserializeDoubleThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> QuarterUtil.deserializeDouble("waawdaw"));
    }

    @Test
    void deserializeDoubleReturnsCorrectValue() {
        assertEquals(5, QuarterUtil.deserializeDouble("5"));
    }
}