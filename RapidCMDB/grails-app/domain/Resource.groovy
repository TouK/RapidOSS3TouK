class Resource {
    String name;
    String displayname;
    String dsname;
    String operationalstate;

    static transients = ["operationalstate"];
    static constraints = {
        name(blank: false);
    };

    static hasMany = [services:Service];
    static belongsTo = Service;
    
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
            displayname:['datasource':"RCMDB"],
            dsname:['datasource':"RCMDB"],
            operationalstate:['datasourceProperty':"dsname"]
    ];
}
