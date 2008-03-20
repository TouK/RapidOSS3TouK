class Resource {
    String name;
    String displayname;
    String dsname;
    String operationalstate;

    static transients = ["operationalstate"];
    static constraints = {
        name(blank: false);
    };

    static hasMany = [services:Service, events:Event];

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
        ],
        "DS1":
        [
            master: false,
            keys:
            [
                name:["nameInDs":"name"]
            ]
        ],
        "DS2":
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
            name:['datasource':"RCMDB"],
            displayname:['datasource':"RCMDB"],
            dsname:['datasource':"RCMDB"],
            operationalstate:['datasourceProperty':"dsname"]
    ];

    String toString(){
        return "$name";
    }
}
