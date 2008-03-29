Dependent Object Framework (DOF)

http://sourceforge.net/projects/dof/
Description: http://www.junit.org/node/308
             http://www.justingordon.org

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
* You must use ant 1.7.0 or higher



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
* Changed DependentObjectHandler interface so that FileNameParts is passed into create method. This is 
  useful so that you can easily get the primary key, object type, or file type when doing a create.
* Changed handler_mappings.properties so that it support regular expression matching
* Added system property DOF_DEBUG to get printouts of when the DOF creates and deletes objects
