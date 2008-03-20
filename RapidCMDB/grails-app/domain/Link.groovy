class Link extends Resource{
    String memberof;
    static transients = ["memberof", "operationalstate"];

     static dataSources =
    [
        "LINKDS":
        [
            master: true,
            keys:
            [
                name:["nameInDs":"ID"]
            ]
        ]
    ];
    static propertyConfiguration =
    [
            memberof:['datasource':"LINKDS"]
    ];
}
