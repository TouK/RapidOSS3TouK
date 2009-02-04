package com.ifountain.rcmdb.scripting
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 4, 2009
 * Time: 1:02:04 PM
 * To change this template use File | Settings | File Templates.
 */
class ScriptOperationLoader {
     //static Map delegatePool=[:]
    public static void addOperationSupport(Class domainClass, Class operationClass)
    {
        MetaClass mc = domainClass.metaClass;
        mc."${AbstractScriptOperation.OPERATION_PROPERTY_NAME}"=null;

        mc.methodMissing = {String name, args ->
            def oprInstance = delegate[AbstractScriptOperation.OPERATION_PROPERTY_NAME];
            //def oprInstance = delegatePool[domainClass.getName()]
            if (oprInstance == null)
            {
                oprInstance = operationClass.newInstance();
                operationClass.metaClass.getMetaProperty("domainObject").setProperty(oprInstance, delegate);
                //delegatePool[domainClass.getName()]=oprInstance;
                delegate[AbstractScriptOperation.OPERATION_PROPERTY_NAME] = oprInstance;
            }
            try {
                return oprInstance.invokeMethod(name, args)
            } catch (MissingMethodException e) {
                if (e.getType().name != oprInstance.class.name || e.getMethod() != name)
                {
                    throw e;
                }
            }
            throw new MissingMethodException(name, mc.theClass, args);
        }
        mc.'static'.methodMissing = {String methodName, args ->
            try {
                return operationClass.metaClass.invokeStaticMethod(operationClass, methodName, args);
            } catch (MissingMethodException e) {
                if (e.getType().name != operationClass.name || e.getMethod() != methodName)
                {
                    throw e;
                }
            }
            throw new MissingMethodException(methodName, mc.theClass, args);
        }

    }
}