class Service {
    String name;
    String manager;
    String status;

    static constraints = {
        name(blank:false);
    }

    static hasMany = [resources:Resource, slas:Sla];
    
    static dataSources =
    [
        "RCMDB":
        [
            master: true,
            keys:
            [
                name:["nameInDs":"name"]
            ]
        ]
    ];

    static propertyConfiguration =
    [
            name:['datasource':"RCMDB"],
            manager:['datasource':"RCMDB"],
            status:['datasource':"RCMDB"]
    ];
}
