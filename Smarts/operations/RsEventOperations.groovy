import datasource.SmartsNotificationDatasource

class RsEventOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    public void acknowledge(boolean action, userName) {
        SmartsNotificationDatasource ds = SmartsNotificationDatasource.get(name: rsDatasource);
        if (ds == null) throw new Exception("Datasource with name ${rsDatasource} is not defined")
        if (action) {
            ds.acknowledge([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: " Acknowledged by " + userName])
            update(acknowledged: true);
        }
        else {
            ds.unacknowledge([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: " Acknowledged by " + userName])
            update(acknowledged: false);
        }
    }

    public void setOwnership(boolean action, userName) {
        SmartsNotificationDatasource ds = SmartsNotificationDatasource.get(name: rsDatasource);
        if (ds == null) throw new Exception("Datasource with name ${rsDatasource} is not defined")
        if (action) {
            ds.takeOwnership([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: " TakeOwnerwhip with user " + userName])
            update(owner: userName);
        }
        else {
            ds.releaseOwnership([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: " TakeOwnerwhip with user " + userName])
            update(owner: "root");
        }
    }
}
    
