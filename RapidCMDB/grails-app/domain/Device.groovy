class Device extends Resource{
    String vendor;
    String model;
    String location;
    String ipaddress;

    static transients = ["vendor", "model", "location", "ipaddress","operationalstate"];

    static dataSources =
    [
        "DeviceDS":
        [
            master: false,
            keys:
            [
                name:["nameInDs":"ID"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            vendor:['datasourceProperty':"dsname"],
            model:['datasourceProperty':"dsname"],
            location:['datasourceProperty':"dsname"],
            ipaddress:['datasource':"DeviceDS"]
    ];

}
