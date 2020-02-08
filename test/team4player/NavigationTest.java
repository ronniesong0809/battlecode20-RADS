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

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    Navigation navigation;

    @Before
    public void setUp() {
        when(rcMock.getTeam()).thenReturn(Team.A);
        when(rcMock.getType()).thenReturn(RobotType.HQ);
        when(rcMock.getID()).thenReturn(12345678);
        when(rcMock.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rcMock.getRoundNum()).thenReturn(50);
    }

    @Test
    public void TryMove_test() throws Exception {
        boolean result = navigation.tryMove(Direction.NORTH);
        assertEquals(false, result);
    }
//
//    @Test
//    public void TryMove_test2() throws Exception {
//        navigation.tryMove(Direction.NORTH);
//        MapLocation result = rcMock.getLocation();
//        assertEquals(result, new MapLocation(5,5));
//    }
//
//    @Test
//    public void GoTo_test() throws Exception {
//        boolean result = navigation.goTo(Direction.NORTH);
//        assertEquals(true, result);
//    }
//
//    @Test
//    public void GoTo_test2() throws Exception {
//        boolean result = navigation.goTo(new MapLocation(5, 6));
//        assertEquals(true, result);
//    }
//
//    @Test
//    public void GoAround_test() throws Exception {
//        boolean result = navigation.goAround(new MapLocation(6, 5));
//        assertEquals(true, result);
//    }
}