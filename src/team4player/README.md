# team4player
[![Build Status](https://travis-ci.com/battlecode20-team4/battlecode20-scaffold.svg?branch=master)](https://travis-ci.com/battlecode20-team4/battlecode20-scaffold)

This is the source code for team *RADS*'s robotplayer.

## Project Structure

- `README.md` This file.
- `Robotplayer.java` is the main class, and the super class of *Robot* Class.
- `Robot.java` is the subclass of *RobotPlayer* Class, and the super class of *Unit* Class and *Building*.
- `Unit.java` is the subclass of *Robot* Class, and the super class of *Miner*, *Landscaper*, and *Drone* Classes.
- `Building.java` is the subclass of *Robot* Class, and the super class of *HQ*, *DesignSchool*, *Refinery*, *Vaporator*, *FulfillmentCenter*, and *Netgun* Classes.

### Units
- `Miner.java` contains functions for [*miner*](https://2020.battlecode.org/specs.html#units).
- `Landscaper.java` contains functions for [*landscpaer*](https://2020.battlecode.org/specs.html#units).
- `Drone.java` contains functions for [*delivery drone*](https://2020.battlecode.org/specs.html#units).

### Buildings
- `HQ.java` contains functions for [*headquarter*](https://2020.battlecode.org/specs.html#buildings).
- `DesignSchool.java` contains functions for [*design school*](https://2020.battlecode.org/specs.html#buildings).
- `Refinery.java` contains functions for [*refinery*](https://2020.battlecode.org/specs.html#buildings).
- `Vaporator.java` contains functions for [*vaporator*](https://2020.battlecode.org/specs.html#buildings).
- `FulfillmentCenter.java` contains functions for [*fulfillment center*](https://2020.battlecode.org/specs.html#buildings).
- `Netgun.java` contains functions for [*net gun*](https://2020.battlecode.org/specs.html#buildings).

### Uilties
- `Util.java` contains functions for *random helper*.
- `Navigation.java` contains functions for *movement*.
- `Broadcast.java` contains functions for [*blockchain*](https://2020.battlecode.org/specs.html#communication) transaction.

## Objects

```java
rc = new RobotController();
```
This is the RobotController object. You use `rc` to perform actions from this robot, and to get information on its current status.

```java
nav = new Navigation(rc);
```
This is the Navigation object. You use `nav` to perform movement actions for the robot.

```java
bc = new Broadcast(rc);
```
This is the Broadcast object. You use `bc` to perform [Blockchain transaction](https://2020.battlecode.org/specs.html#communication).
