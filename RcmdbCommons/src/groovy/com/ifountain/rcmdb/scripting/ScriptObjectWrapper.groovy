package com.ifountain.rcmdb.scripting

import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 9:35:08 AM
* To change this template use File | Settings | File Templates.
*/
public class ScriptObjectWrapper extends Script {
    Script scriptObject;

    protected ScriptObjectWrapper(Script scriptObject) {
        this.scriptObject = scriptObject;
    }

    protected ScriptObjectWrapper(Script scriptObject, Binding binding) {
        this(scriptObject);
        setBinding(binding);
    }

    public Binding getBinding() {
        return this.scriptObject.getBinding();
    }

    public void setBinding(Binding binding) {
        this.scriptObject.setBinding(binding);
    }

    public Object getProperty(String s) {
        return this.scriptObject.getProperty(s);
    }

    public void setProperty(String s, Object o) {
        this.scriptObject.setProperty(s, o);
    }

    public Object invokeMethod(String s, Object o) {
        def tmpScript = this.scriptObject;
        return ExecutionContextManagerUtils.executeInContext([:]){
            addLoggerToContext();
            return tmpScript.invokeMethod(s, o);
        }
    }

    public void println() {
        this.scriptObject.println();
    }

    public void print(Object o) {
        this.scriptObject.print(o);
    }

    public void println(Object o) {
        this.scriptObject.println(o);
    }

    public Object evaluate(String s) {
        throw new Exception("Not supported");
    }

    public Object evaluate(File file) {
        throw new Exception("Not supported");
    }

    public void run(File file, String[] strings) {
        throw new Exception("Not supported");
    }

    public Object run() {
        def tmpScript = this.scriptObject;
        try{
            return ExecutionContextManagerUtils.executeInContext([:]){
                addLoggerToContext();
                return tmpScript.run();
            }
        }
        catch(com.ifountain.rcmdb.scripting.ScriptStoppedByUserException e)
        {
            return e.getMessage();    
        }
    }

    private void addLoggerToContext()
    {
        try{
            def logger = this.scriptObject[RapidCMDBConstants.LOGGER]
            ExecutionContextManagerUtils.addLoggerToCurrentContext (logger);
        }catch(groovy.lang.MissingPropertyException e){};
    }

}