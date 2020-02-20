package team4player;

import battlecode.common.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.*;
import org.mockito.*;
import org.mockito.junit.*;


public class NavigationTest {
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @InjectMocks
    Navigation navMock = new Navigation(rcMock);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws GameActionException{
        when(rcMock.isReady()).thenReturn(true);
        when(rcMock.canMove(Direction.CENTER)).thenReturn(true);
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.senseFlooding(new MapLocation(5, 5))).thenReturn(false);
    }

    @Test
    public void tryMoveTest() throws GameActionException {
        boolean result = navMock.tryMove();
        assertEquals(result, false);
    }

    @Test
    public void tryMoveDirectionTest() throws GameActionException {
        boolean result = navMock.tryMove(Direction.CENTER);
        assertEquals(result, true);
    }

    @Test
    public void tryMoveMapLocationTest() throws GameActionException {
        boolean result = navMock.tryMove(Direction.NORTH);
        assertEquals(result, false);
    }

    @Test
    public void goToDirectionTest() throws GameActionException {
        boolean result = navMock.goTo(Direction.CENTER);
        assertEquals(true, result);
    }

    @Test
    public void goToMapLocationTest() throws GameActionException {
        //boolean result = navMock.goTo(new MapLocation(5, 5));
        boolean result = navMock.goTo(Direction.CENTER);
        assertEquals(true, result);
    }

    @Test
    public void goAroundMapLocationTest() throws GameActionException {
        boolean result = navMock.goAround(new MapLocation(5, 5));
        assertEquals(true, result);
    }

    @Test
    public void droneMoveTest() throws GameActionException {
        boolean result = navMock.droneMove(Direction.CENTER);
        assertEquals(true, result);
    }
}
