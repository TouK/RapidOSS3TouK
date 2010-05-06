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
/*
 * Created on Aug 31, 2007
 *
 */
package com.ifountain.core.test.util;

import java.util.Locale;

import com.ifountain.comp.test.util.CommonTestUtils;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.comp.test.util.testcase.RapidTestCase;
import com.ifountain.core.connection.ConnectionManager;


public class RapidCoreTestCase extends RapidTestCase
{
    private static boolean isSetupCompleted = false;
    public static String TEST_PROPERTIES_FILE = "Test.properties";
    
    /**
     * 
     */
    public RapidCoreTestCase()
    {
        super();
        if(!isSetupCompleted)
        {
            isSetupCompleted = true;
            CommonTestUtils.initializeFromFile(TEST_PROPERTIES_FILE);
            Locale.setDefault(Locale.ENGLISH);
        }
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        ConnectionManager.initialize(TestLogUtils.log, DatasourceTestUtils.getParamSupplier(), Thread.currentThread().getContextClassLoader(), 1000);
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        ConnectionManager.destroy();
    }
}
