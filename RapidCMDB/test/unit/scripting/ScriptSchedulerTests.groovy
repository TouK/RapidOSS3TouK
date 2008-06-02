package scripting

import org.quartz.StatefulJob
import org.quartz.JobExecutionContext
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.quartz.impl.StdSchedulerFactory
import org.quartz.Scheduler
import com.ifountain.rcmdb.scripting.ScriptScheduler
import java.text.ParseException;

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
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 27, 2008
 * Time: 4:40:49 PM
 */
class ScriptSchedulerTests extends RapidCmdbTestCase {
    private Scheduler qScheduler;
    private ScriptScheduler scriptScheduler;
    public static int executionCount = 0;
    protected void setUp() {
        super.setUp();
        executionCount = 0;
        qScheduler = StdSchedulerFactory.getDefaultScheduler();
        qScheduler.start();
        scriptScheduler = ScriptScheduler.getInstance();
        scriptScheduler.initialize(qScheduler);
        scriptScheduler.setScriptExecutorClass(MockQuartzJob.class);
    }

    protected void tearDown() {
        if (qScheduler != null) {
            qScheduler.shutdown();
        }
        super.tearDown();
    }

    public void testScheduleScriptWithPeriod()
    {
        scriptScheduler.scheduleScript("myScript", 0, 1);
        Thread.sleep(200);
        assertEquals(1, executionCount);
        Thread.sleep(1000);
        assertEquals(2, executionCount);
    }

    public void testScheduleScriptWithPeriodAndStartDelay()
    {
        scriptScheduler.scheduleScript("myScript", 1, 1);
        Thread.sleep(200);
        assertEquals(0, executionCount);
        Thread.sleep(1000);
        assertEquals(1, executionCount);
    }

    public void testScheduleScriptWithCronExpression()
    {
        scriptScheduler.scheduleScript("myScript", 0, "* * * * * ?");
        Thread.sleep(200);
        assertEquals(1, executionCount);
        Thread.sleep(1000);
        assertEquals(2, executionCount);
    }

    public void testScheduleScriptWithCronExpressionAndStartDelay()
    {
        scriptScheduler.scheduleScript("myScript", 1, "* * * * * ?");
        Thread.sleep(200);
        assertEquals(0, executionCount);
        Thread.sleep(1000);
        assertEquals(1, executionCount);
    }

    public void testScheduleScriptWithTheSameNameThrowsException()
    {
        scriptScheduler.scheduleScript("myScript", 0, 1);
        try {
            scriptScheduler.scheduleScript("myScript", 0, 2);
            fail("should throw exception");
        }
        catch (org.quartz.ObjectAlreadyExistsException e) {
        }
    }

    public void testUnscheduleScript()
    {
        try{
            scriptScheduler.unscheduleScript("myScript");
        }
        catch(e){
            fail("should not throw exception");
        }
        scriptScheduler.scheduleScript("myScript", 0, 1);
        Thread.sleep(100);
        assertEquals(1, executionCount);
        scriptScheduler.unscheduleScript("myScript");
        int oldExecutionCount = executionCount;
        Thread.sleep(1200);
        assertEquals(oldExecutionCount, executionCount);
    }

    public void testInvalidCronExpressionThrowsException()
    {
        try{
            scriptScheduler.scheduleScript("myScript", 0, "invalidCronExp") ;
            fail("should throw exception")
        }
        catch(e){
            assertTrue(e instanceof ParseException)
        }
    }

}
class MockQuartzJob implements StatefulJob {
    public void execute(JobExecutionContext jobExecutionContext) {
        ScriptSchedulerTests.executionCount++;
    }
}

