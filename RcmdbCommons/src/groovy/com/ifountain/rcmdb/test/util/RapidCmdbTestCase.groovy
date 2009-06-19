/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.testcase.RapidTestCase
import com.ifountain.rcmdb.domain.DomainLockManager
import org.apache.log4j.Logger
import com.ifountain.comp.test.util.CommonTestUtils
import com.ifountain.rcmdb.domain.cache.IdCache

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Apr 18, 2008
* Time: 2:26:55 PM
* To change this template use File | Settings | File Templates.
*/
public class RapidCmdbTestCase extends RapidTestCase{
    def defaultBaseDir;
    public static boolean isSetupCompleted = false;
    protected void setUp() {
        if(!isSetupCompleted)
        {
            isSetupCompleted = true;
            CommonTestUtils.initializeFromFile("RCMDBTest.properties");
            Locale.setDefault(Locale.ENGLISH);
        }
        defaultBaseDir = System.getProperty("base.dir", ".");
        DomainLockManager.initialize(10000, Logger.getRootLogger());
        IdCache.initialize (10000);
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
    }
    

    protected void tearDown() {
        System.setProperty("base.dir", defaultBaseDir);
        TestDatastore.clear();
        DomainLockManager.destroy();
        IdCache.clearCache();
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        System.setProperty("base.dir", defaultBaseDir);
    }

    public String getWorkspacePath() throws Exception
    {
        String canonicalPath = new java.io.File(".").getCanonicalPath();
        String workspacePath=null;
        //to run in developer pc
        if (canonicalPath.endsWith("RapidModules"))
        {
            workspacePath= "..";
        }
        else    //to run in hudson
        {
            workspacePath= "../../..";
        }
        return workspacePath;
    }
    public java.io.File getWorkspaceDirectory() throws Exception
    {
        return new java.io.File(getWorkspacePath());
    }

}
