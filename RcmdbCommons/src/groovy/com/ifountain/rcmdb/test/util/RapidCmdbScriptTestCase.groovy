package com.ifountain.rcmdb.test.util

import com.ifountain.comp.test.util.logging.TestLogUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 7, 2009
* Time: 2:59:49 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbScriptTestCase extends RapidCmdbWithCompassTestCase{
    Class scriptClass;
    Script scriptInstance;
    public Script loadScript(String path, Map properties = [:])
    {
        scriptClass = gcl.parseClass(new File("${getWorkspaceDirectory()}/${path}"))
        scriptClass.metaClass.IS_STOPPED = {->
            return false;            
        }
        scriptInstance = scriptClass.newInstance();
        scriptInstance.setProperty("logger", TestLogUtils.log);
        scriptInstance.setProperty("params", [:]);
        properties.each{String propName, Object propValue->
            scriptInstance.setProperty(propName, propValue);
        }
        return scriptInstance;
    }
}