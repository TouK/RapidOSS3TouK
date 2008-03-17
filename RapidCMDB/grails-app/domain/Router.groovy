class Router extends Device{
    static dataSources =
    [
        "smartsTopoDs":
        [
            master:false,
            keys:
            [
                "creationClassName":["nameInDs":"CreationClassName"],
                "name":["nameInDs":"Name"]
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
    
}
