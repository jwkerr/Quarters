package net.earthmc.quarters.object;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuarterTest {
    @BeforeAll
    static void setUp() {
        MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testGetVolumeReturnsCorrectValue() {
        Quarter quarter = new Quarter();
        World world = new WorldMock(Material.DIRT, 3);

        Location cuboid1Pos1 = new Location(world, 2423, 74, -9734);
        Location cuboid1Pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid1 = new Cuboid(cuboid1Pos1, cuboid1Pos2);

        Location cuboid2pos1 = new Location(world, 2398, 77, -9734);
        Location cuboid2pos2 = new Location(world, 2388, 73, -9738);
        Cuboid cuboid2 = new Cuboid(cuboid2pos1, cuboid2pos2);

        List<Cuboid> cuboids = new ArrayList<>();
        cuboids.add(cuboid1);
        cuboids.add(cuboid2);

        quarter.setCuboids(cuboids);

        int expectedVolume = 1120;

        assertEquals(expectedVolume, quarter.getVolume());
    }

    @Test
    void testRGBArrayClampsCorrectly() {
        Quarter quarter = new Quarter();

        int[] inputRGB = {256, 0, -10};
        int[] expectedRGB = {255, 0, 0};

        quarter.setRGB(inputRGB);

        assertArrayEquals(expectedRGB, quarter.getRGB());
    }
}