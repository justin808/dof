Instructions for creating a file release.
By Justin Gordon, March 27, 2008


The core module contains the core files for building the DOF. Run ant build.xml to create the
DOF.jar.

(note, use ant 1.7.0 or later)

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
   In another shell
   > ant create.schema
6. Open eclipse and import projects from $TEMP/DOF-release
7. Open up class AllTestSuite and run as JUNit test

If all went well, the JUnit tests ran.


** Miscellaneous

* Eclipse
  * Make Eclipse ignore .svn directories
    Window -> Preferences..., Java -> Compiler -> Building. Under "Output folder" add ", .svn/" to "Filtered Resources" (so that you get "*.launch, .svn/").