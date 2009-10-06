import application.RsApplication

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 14, 2009
* Time: 4:57:46 PM
* To change this template use File | Settings | File Templates.
*/
public class EventProcessor {

    static def beforeProcessors=[];
    static def afterProcessors=["StateCalculator"];


    static def eventInBeforeInsert(event)
    {
        beforeProcessors.each{ procName ->
            def processor=RsApplication.getUtility(procName);
            processor.eventInBeforeInsert(event);
        }
    }
    static def eventInBeforeUpdate(event,params)
    {
        beforeProcessors.each{ procName ->
            def processor=RsApplication.getUtility(procName);
            processor.eventInBeforeUpdate(event,params.updatedProps);
        }
    }

    static def eventIsAdded(event){
        afterProcessors.each{ procName ->
            def processor=RsApplication.getUtility(procName);
            processor.eventIsAdded(event);
        }
    }
    static def eventIsUpdated(event,params){
        afterProcessors.each{ procName ->
            def processor=RsApplication.getUtility(procName);
            processor.eventIsUpdated(event,params.updatedProps);
        }
    }
    static def eventIsDeleted(event){
        afterProcessors.each{ procName ->
            def processor=RsApplication.getUtility(procName);
            processor.eventIsDeleted(event);
        }
    }

}