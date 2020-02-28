package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DroneTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    RobotController rcMock = mock(RobotController.class);

    @Mock
    Navigation navMock = mock(Navigation.class);

    @InjectMocks
    Drone droneMock = new Drone(rcMock);

    @Before
    public void setup() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.getTeam()).thenReturn(Team.A);
        droneMock.findHQ();
        droneMock.findEnemyHQ();
    }

    @Test
    public void takeTurnTest() throws GameActionException {
        initializeCircleTest();
        isCowAroundTest();
        for (int i = 0; i < 3; i++) {
            droneMock.takeTurn();
        }
    }

    @Test
    public void isCowAroundTest() throws GameActionException {
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseNearbyRobots(5)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.COW, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        boolean result = droneMock.isCowAround();
        assertTrue(result);
    }

    @Test
    public void isCowAroundTest2() throws GameActionException {
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseNearbyRobots(5)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.MINER, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        boolean result = droneMock.isCowAround();
        assertFalse(result);
    }

    @Test
    public void pickUpCowTest() throws GameActionException {
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseNearbyRobots(5)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.COW, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 4));
        when(rcMock.canPickUpUnit(12)).thenReturn(true);

        boolean result = droneMock.pickUpCow();
        assertTrue(result);
    }

    @Test
    public void pickUpCowTest2() throws GameActionException {
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseNearbyRobots(5)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.COW, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 11));
        when(rcMock.canPickUpUnit(12)).thenReturn(true);
        when(navMock.goAround(new MapLocation(5, 5))).thenReturn(true);

        boolean result = droneMock.pickUpCow();
        assertFalse(result);
    }

    @Test
    public void pickUpCowTest3() throws GameActionException {
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseNearbyRobots(5)).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.MINER, 0, false, 0, 0, 0, new MapLocation(5, 5))});

        boolean result = droneMock.pickUpCow();
        assertFalse(result);
    }

    @Test
    public void goTest() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.canSenseLocation(new MapLocation(5, 5))).thenReturn(true);

        boolean result = droneMock.go(Direction.CENTER);
        assertTrue(result);
    }

    @Test
    public void goTest2() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.canSenseLocation(new MapLocation(5, 5))).thenReturn(false);

        boolean result = droneMock.go(Direction.CENTER);
        assertFalse(result);
    }

    @Test
    public void goToHQLocationsTest() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(10, 10));

        boolean result = droneMock.goToHQLocations(0);
        assertTrue(result);
    }

    @Test
    public void goToHQLocationsTest2() throws GameActionException {
        when(rcMock.senseRobotAtLocation(new MapLocation(-6, 5))).thenReturn(new RobotInfo(12, Team.B, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5)));
        when(rcMock.getLocation()).thenReturn(new MapLocation(-6, 5));

        boolean result = droneMock.goToHQLocations(0);
        assertFalse(result);
    }

    @Test
    public void circleHQandPickUpTest() throws GameActionException {
        initializeCircleTest();
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));

        boolean result = droneMock.circleHQandPickUp();
        assertTrue(result);
    }

    @Test
    public void circleHQandPickUpTest2() throws GameActionException {
        initializeCircleTest();
        when(rcMock.getLocation()).thenReturn(new MapLocation(-2, 5));

        boolean result = droneMock.circleHQandPickUp();
        assertTrue(result);
    }

    @Test
    public void initializeCircleTest() throws GameActionException {
        goToHQLocationsTest2();
        droneMock.initializeCircle();
    }

    @Test
    public void closestCircleSpot() throws GameActionException {
        initializeCircleTest();

        int result = droneMock.closestCircleSpot();
        assertEquals(result, 2);
    }
}
