package test

import junit.framework.Test;
import junit.framework.TestSuite;
import datasources.*;
import connections.*;

class AllHTTPGroovyTests extends GroovyTestCase {

	public static Test suite()	{
	    TestSuite suite = new TestSuite(AllHTTPGroovyTests.class.getName());
	    suite.addTestSuite(DoRequestActionTest.class);
	    suite.addTestSuite(HttpConnectionImplTest.class);
	    return suite;
	}

}