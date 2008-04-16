/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be 
 * noted in a separate copyright notice. All rights reserved.
 * This file is part of RapidCMDB.
 * 
 * RapidCMDB is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */
/**
 * Created on Aug 18, 2006
 *
 * Author Sezgin kucukkaraaslan
 */
package com.ifountain.comp.test;

import junit.framework.Test;
import junit.framework.TestSuite;

      
import com.ifountain.comp.exception.ExceptionMessagesMapTest;
import com.ifountain.comp.test.util.CreateStatementBuilderFactoryTest;
import com.ifountain.comp.test.util.CreateStatementBuilderTest;
import com.ifountain.comp.test.util.MYSQLCreateStatementBuilderTest;
import com.ifountain.comp.test.util.OracleCreateStatementBuilderTest;
import com.ifountain.comp.test.util.RCompTestCase;
import com.ifountain.comp.test.util.XmlTestUtilsTest;
import com.ifountain.comp.test.util.testcase.RapidTestCaseTest;
import com.ifountain.comp.utils.CaseInsensitiveMapTest;
import com.ifountain.comp.utils.FileMergerTest;
import com.ifountain.comp.utils.FileUtilsTest;
import com.ifountain.comp.utils.HttpUtilsTest;
import com.ifountain.comp.utils.RapidConfigTest;
import com.ifountain.comp.utils.StringUtilsTest;
import com.ifountain.comp.cli.CommandLineUtilityTest;

public class AllUnitTests extends RCompTestCase{
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllUnitTests.class);
    }
    
    public static Test suite()
    {
        TestSuite suite = new TestSuite(AllUnitTests.class.getName());
        suite.addTestSuite(StringUtilsTest.class);
        suite.addTestSuite(XmlTestUtilsTest.class);
        suite.addTestSuite(HttpUtilsTest.class);
        suite.addTestSuite(CaseInsensitiveMapTest.class);
        suite.addTestSuite(FileUtilsTest.class);
        suite.addTestSuite(CreateStatementBuilderFactoryTest.class);
        suite.addTestSuite(CreateStatementBuilderTest.class);
        suite.addTestSuite(MYSQLCreateStatementBuilderTest.class);
        suite.addTestSuite(OracleCreateStatementBuilderTest.class);
        suite.addTestSuite(ExceptionMessagesMapTest.class);
        suite.addTestSuite(FileMergerTest.class);
        suite.addTestSuite(RapidConfigTest.class);
        suite.addTestSuite(RapidTestCaseTest.class);
        suite.addTestSuite(CommandLineUtilityTest.class);
        return suite;
    }
}
