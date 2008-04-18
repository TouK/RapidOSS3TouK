package com.ifountain.rcmdb.exception.scripting
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Apr 17, 2008
 * Time: 10:51:01 AM
 * To change this template use File | Settings | File Templates.
 */
class ScriptingException extends Exception{

    public ScriptingException(String message) {
        super(message); //To change body of overridden methods use File | Settings | File Templates.
    }

    public ScriptingException(String message, Throwable cause) {
        super(message, cause); //To change body of overridden methods use File | Settings | File Templates.
    }

    public static  ScriptingException scriptDoesnotExist(scriptName)
    {
        return new ScriptingException("Script ${scriptName} does not exist.")
    }

    public static  ScriptingException runScriptException(scriptName, lineNumber, exception)
    {
        return new ScriptingException("Exception occurred while executing script $scriptName at line $lineNumber . Reason :$exception")
    }

    public static  ScriptingException compileScriptException(scriptName, exception)
    {
        return new ScriptingException("Exception occurred while executing script $scriptName . Reason :$exception")
    }
    
}