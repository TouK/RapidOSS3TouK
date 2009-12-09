package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import javax.servlet.http.HttpServletResponse
import org.apache.log4j.Logger


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 6:32:45 PM
* To change this template use File | Settings | File Templates.
*/
class ExecutionContextManagerUtils {
    
    public static void addObjectToCurrentContext(String key,Object object)
    {
        executeInCurrentContext(){context->
            context[key] = object;
        }
    }
    public static void removeObjectFromCurrentContext(String key)
    {
        executeInCurrentContext(){context->
            context.remove(key);
        }
    }
    public static Object getObjectFromCurrentContext(String key)
    {
        def object=null;
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if(context != null)
        {
            object = context[key];
        }
        return object;
    }


    public static void addUsernameToCurrentContext(String username)
    {
        executeInCurrentContext(){context->
            context[RapidCMDBConstants.USERNAME] = username;
        }
    }
    public static Object getUsernameFromCurrentContext()
    {
        return getObjectFromCurrentContext(RapidCMDBConstants.USERNAME);
    }


    public static void addLoggerToCurrentContext(Logger logger)
    {
        executeInCurrentContext(){context->
            context[RapidCMDBConstants.LOGGER] = logger;
        }
    }
    public static Object getLoggerFromCurrentContext()
    {
        return getObjectFromCurrentContext(RapidCMDBConstants.LOGGER);
    }


    public static void addWebResponseToCurrentContext(HttpServletResponse webResponse )
    {
       executeInCurrentContext(){context->
            context[RapidCMDBConstants.WEB_RESPONSE] = webResponse;
        }
    }
    public static Object getWebResponseFromCurrentContext()
    {
        return getObjectFromCurrentContext(RapidCMDBConstants.WEB_RESPONSE);
    }


    public static Object executeInContext(Map contextProps, Closure closureToBeExecuted)
    {
        ExecutionContextManager.getInstance().startExecutionContext(contextProps);
        try{
            return closureToBeExecuted();
        }finally
        {
            ExecutionContextManager.getInstance().endExecutionContext();
        }
    }

    private static executeInCurrentContext(Closure c)
    {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if (context != null)
        {
            c(context)
        }
    }
}