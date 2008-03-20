class Device extends Resource{
    String vendor;
    String model;
    String location;
    String ipaddress;

    static transients = ["vendor", "model", "location", "ipaddress"];

    static dataSources =
    [
        "DeviceDBDS":
        [
            master: false,
            keys:
            [
                name:["nameInDs":"ID"]
            ]
        ],
        "IPDS":
        [
            master: false,
            keys:
            [
                name:["nameInDs":"id"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            vendor:['datasourceProperty':"dsname"],
            model:['datasourceProperty':"dsname"],
            location:['datasourceProperty':"dsname"],
            ipaddress:['datasource':"IPDS"]
    ];

}
