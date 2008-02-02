1. The jdbc module is setup to be dependent on the core module in the IntelliJ setup. Alternately,
  it could reference DOF.jar, which is constructed by building the core jar.

2. It is necessary to have the etc and test_data directories in the classpath, as those files are
   loaded as resources.

3. To setup HSQLDB for running the tests:
  a. Make sure that this is in your sqltool.rc in your home directory
      # This is for a hsqldb Server running with default settings on your local
      # computer (and for which you have not changed the password for "sa").
      urlid localhost-sa
      url jdbc:hsqldb:hsql://localhost
      username sa
      password
  b. In the same dir, check that server.properties shows where the db should go. I've got this:
      server.database.0   file:c:/SDWest/1/accounting_db/accounting_db;ifexists=true

  c. I use these aliases:
      # start the server
      alias start_hsqldb="java -cp c:/dev/hsqldb/lib/hsqldb.jar org.hsqldb.Server"

      # cmd line tool for running queries
      alias localhost_sa="java -jar c:/dev/hsqldb/lib/hsqldb.jar localhost-sa"

      # open up the graphical tool
      alias hsqldb_gui="java -cp c:/dev/hsqldb/lib/hsqldb.jar org.hsqldb.util.DatabaseManager"
