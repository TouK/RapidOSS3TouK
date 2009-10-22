package com.ifountain.rcmdb.scripting
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 25, 2009
 * Time: 2:39:52 AM
 * To change this template use File | Settings | File Templates.
 */
class ScriptStateManager {

    private static final String IS_STOPPED_PROPERTY="IS_STOPPED";
    private static final String STATE_OBJECT_PROPERTY="STATE_OBJECT";

    private static ScriptStateManager manager;
    private static Object getInstanceLock = new Object();

    private Map scriptStopStates;
    private Object stopStateLock;


    private ScriptStateManager() {
    }

    public static ScriptStateManager getInstance() {
        synchronized (getInstanceLock)
        {
            if (manager == null) {
                manager = new ScriptStateManager();
            }
            return manager;
        }
    }
    public static void destroyInstance() {
        synchronized (getInstanceLock)
        {
            if (manager != null) {
                manager.destroy();
                manager = null;
            }
        }
    }

     public void initialize() {
        scriptStopStates=[:];
        stopStateLock=new Object();
     }
    public def addStateParamToBindings(String scriptName,bindings)
    {
        createStopStateIfNotExists(scriptName);
        bindings[STATE_OBJECT_PROPERTY]=scriptStopStates[scriptName];

    }
    private def createStopStateIfNotExists(String scriptName)
    {
        synchronized (stopStateLock)
        {
            if(!scriptStopStates.containsKey(scriptName))
            {
               createDefaultStopState(scriptName);
            }
        }
    }
    private def createDefaultStopState(String scriptName)
    {
        def stateMap=[:];
        stateMap[IS_STOPPED_PROPERTY]=false;
        scriptStopStates[scriptName]=stateMap;
    }

    private def getStopStateOfStateObject(stateMap)
    {
        synchronized (stopStateLock)
        {
            return stateMap.get(IS_STOPPED_PROPERTY);
        }
    }
    public def stopRunningScripts(scriptName)
    {
       synchronized (stopStateLock)
       {
            def oldStateMap=scriptStopStates[scriptName];
            if(oldStateMap){
                oldStateMap[IS_STOPPED_PROPERTY]=true;
            }
            createDefaultStopState(scriptName);
       }
    }

    public boolean isScriptStopped(scriptObject)
    {
        def stateObject=scriptObject.getProperty(STATE_OBJECT_PROPERTY);
        return getStopStateOfStateObject(stateObject);
    }

    private void destroy() {
        if(scriptStopStates != null)
            scriptStopStates.clear();
    }
}