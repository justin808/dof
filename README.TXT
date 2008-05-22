Dependent Object Framework (DOF)

http://sourceforge.net/projects/dof/
Description: http://www.junit.org/node/308
             http://www.justingordon.org
Google Group: http://groups.google.com/group/dependent-object-framework



In a nutshell, with this framework,
1. It's OK and EASY to run JUnit tests against the database
2. You don't necessarily need mock objects for doing TDD where there are database dependencies.


Please let me know what you think of this project!
For more info, see article: http://www.junit.org/node/308


Cheers,

Justin Gordon
justingordon at yahoo.com


This release contains:
* core/build/DOF.jar inside the core/build directory. This is the JAR file that you would use in your project
* core/dof_src: source files for the DOF
* javadoc: most of the details are in the Javadoc
* jdbc: example of using the DOF with direct JDBC calls
* idea: project files for IntelliJ
* hibernate: hibernate support not yet implemented (anybody interested in helping me?)

Miscellaneous:
* You must use ant 1.7.0 or higher to build the release



Release 0.9.3
* Fixed bug when grandparent of two parent dependencies not getting deleted

Release 0.9.4
* Changed to use @require("type.pk.extension") rather than $require("type.pk.extension");
* Updated the build file to manage the test database:
   I added ant tasks in the jdbc/build.xml to simplify DB interaction
   a. start.hsqldb -- starts up hsqldb, creating database automatically
   b. start.db.gui -- starts up gui for checking sql
   c. create.schema -- to create the schema after the database is created

Release 0.9.5
* Fixed up Eclipse files
* Enabled defining system variable DOF_DIR which is where the DOF description files can be placed.
  To use this, specify parameter -DDOF_DIR={your_directory}
* Added support for defining a custom FileNamePartsProcessor class in case you want to have a custom
  naming convention.

Release 0.9.6
* Changed handler_mappings.properties so that it support regular expression matching
* Renamed FileNameParts to ObjectFileInfo
* Changed DependentObjectHandler interface so that ObjectFileInfo is passed into create method, rather than
  the pk or fileToLoad. This is useful so that you can easily get the primary key, object type, or file type
  when doing a create, get, or delete, and all of these pieces may be necessary when doing regexp matches.
* Added system property DOF_DEBUG to get printouts of when the DOF creates and deletes objects
* Added method DOF.setDofDebug(boolean b) to turn this on programmatically
* Passing in objectToDelete to DependentObjectHandler.delete() and only calling this method if objectToDelete 
  is not null. Method returns DOF.deleteObject returns true if the object does not exist


Release 0.9.7
* Now supports clear "scratch" objects that have unique primary keys for modifications in tests compared
  to "reference" objects, which were all the objects that the DOF was previously handling.
* IMPORTANT CHANGES FOR PREVIOUS USERS FOR SUPPORT OF SCRATCH OBJECTS:
* Changed ObjectFileInfo class to now require the use of a constructor rather than having public access
  to the members. You'll get a compile error if you have a custom implementor of ObjectFileInfoProcessor
* In order for implementation of DependentObjectHandler to work properly with scratch objects, you MUST
  ONLY get the file contents by calling either ObjectFileInfo.getFileContentsAsString() or 
  ObjectFileInfo.getFileContentsAsInputStream()
