Description & Background
------------------------------------------------------
This Eclipse project is a sample application that uses DOF in its junit test
cases.



Instructions to Run Sample App / Test Cases in Eclipse
------------------------------------------------------
1) Ensure this project's build path to references the DOF jar or it references
   the DOF core Eclipse project (this means you pulled the project out of
   Subversion).

2) Select the org.doframework.sample.jdbc_app.test.AllTestSuite (it is located
   in the src/junit directory)

3) Right click and from the pop-up menu select Run As > JUnit Test

4) That's it - Eclipse should show the JUnit view and you should see the tests
   run.

5) Please note that not all of the tests use DOF.  Look for JUnit test methods 
   that start with the pattern of testDOFSample - these tests use DOF so look
   at these tests first.



Key Aspects of This Sample
-------------------------------------------------------
0) Not all test cases have been written - we could write more test cases.
   Things also need to be documented so people can read about the code's
   intent.

1) The sample app is not using any persistence framework (e.g. no Hibernate,
   EJBs or anything else).  This  home grown persistence framework is here
   because we know lots of old applications have their own homegrown stuff.
   The DOF development crew support you using persistence frameworks, we
   just have not created a sample yet or tested DOF with a persistence framework.

2) The sample application project has three "source" folders.  They are:
		src/sample_app - This contains our sample application and the applications
						 home grown persistence framework.
		src/junit	   - This contains the junit tests for our sample application.
						 This also contains junit tests that test our project's
						 custom DOF Handlers.
		src/test_data  - This contains the DOF XML files that contain our test data.
						 This also contains our DDL creation script.

2) As mentioned above, some of the JUnit Tests verify this sample application's DOF 
   handlers. Hey, we're following Test Driven Development baby!!!

3) The other JUnit tests either test our sample application's custom persistence 
   framework or our verify it or the tests test our sample application's functionality.
   
3) Notice how the sample app's DOF XML Handlers use the sample app's home
   grown persistence framework.

4) We kept the application really simple so you could focus on the DOF 
   implication aspects.

5) After reviewing this sample you should see how easy and simple DOF is to use!!!