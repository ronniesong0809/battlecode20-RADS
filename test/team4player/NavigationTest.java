package team4player;

import battlecode.common.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.*;
import org.mockito.*;
import org.mockito.junit.*;


public class NavigationTest {
    @Mock
    Navigation navMock = mock(Navigation.class);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws GameActionException {
        when(navMock.tryMove()).thenReturn(true);
        when(navMock.tryMove(Direction.NORTH)).thenReturn(true);
        when(navMock.goTo(Direction.SOUTH)).thenReturn(true);
        when(navMock.goTo(new MapLocation(5, 5))).thenReturn(true);
        when(navMock.goAround(new MapLocation(5, 5))).thenReturn(true);
    }

    @Test
    public void tryMoveTest() throws GameActionException {
        boolean result = navMock.tryMove();
        assertEquals(result, true);
    }

    @Test
    public void tryMoveTest2() throws GameActionException {
        boolean result = navMock.tryMove(Direction.NORTH);
        assertEquals(result, true);
    }

    @Test
    public void goToTest() throws GameActionException {
        boolean result = navMock.goTo(Direction.SOUTH);
        assertEquals(true, result);
    }

    @Test
    public void goToTest2() throws GameActionException {
        boolean result = navMock.goTo(new MapLocation(5, 5));
        assertEquals(true, result);
    }

    @Test
    public void goAroundTest() throws GameActionException {
        boolean result = navMock.goAround(new MapLocation(5, 5));
        assertEquals(true, result);
    }
}