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
            eventText:['datasource':"smartsDs", 'nameInDs':'EventText'],
            name:['datasource':"smartsDs", 'nameInDs':'Name']
    ];

    static transients = ["eventText", "name"]

    String className;
    String instanceName;
    String eventName;
    String name;
    String eventText;
    Integer severity;

    String toString(){
        return "$className::$instanceName::$eventName";
    }
}
