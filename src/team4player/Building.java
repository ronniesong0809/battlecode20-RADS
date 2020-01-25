package team4player;
import battlecode.common.*;

public class Building extends Robot{
    public Building(RobotController rc) {
        super(rc);
    }
    public void takenTurn() throws GameActionException{
        super.takeTurn();
    }
}
