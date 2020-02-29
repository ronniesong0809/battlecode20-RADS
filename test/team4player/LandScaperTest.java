package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class LandScaperTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    RobotController rcMock = mock(RobotController.class);
    @Mock
    Navigation navMock = mock(Navigation.class);

    @InjectMocks
    Landscaper lpMock = new Landscaper(rcMock);

    @Before
    public void setup() throws GameActionException {
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(5, 5))});
        when(rcMock.getTeam()).thenReturn(Team.A);
        lpMock.findHQ();
    }

    @Test
    public void takeTurnTest() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(1, 1));
        when(navMock.goAround(new MapLocation(5, 5))).thenReturn(true);
        lpMock.takeTurn();
    }

    @Test
    public void takeTurnTest2() throws GameActionException {
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 4));
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{null});
        initializeWallLocationsAndLevels();
        lpMock.takeTurn();
    }

    @Test
    public void tryDigTest() throws GameActionException {
        findDigSpotTest();
        when(rcMock.canDigDirt(Direction.WEST)).thenReturn(true);

        boolean result = lpMock.tryDig();
        assertEquals(result, true);
    }

    @Test
    public void tryDigTest2() throws GameActionException {
        findDigSpotTest();
        when(rcMock.canDigDirt(Direction.NORTH)).thenReturn(true);

        boolean result = lpMock.tryDig();
        assertEquals(result, false);
    }

    @Test
    public void findDigSpotTest() {
        createDigLocations();
        when(rcMock.getLocation()).thenReturn(new MapLocation(8, 8));
        when(rcMock.canDigDirt(Direction.NORTH)).thenReturn(true);

        Direction result = lpMock.findDigSpot();
        assertEquals(result, null);
    }

    @Test
    public void findDigSpotTest2() {
        createDigLocations();
        when(rcMock.getLocation()).thenReturn(new MapLocation(8, 8));
        when(rcMock.canDigDirt(Direction.WEST)).thenReturn(true);
        Direction result = lpMock.findDigSpot();

        assertEquals(result, Direction.WEST);
    }

    @Test
    public void tryDepositTest() throws GameActionException {
        when(rcMock.canDepositDirt(Direction.NORTH)).thenReturn(true);
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        lpMock.initializeWallLocationsAndLevels();

        lpMock.tryDeposit(new MapLocation(4, 7)); // SOUTHEAST
        lpMock.tryDeposit(new MapLocation(4, 3)); // NORTHEAST
        lpMock.tryDeposit(new MapLocation(7, 4)); // NORTHWEST
        lpMock.tryDeposit(new MapLocation(7, 6)); // SOUTHWEST
        for (int i = 0; i < 301; i++) {
            takeTurnTest();
        }
        lpMock.tryDeposit(new MapLocation(8, 7)); // SOUTHWEST
    }

    @Test
    public void createDigLocations() {
        lpMock.createDigLocations();
    }

    @Test
    public void initializeWallLocationsAndLevels() {
        lpMock.initializeWallLocationsAndLevels();
        when(rcMock.senseNearbyRobots()).thenReturn(new RobotInfo[]{new RobotInfo(12, Team.A, RobotType.HQ, 0, false, 0, 0, 0, new MapLocation(4, 4))});
        lpMock.initializeWallLocationsAndLevels();
    }

    @Test
    public void senseWallHeights() {
        lpMock.initializeWallLocationsAndLevels();
        lpMock.senseWallHeights();
    }

    @Test
    public void findWallSpotTestReturnNull() throws GameActionException {
        lpMock.initializeWallLocationsAndLevels();
        when(rcMock.getLocation()).thenReturn(new MapLocation(40, 5));
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(2);

        MapLocation result = lpMock.findWallSpot();
        assertEquals(result, null);
    }

    @Test
    public void findWallSpotTestReturnMapLocation() throws GameActionException {
        lpMock.initializeWallLocationsAndLevels();
        when(rcMock.getLocation()).thenReturn(new MapLocation(4, 6));
        when(rcMock.getCurrentSensorRadiusSquared()).thenReturn(5);
        when(rcMock.senseRobotAtLocation(new MapLocation(4, 4))).thenReturn(null);

        MapLocation result = lpMock.findWallSpot();
        assertEquals(result, new MapLocation(4, 6));
    }
}
