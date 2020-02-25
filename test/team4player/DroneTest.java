package team4player;

import battlecode.common.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class DroneTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @Mock
    Drone droneMock = mock(Drone.class);

    @Mock
    Navigation navMock = mock(Navigation.class);

    @Mock
    Broadcast bcMock = mock(Broadcast.class);

    @Mock
    RobotInfo rInfoMock = mock(RobotInfo.class); // daniel

    @Mock
    Util utilMock = mock(Util.class); // daniel

    @Mock
    Unit unitMock = mock(Unit.class); // daniel

    @Mock
    Robot robotMock = mock(Robot.class); // daniel

    @InjectMocks
    Miner minerMock = new Miner(rcMock);

    @Before
    public void setup() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
    }

    @Test
    public void takeTurnTest() throws GameActionException {
    }

    @Test
    public void goTest() throws GameActionException {
        //when(rcMock.canSenseLocation(rcMock.getLocation().add(Direction.NORTH))).thenReturn(true);
        navMock.droneMove(Direction.NORTH);
        boolean result = droneMock.go(Direction.NORTH);
        assertTrue(true == true);
    } // Daniel -- TODO

    @Test
    public void composeEnemyHQLocationsTest() {
        //when(rcMock.canSenseLocation(rcMock.getLocation().add(Direction.NORTH))).thenReturn(true);
        droneMock.enemyHQlocs = new ArrayList<MapLocation>();

        MapLocation topCorner = new MapLocation(rcMock.getMapWidth() - 1, rcMock.getMapHeight() - 1);
        //TODO -- look here https://stackoverflow.com/questions/23236338/using-mockito-to-mock-a-local-variable-of-a-method
        /*droneMock.enemyHQlocs.add(new MapLocation(topCorner.x - droneMock.hqLoc.x, topCorner.y - droneMock.hqLoc.y));
        droneMock.enemyHQlocs.add(new MapLocation(topCorner.x - droneMock.hqLoc.x, droneMock.hqLoc.y));
        droneMock.enemyHQlocs.add(new MapLocation(droneMock.hqLoc.x, topCorner.y - droneMock.hqLoc.y));

        droneMock.composeEnemyHQLocations();*/
        //verify(droneMock.enemyHQlocs);
        //assertTrue(true == true);
    }

    @Test // true branch
    public void goToHQLocationsTest1() throws GameActionException {
        MapLocation enemyHQLocation = new MapLocation(1, 1);
        //when(rcMock.getLocation().distanceSquaredTo(enemyHQLocation) > 20).thenReturn(true);
        //navMock.droneMove(rcMock.getLocation().directionTo(enemyHQLocation));
        boolean result = droneMock.goToHQLocations(0);
        //assertTrue(result);
    }

    @Test // false branch
    public void goToHQLocationsTest2() throws GameActionException {
        MapLocation enemyHQLocation = new MapLocation(1, 1);
        //when(rcMock.getLocation().distanceSquaredTo(enemyHQLocation) > 20).thenReturn(true);
        //navMock.droneMove(rcMock.getLocation().directionTo(enemyHQLocation));
        boolean result = droneMock.goToHQLocations(0);
        //assertTrue(result);
    }


    @Test
    public void circleHQandPickUpTest() throws GameActionException {
        int closestSpot = droneMock.closestCircleSpot();
        List<MapLocation> droneCircle = null;
        //when(rcMock.getLocation().distanceSquaredTo(enemyHQLocation) > 20).thenReturn(true);
        //navMock.droneMove(rcMock.getLocation().directionTo(enemyHQLocation));
        boolean result = droneMock.circleHQandPickUp();
        //assertTrue(result);
    }

    @Test
    public void initializeCircleTest() {
        //List droneCircle = new Mock
        //droneMock.initializeCircle();
        //droneMock.droneCircle = new ArrayList<MapLocation>();
        //verify(droneMock).droneCircle.add(new MapLocation(droneMock.enemyHQ.x-2, droneMock.enemyHQ.y));
        //assertTrue();
    }

    @Test
    public void closestCircleSpot() {
        int result = droneMock.closestCircleSpot();
        boolean res = result == 0;
        assertTrue(res);
    }
}
