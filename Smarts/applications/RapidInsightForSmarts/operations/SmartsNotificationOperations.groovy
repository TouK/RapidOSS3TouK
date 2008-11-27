import datasource.SmartsNotificationDatasource

class SmartsNotificationOperations extends RsEventOperations
{
    public void clear() {
        def props = asMap();
        props.remove('__operation_class__');
        props.remove('__is_federated_properties_loaded__');
        props.remove('errors');
        props.clearedAt = Date.now()
        props.active = false;
        props["causedBy"] = serializeRelations("causedBy");
        props["causes"] = serializeRelations("causes");
        RsEventJournal.add(eventId: id, eventName: "cleared", rsTime: new Date())
        def historicalEvent = SmartsHistoricalNotification.add(props)

        def journals = RsEventJournal.search("eventId:${id}").results
        journals.each {
            it.eventId = historicalEvent.id
        }
        remove()
    }
    def serializeRelations( relationName)
    {
        def serializedRelation = new StringBuffer();
        getProperty(relationName).each {
            serializedRelation.append(it.name).append(", ")
        }
        if (serializedRelation.length() > 0)
        {
            return serializedRelation.substring(0, serializedRelation.length() - 2);
        }
        return "";
    }

    public void acknowledge(boolean action, userName) {
        SmartsNotificationDatasource ds = SmartsNotificationDatasource.get(name: rsDatasource);
        if (ds == null) throw new Exception("Datasource with name ${rsDatasource} is not defined")
        if (action) {
            ds.acknowledge([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: "Acknowledged by " + userName])
            update(acknowledged: true, owner: userName);
        }
        else {
            ds.unacknowledge([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: "UnAcknowledged by " + userName])
            update(acknowledged: false, owner: userName);
        }
    }

    public void setOwnership(boolean action, userName) {
        SmartsNotificationDatasource ds = SmartsNotificationDatasource.get(name: rsDatasource);
        if (ds == null) throw new Exception("Datasource with name ${rsDatasource} is not defined")
        if (action) {
            ds.takeOwnership([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: "TakeOwnership with user " + userName])
            update(owner: userName);
        }
        else {
            ds.releaseOwnership([ClassName: className, InstanceName: instanceName, EventName: eventName, User: userName, AuditTrailText: "ReleaseOwnership from user " + userName])
            update(owner: "");
        }
    }
}
    