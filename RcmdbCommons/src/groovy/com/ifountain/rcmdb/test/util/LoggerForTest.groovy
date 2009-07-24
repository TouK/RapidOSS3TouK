package com.ifountain.rcmdb.test.util
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 24, 2009
 * Time: 11:52:03 AM
 * To change this template use File | Settings | File Templates.
 */


class LoggerForTest
{
    def logHistory=null;
    public LoggerForTest()
    {
        clear();
    }
    def info(message)
    {
        logHistory["INFO"].add(message);
    }
    def debug(message)
    {
        logHistory["DEBUG"].add(message);
    }
    def warn(message)
    {
        logHistory["WARN"].add(message);
    }
    def warn(message,exception)
    {
        logHistory["WARN"].add([message:message,exception:exception]);
    }
    def clear()
    {
        logHistory=[:];
        logHistory["DEBUG"]=[];
        logHistory["INFO"]=[];
        logHistory["WARN"]=[];
    }

    def isDebugEnabled()
    {
        return true;
    }

}