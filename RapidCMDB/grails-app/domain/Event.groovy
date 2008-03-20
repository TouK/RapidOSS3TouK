class Event {
    String name;
    String severity;
    String ack;
    String owner;
    String description;
    String lastOccured;
    String lastChanged;

    Resource resource;
    static belongsTo = Resource;
    static constraints = {
        name(blank:false);
    }
    static transients = ["severity", "ack", "owner", "description", "owner", "lastOccured", "lastChanged"];
     static dataSources =
    [
        "RCMDB":
        [
            master: true,
            keys:
            [
                name:["nameInDs":"name"]
            ]
        ],
        "EVENTDS":
        [
            master: false,
            keys:
            [
                name:["nameInDs":"EventName"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            name:['datasource':"RCMDB"],
            severity:['datasource':"EVENTDS", "nameInDs": "Severity"],
            ack:['datasource':"EVENTDS", "nameInDs": "Acknowledged"],
            owner:['datasource':"EVENTDS", "nameInDs": "Owner"],
            description:['datasource':"EVENTDS", "nameInDs": "Description"],
            lastOccured:['datasource':"EVENTDS", "nameInDs": "LastOccuredAt"],
            lastChanged:['datasource':"EVENTDS", "nameInDs": "LastChangedAt"]
    ];
}
