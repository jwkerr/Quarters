package net.earthmc.quarters.object;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CuboidTest {
    @BeforeAll
    static void setUp() {
        MockBukkit.getOrCreateMock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void intersectingCuboidsShouldReturnTrue() {
        World world = new WorldMock(Material.DIRT, 3);

        Location cuboid1Pos1 = new Location(world, 2423, 74, -9734);
        Location cuboid1Pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid1 = new Cuboid(cuboid1Pos1, cuboid1Pos2);

        Location cuboid2pos1 = new Location(world, 2415, 83, -9725);
        Location cuboid2pos2 = new Location(world, 2405, 73, -9714);
        Cuboid cuboid2 = new Cuboid(cuboid2pos1, cuboid2pos2);

        assertTrue(cuboid1.doesIntersectWith(cuboid2));
    }

    @Test
    void nonIntersectingCuboidsShouldReturnFalse() {
        World world = new WorldMock(Material.DIRT, 3);

        Location cuboid1Pos1 = new Location(world, 2423, 74, -9734);
        Location cuboid1Pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid1 = new Cuboid(cuboid1Pos1, cuboid1Pos2);

        Location cuboid2pos1 = new Location(world, 2398, 77, -9734);
        Location cuboid2pos2 = new Location(world, 2388, 73, -9738);
        Cuboid cuboid2 = new Cuboid(cuboid2pos1, cuboid2pos2);

        assertFalse(cuboid1.doesIntersectWith(cuboid2));
    }

    @Test
    void intersectingCuboidsInOtherWorldsShouldReturnFalse() {
        World world1 = new WorldMock(Material.DIRT, 3);
        World world2 = new WorldMock(Material.DIRT, 3);

        Location cuboid1Pos1 = new Location(world1, 2423, 74, -9734);
        Location cuboid1Pos2 = new Location(world1, 2413, 82, -9722);
        Cuboid cuboid1 = new Cuboid(cuboid1Pos1, cuboid1Pos2);

        Location cuboid2pos1 = new Location(world2, 2415, 83, -9725);
        Location cuboid2pos2 = new Location(world2, 2405, 73, -9714);
        Cuboid cuboid2 = new Cuboid(cuboid2pos1, cuboid2pos2);

        assertFalse(cuboid1.doesIntersectWith(cuboid2));
    }

    @Test
    void locationInsideBoundsShouldReturnTrue() {
        World world = new WorldMock(Material.DIRT, 3);

        Location pos1 = new Location(world, 2423, 74, -9734);
        Location pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid = new Cuboid(pos1, pos2);

        Location locationToCheck = new Location(world, 2417, 74, -9726);
        assertTrue(cuboid.isLocationInsideBounds(locationToCheck));
    }

    @Test
    void locationNotInsideBoundsShouldReturnFalse() {
        World world = new WorldMock(Material.DIRT, 3);

        Location pos1 = new Location(world, 2423, 74, -9734);
        Location pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid = new Cuboid(pos1, pos2);

        Location locationToCheck = new Location(world, 2393, 73, -9729);
        assertFalse(cuboid.isLocationInsideBounds(locationToCheck));
    }

    @Test
    void locationInOtherWorldShouldReturnFalse() {
        World world1 = new WorldMock(Material.DIRT, 3);
        World world2 = new WorldMock(Material.DIRT, 3);

        Location pos1 = new Location(world1, 2423, 74, -9734);
        Location pos2 = new Location(world1, 2413, 82, -9722);
        Cuboid cuboid = new Cuboid(pos1, pos2);

        Location locationToCheck = new Location(world2, 2417, 74, -9726);
        assertFalse(cuboid.isLocationInsideBounds(locationToCheck));
    }

    @Test
    void getPlayersInCuboid() {
        World world1 = new WorldMock(Material.DIRT, 3);
        World world2 = new WorldMock(Material.DIRT, 3);

        Location pos1 = new Location(world1, 2423, 74, -9734);
        Location pos2 = new Location(world1, 2413, 82, -9722);
        Cuboid cuboid = new Cuboid(pos1, pos2);

        ServerMock server = MockBukkit.getMock();
        server.setPlayers(4);
        server.getPlayer(0).setLocation(new Location(world1, 2417, 74, -9726));
        server.getPlayer(1).setLocation(new Location(world1, 2417, 80, -9729));
        server.getPlayer(2).setLocation(new Location(world1, 9999, 80, -9999));
        server.getPlayer(3).setLocation(new Location(world2, 2417, 74, -9726));

        List<Player> expectedList = new ArrayList<>();
        expectedList.add(server.getPlayer(0));
        expectedList.add(server.getPlayer(1));

        assertEquals(expectedList, cuboid.getPlayersInCuboid());
    }

    @Test
    void getVolumeReturnsCorrectValue() {
        World world = new WorldMock(Material.DIRT, 3);

        Location pos1 = new Location(world, 2423, 74, -9734);
        Location pos2 = new Location(world, 2413, 82, -9722);
        Cuboid cuboid = new Cuboid(pos1, pos2);

        int expectedVolume = 960;

        assertEquals(expectedVolume, cuboid.getVolume());
    }
}