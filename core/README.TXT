The core module contains the core files for building the DOF. Run ant build.xml to create the
DOF.jar.

$TOP = top level directory
$TEMP = temp directory

Instructions for creating a release:
1. run
   > cd $TOP/core
   > ant dof.release
2. Make sure there were no errors
3. Extract the zip file in $TOP/release to $TEMP
4. Open up the projects in $TEMP/DOF-release in Eclipse.
5. run ant tasks to start hsqldb:
   > cd $TOP/jdbc
   > ant start.hsqldb
   > 