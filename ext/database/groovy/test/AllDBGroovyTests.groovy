package test;

import junit.framework.Test;
import junit.framework.TestSuite;
import datasources.*;
import connections.*;

class AllDBGroovyTests extends GroovyTestCase {

	public static Test suite()	{
	    TestSuite suite = new TestSuite(AllDBGroovyTests.class.getName());
	    suite.addTestSuite(DatabaseConnectionImplTest.class);
	    suite.addTestSuite(DatabaseAdapterTest.class);
	    suite.addTestSuite(ExecuteQueryActionTest.class);
	    suite.addTestSuite(ExecuteUpdateActionTest.class);
	    return suite;
	}

}