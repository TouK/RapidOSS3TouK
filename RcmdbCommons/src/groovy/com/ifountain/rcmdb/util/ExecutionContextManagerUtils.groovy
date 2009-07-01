package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import javax.servlet.http.HttpServletResponse


/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 6:32:45 PM
* To change this template use File | Settings | File Templates.
*/
class ExecutionContextManagerUtils {
    public static void addUsernameToCurrentContext(String username)
    {
        addToCurrentContext(){context->
            context[RapidCMDBConstants.USERNAME] = username;
        }
    }
    public static void addLoggerToCurrentContext(org.apache.log4j.Logger logger)
    {
        addToCurrentContext(){context->
            context[RapidCMDBConstants.LOGGER] = logger;
        }
    }
    public static void addWebResponseToCurrentContext(HttpServletResponse webResponse )
    {
       addToCurrentContext(){context->
            context[RapidCMDBConstants.WEB_RESPONSE] = webResponse;
        }
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

    private static addToCurrentContext(Closure c)
    {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if (context != null)
        {
            c(context)
        }
    }
}