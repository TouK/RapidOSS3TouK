package com.ifountain.rcmdb.scripting

import org.quartz.Trigger
import org.quartz.CronTrigger
import org.quartz.JobDetail
import org.quartz.Scheduler
import org.quartz.SimpleTrigger

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
 * Time: 3:01:55 PM
 */
class ScriptScheduler {
    private Scheduler qScheduler;
    private static ScriptScheduler scheduler;
    private static Object getInstanceLock = new Object();
    private Class scriptExecutorClass;

    private ScriptScheduler() {
        this.scriptExecutorClass = QuartzScriptJob.class;
    }

    public static ScriptScheduler getInstance() {
        synchronized (getInstanceLock)
        {
            if (scheduler == null) {
                scheduler = new ScriptScheduler();
            }
            return scheduler;
        }
    }


    public static void destroyInstance() {
        synchronized (getInstanceLock)
        {
            if (scheduler != null) {
                scheduler = null;
            }
        }
    }

    public void initialize(Scheduler qScheduler) {
        this.qScheduler = qScheduler;
    }

    public synchronized boolean isScheduled(String scriptName, long startDelay, long period) {
        def trigger = qScheduler.getTrigger(scriptName, null);
        if (trigger instanceof SimpleTrigger)
        {
            return trigger.getRepeatInterval() == period * 1000 && trigger.startDelay == startDelay * 1000
        }
        return false;
    }


    public synchronized boolean isScheduled(String scriptName) {
        return qScheduler.getTrigger(scriptName, null) != null;
    }

    public synchronized boolean isScheduled(String scriptName, long startDelay, String cronExp) {
        def trigger = qScheduler.getTrigger(scriptName, null);
        if (trigger instanceof CronTrigger)
        {
            return trigger.getCronExpression() == cronExp && trigger.startDelay == startDelay *1000    
        }
        return false;
    }
    public synchronized void scheduleScript(String scriptName, long startDelay, long period) {
        JobDetail jobDetail = new JobDetail(scriptName, null, scriptExecutorClass);
        CustomSimpleTrigger trigger = new CustomSimpleTrigger(scriptName, startDelay * 1000, period * 1000);
        scheduleJob(jobDetail, trigger);
    }

    public synchronized void scheduleScript(String scriptName, long startDelay, String cronExp) {
        JobDetail jobDetail = new JobDetail(scriptName, null, scriptExecutorClass);
        CustomCronTrigger cronTrigger = new CustomCronTrigger(scriptName, startDelay * 1000, cronExp);
        scheduleJob(jobDetail, cronTrigger);

    }
    private void scheduleJob(JobDetail jobDetail, Trigger trigger)
    {
        try {
            qScheduler.scheduleJob(jobDetail, trigger);
        }
        catch (org.quartz.ObjectAlreadyExistsException e)
        {
            org.apache.log4j.Logger.getRootLogger().info("[ScriptScheduler]: in scheduleJob, ${trigger.getName()} already scheduled, rescheduling it.");
            qScheduler.rescheduleJob(trigger.getName(), null, trigger)
        }
    }
    public synchronized void unscheduleScript(String scriptName) {
        qScheduler.deleteJob(scriptName, null);

    }

    public void setScriptExecutorClass(Class newScriptExecutorClass) {
        this.scriptExecutorClass = newScriptExecutorClass;
    }

}

class CustomCronTrigger extends CronTrigger
{
    public long startDelay;
    public CustomCronTrigger(String name, long startDelay, String cronExpression) {
        super(name, null, cronExpression); //To change body of overridden methods use File | Settings | File Templates.
        this.startDelay = startDelay;
        setStartTime(new Date(System.currentTimeMillis() + startDelay));
    }

}
class CustomSimpleTrigger extends SimpleTrigger
{

    public long startDelay;
    public CustomSimpleTrigger(String name, long startDelay, long repeatInterval) {
        super(name, null, new Date(System.currentTimeMillis() + startDelay), null, SimpleTrigger.REPEAT_INDEFINITELY, repeatInterval); //To change body of overridden methods use File | Settings | File Templates.
        this.startDelay = startDelay;
    }

}