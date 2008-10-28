package datasource
import com.ifountain.smarts.datasource.BaseNotificationAdapter
import com.ifountain.smarts.datasource.SmartsNotificationListeningAdapter
import connection.SmartsConnection
import org.apache.log4j.Logger
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Sep 11, 2008
 * Time: 6:03:34 PM
 * To change this template use File | Settings | File Templates.
 */
class SmartsNotificationDatasourceOperations extends BaseListeningDatasourceOperations{
    def adapter;
    def onLoad(){
        this.adapter = new BaseNotificationAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName){
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName, [propName]);
        if(prop)
        {
            return prop[propName];
        }
        return "";
    }

    def getProperties(Map keys, List properties){
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName);
        return prop;
    }

    def getListeningAdapter(Map params){
         return new SmartsNotificationListeningAdapter(connection.name, reconnectInterval*1000, params.logger,
                 params.Attributes, params.NotificationList, params.TransientInterval, params.TailMode);
    }

    def addNotification(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        tempParams.remove("ClassName");
        tempParams.remove("InstanceName");
        tempParams.remove("EventName");
        this.adapter.createNotification(className, instanceName, eventName, tempParams);
    }

    def getNotification(Map keys) {
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        Map<String, Object> result = this.adapter.getNotification(keys.ClassName, keys.InstanceName, keys.EventName);
        if(!result){
            result = [:];
        }
        return result;
    }

    def getNotification(Map keys, List attributes) {
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        Map<String, Object> result = this.adapter.getNotification(keys.ClassName, keys.InstanceName, keys.EventName, attributes);
        if(!result){
            result = [:];
        }
        return result;
    }

    def getNotifications(Map keys) {
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName);
    }

    def getNotifications(Map keys, boolean expEnabled) {
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName, expEnabled);
    }

    def getNotifications(Map keys, List properties, boolean expEnabled) {
        checkParams(keys, ["ClassName", "InstanceName", "EventName"]);
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName, properties, expEnabled);
    }


    def updateNotification(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        tempParams.remove("ClassName");
        tempParams.remove("InstanceName");
        tempParams.remove("EventName");
        this.adapter.updateNotification(className, instanceName, eventName, tempParams);
    }

    def archiveNotification(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.archiveNotification(className, instanceName, eventName, user, auditTrailText);
    }

    def clearNotification(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "SourceDomainName", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def source = tempParams.SourceDomainName;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.clearNotification(className, instanceName, eventName, source, user, auditTrailText);
    }

    def acknowledge(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.acknowledge(className, instanceName, eventName, user, auditTrailText);
    }

    def unacknowledge(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.unacknowledge(className, instanceName, eventName, user, auditTrailText);
    }

    def takeOwnership(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.takeOwnership(className, instanceName, eventName, user, auditTrailText);
    }

    def releaseOwnership(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.releaseOwnership(className, instanceName, eventName, user, auditTrailText);
    }

    def addAuditLog(Map params){
        checkParams(params, ["ClassName", "InstanceName", "EventName", "User", "AuditTrailText", "Action"]);
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def auditTrailText = tempParams.AuditTrailText;
        def action = tempParams.Action;
        return this.adapter.addAuditEntry(className, instanceName, eventName, user, auditTrailText, action);
    }

    def checkParams(Map params, List requiredParams)
    {
        requiredParams.each{paramName->
            if(params[paramName] == null)
            {
                throw new Exception("Mandatory parameter ${paramName} can not be null.");
            }
        }
        params.each{paramName, paramsValue->
            if(paramsValue == null)
            {
                throw new Exception("Parameter ${paramName} can not be null.");
            }
        }
    }
    def invokeOperation(className, instanceName, opName, opParams){
        opParams.each{
            throw new Exception("Operation parameters cannot be null.");
        }
        this.adapter.invokeOperation(className, instanceName, opName, opParams);
    }
}