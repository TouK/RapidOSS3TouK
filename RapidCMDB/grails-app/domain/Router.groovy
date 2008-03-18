class Router extends Device{
    static dataSources =
    [
        "smartsTopoDs":
        [
            referenceProperty:"dsName",
            master:false,
            keys:
            [
                "creationClassName":["nameInDs":"CreationClassName"],
                "instanceName":["nameInDs":"Name"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            "location":['datasource':"smartsTopoDs", 'nameInDs':'Location'],
            "model":['datasource':"smartsTopoDs", 'nameInDs':'Model']
    ];

    static transients = ["location","model"]
    String location;
    String model;
    String dsName;
    
}
