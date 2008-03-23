1. The jdbc module is setup to be dependent on the core module in the IntelliJ setup. Alternately,
  it could reference DOF.jar, which is constructed by building the core jar.

2. It is necessary to have the etc and test_data directories in the classpath, as those files are
   loaded as resources.

3. I added ant tasks in the jdbc/build.xml to simplify DB interaction
   a. start.hsqldb -- starts up hsqldb, creating database automatically
   b. start.db.gui -- starts up gui for checking sql
   c. create.schema -- to create the schema after the database is created