package team4player;

import battlecode.common.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.*;
import org.mockito.*;
import org.mockito.junit.*;


public class RobotPlayerTest {
    @Mock
    RobotController rcMock = mock(RobotController.class);

    @InjectMocks
    RobotPlayer rpMock = new RobotPlayer();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
        when(rcMock.getID()).thenReturn(12345678);
        when(rcMock.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rcMock.getRoundNum()).thenReturn(50);
    }

    @Test
    public void getTeamTest() {
        assertEquals(rcMock.getTeam(), Team.A);
    }

    @Test
    public void getTypeTest() {
        assertEquals(rcMock.getType(), RobotType.HQ);
    }

    @Test
    public void getIDTest() {
        assertEquals(rcMock.getID(), 12345678);
    }

    @Test
    public void getLocationTest() {
        assertEquals(rcMock.getLocation(), new MapLocation(0, 0));
    }

    @Test
    public void getRoundNumTest() {
        assertEquals(rcMock.getRoundNum(), 50);
    }

//    @Test
//    public void runTest () throws GameActionException{
//        rpMock.run(rcMock);
//    }
}