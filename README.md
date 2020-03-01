# Battlecode20-RADS Scaffold

[![Build Status](https://travis-ci.com/battlecode20-team4/battlecode20-scaffold.svg?branch=master)](https://travis-ci.com/battlecode20-team4/battlecode20-scaffold)
<a href='https://coveralls.io/github/ronniesong0809/battlecode20-RADS?branch=master'><img src='https://coveralls.io/repos/github/ronniesong0809/battlecode20-RADS/badge.svg?branch=master' alt='Coverage Status' /></a>
[![License: AGPL 3.0](https://img.shields.io/badge/License-AGPL--3.0-yellow.svg)](https://github.com/battlecode20-team4/battlecode20-scaffold/blob/master/LICENSE)
[![CodeFactor](https://www.codefactor.io/repository/github/ronniesong0809/battlecode20-rads/badge)](https://www.codefactor.io/repository/github/ronniesong0809/battlecode20-rads)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2916fccd9804435d89f09b3dbb8c9120)](https://app.codacy.com/manual/ronsong/battlecode20-RADS?utm_source=github.com&utm_medium=referral&utm_content=ronniesong0809/battlecode20-RADS&utm_campaign=Badge_Grade_Dashboard)

This is the Battlecode 2020 scaffold, containing an `examplefuncsplayer`. Read:
- https://github.com/ronniesong0809/battlecode20-RADS/blob/refactor/src/team4player/README.md
- https://2020.battlecode.org/getting-started!

### Project Participants
- Alexander Saber
- Daniel Connelly
- Ronnie Song
- Sukanya Kothapally

### Project Structure

- `README.md`
    This file.
- `build.gradle`
    The Gradle build file used to build and run players.
- `src/`
    Player source code.
- `test/`
    Player test code.
- `client/`
    Contains the client.
- `build/`
    Contains compiled player code and other artifacts of the build process. Can be safely ignored.
- `matches/`
    The output folder for match files.
- `maps/`
    The default folder for custom maps.
- `gradlew`, `gradlew.bat`
    The Unix (OS X/Linux) and Windows versions, respectively, of the Gradle wrapper. These are nifty scripts that you can execute in a terminal to run the Gradle build tasks of this project. If you aren't planning to do command line development, these can be safely ignored.
- `gradle/`
    Contains files used by the Gradle wrapper scripts. Can be safely ignored.

## Reference
- Battlecode specification: https://2020.battlecode.org/specs.htm://2020.battlecode.org/specs.html
- Battlecode Javadocs: https://2020.battlecode.org/javadoc/index.html

## License

This program is licensed under the "MIT License". Please
see the file [`LICENSE`](https://github.com/battlecode20-team4/battlecode20-scaffold/blob/master/LICENSE) in the source distribution of this
software for license terms.
