package datasource

import com.ifountain.smarts.datasource.BaseNotificationAdapter
import com.ifountain.smarts.datasource.SmartsNotificationListeningAdapter
import connection.SmartsConnection
import org.apache.log4j.Logger


class SmartsNotificationDatasource extends BaseListeningDatasource{
    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    SmartsConnection connection ;
    int reconnectInterval = 0;
    

    static hasMany = [:]
    
    static constraints={
    connection(nullable:true)
        
     
    }

    static mappedBy=["connection":"smartsNotificationDatasources"]
    static belongsTo = []
    def adapter;
    static transients =  ['adapter']

    def onLoad = {
        this.adapter = new BaseNotificationAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger());
    }

    def getProperty(Map keys, String propName){
        def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName, [propName]);
        if(prop)
        {
            return prop[propName];
        }
        return "";
    }

    def getProperties(Map keys, List properties){
        def prop = this.adapter.getNotification (keys.ClassName, keys.InstanceName, keys.EventName);
        return prop;
    }

    def getListeningAdapter(Map params){
         return new SmartsNotificationListeningAdapter(connection.name, reconnectInterval*1000, Logger.getRootLogger(),
                 params.Attributes, params.NotificationList, params.TransientInterval, params.TailMode);
    }

    def addNotification(Map params){
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
        Map<String, Object> result = this.adapter.getNotification(keys.ClassName, keys.InstanceName, keys.EventName);
        if(!result){
            result = [:];
        }
        return result;
    }

    def getNotification(Map keys, List attributes) {
        Map<String, Object> result = this.adapter.getNotification(keys.ClassName, keys.InstanceName, keys.EventName, attributes);
        if(!result){
            result = [:];
        }
        return result;
    }

    def getNotifications(Map keys) {
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName);
    }

    def getNotifications(Map keys, boolean expEnabled) {
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName, expEnabled);
    }

    def getNotifications(Map keys, List properties, boolean expEnabled) {
        return this.adapter.getNotifications(keys.ClassName, keys.InstanceName, keys.EventName, properties, expEnabled);
    }


    def updateNotification(Map params){
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
        def tempParams = [:];
        tempParams.putAll(params);
        def className = tempParams.ClassName;
        def instanceName = tempParams.InstanceName;
        def eventName = tempParams.EventName;
        def user = tempParams.User;
        def source = tempParams.Source;
        def auditTrailText = tempParams.AuditTrailText;
        return this.adapter.clearNotification(className, instanceName, eventName, source, user, auditTrailText);
    }

    def acknowledge(Map params){
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
    
    def invokeOperation(className, instanceName, opName, opParams){
        this.adapter.invokeOperation(className, instanceName, opName, opParams);
    }

}
