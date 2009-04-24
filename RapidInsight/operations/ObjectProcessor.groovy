/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Apr 14, 2009
 * Time: 4:57:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectProcessor {
    static final def defaultBeforeProcessors=[];
    static final def defaultAfterProcessors=["StateCalculator"];

    static def beforeProcessors=[];
    static def afterProcessors=["StateCalculator"];

    static def clearProcessors()
    {
        beforeProcessors.clear();
        afterProcessors.clear();
    }
    static def setToDefaultProcessors()
    {
        beforeProcessors=defaultBeforeProcessors.clone();
        afterProcessors=defaultAfterProcessors.clone();
    }


    static def objectInBeforeInsert(object)
    {
        beforeProcessors.each{ procName ->
            def processor=RsUtility.getUtility(procName);
            processor.objectInBeforeInsert(object);
        }
    }
    static def objectInBeforeUpdate(object,params)
    {
        beforeProcessors.each{ procName ->
            def processor=RsUtility.getUtility(procName);
            processor.objectInBeforeUpdate(object,params.updatedProps);
        }
    }

    static def objectIsAdded(object){
        afterProcessors.each{ procName ->
            def processor=RsUtility.getUtility(procName);
            processor.objectIsAdded(object);
        }
    }
    static def objectIsUpdated(object,params){
        afterProcessors.each{ procName ->
            def processor=RsUtility.getUtility(procName);
            processor.objectIsUpdated(object,params.updatedProps);
        }
    }
    static def objectIsDeleted(object){
        afterProcessors.each{ procName ->
            def processor=RsUtility.getUtility(procName);
            processor.objectIsDeleted(object);
        }
    }

}