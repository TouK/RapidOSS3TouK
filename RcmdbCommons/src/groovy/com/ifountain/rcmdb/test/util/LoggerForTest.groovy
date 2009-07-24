package com.ifountain.rcmdb.test.util

import org.apache.log4j.Logger

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 24, 2009
* Time: 11:52:03 AM
* To change this template use File | Settings | File Templates.
*/


class LoggerForTest  extends Logger
{
    def logHistory=null;
    public LoggerForTest()
    {
        super("logger.for.test");
        clear();
    }
    public void info(Object message)
    {
        logHistory["INFO"].add(message);
    }
    public void debug(Object message)
    {
        logHistory["DEBUG"].add(message);
    }
    public void warn(Object message)
    {
        logHistory["WARN"].add(message);
    }
    public void warn(Object message,exception)
    {
        logHistory["WARN"].add([message:message,exception:exception]);
    }
    public boolean isDebugEnabled()
    {
        return true;
    }

    def clear()
    {
        logHistory=[:];
        logHistory["DEBUG"]=[];
        logHistory["INFO"]=[];
        logHistory["WARN"]=[];
    }



}