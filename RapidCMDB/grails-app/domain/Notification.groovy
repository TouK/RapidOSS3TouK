class Notification {

    static dataSources =
    [
        "smartsDs":
        [
            master:false,
            keys:
            [
                className:["nameInDs":"ClassName"],
                instanceName:["nameInDs":"InstanceName"],
                eventName:["nameInDs":"EventName"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            eventText:['datasource':"smartsDs", 'nameInDs':'EventText']
    ];

    static transients = ["className","instanceName","eventName"]

    String className;
    String instanceName;
    String eventName;
    String name;
    String eventText;
    Integer severity;
}
