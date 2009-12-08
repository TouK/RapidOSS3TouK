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
package com.ifountain.rcmdb.domain.method

import com.ifountain.rcmdb.domain.batch.AbstractBatchExecutionManager
import com.ifountain.rcmdb.domain.batch.BatchExecutionContext
import org.apache.log4j.Logger
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: mustafa seker
* Date: Jun 14, 2008
* Time: 4:37:27 AM
* To change this template use File | Settings | File Templates.
*/
class EventTriggeringUtils extends AbstractBatchExecutionManager {
    static final String ONLOAD_EVENT = 'onLoad'
    static final String BEFORE_INSERT_EVENT = 'beforeInsert'
    static final String BEFORE_UPDATE_EVENT = 'beforeUpdate'
    static final String BEFORE_DELETE_EVENT = 'beforeDelete'
    static final String AFTER_INSERT_EVENT = 'afterInsert'
    static final String AFTER_UPDATE_EVENT = 'afterUpdate'
    static final String AFTER_DELETE_EVENT = 'afterDelete'
    private static EventTriggeringUtils instance;
    public static EventTriggeringUtils getInstance() {
        if (instance == null) {
            instance = new EventTriggeringUtils();
        }
        return instance;
    }

    public static void destroy() {
        instance = null;
    }
    private EventTriggeringUtils() {
        batchExecutionContextStorage = new ThreadLocal<BatchExecutionContext>()
    }

    public Object triggerEvent(entity, event) {
        return triggerEvent(entity, event, null);
    }
    public Object triggerEvent(entity, event, Map params) {
        if (event == AFTER_INSERT_EVENT || event == AFTER_DELETE_EVENT || event == AFTER_UPDATE_EVENT) {
            EventTriggerBatchExecutionContext batchContext = (EventTriggerBatchExecutionContext) batchExecutionContextStorage.get();
            if (batchContext) {
                batchContext.triggerEvent(entity, event, params);
                return null;
            }
            else {
                return _triggerEvent(entity, event, params)
            }
        }
        else {
            return _triggerEvent(entity, event, params)
        }
    }

    protected Object _triggerEvent(entity, event, params) {
        try
        {
            if (params != null)
            {
                return entity."${event}Wrapper"(params);
            }
            else
            {
                return entity."${event}Wrapper"();
            }
        }
        catch (MissingMethodException exception)
        {
            if (exception.getMethod() != event && exception.getMethod() != "${event}Wrapper" || exception.getType().name != entity.class.name)
            {
                throw exception;
            }
        }
    }

    protected BatchExecutionContext makeStorageInstance() {
        return new EventTriggerBatchExecutionContext(this);
    }
}

class EventTriggerBatchExecutionContext implements BatchExecutionContext {
    private EventTriggeringUtils triggerUtils;
    private List eventTriggers = new LinkedList();
    Logger logger;
    protected EventTriggerBatchExecutionContext(EventTriggeringUtils utils) {
        triggerUtils = utils;
        logger = createLogger();
    }
    public void batchStarted() {
    }

    public void batchFinished() {
        eventTriggers.each {Map trigger ->
            try{
                triggerUtils._triggerEvent(trigger.entity, trigger.event, trigger.params);    
            }
            catch(Throwable e){
                logger.warn("Exception occurred in ${trigger.event} of ${trigger.entity}. Reason: ${e.toString()}")
            }
        }
        eventTriggers.clear();
    }

    public void triggerEvent(entity, event, params) {
        eventTriggers.add([entity: entity, event: event, params: params])
    }

    private Logger createLogger() {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if (context != null)
        {
            Logger log = context[RapidCMDBConstants.LOGGER];
            if (log == null)
            {
                log = Logger.getLogger(EventTriggerBatchExecutionContext);
            }
            return log;
        }
        else
        {
            return Logger.getLogger(EventTriggerBatchExecutionContext);
        }
    }

}