class Link extends Resource{
    String memberof;
    static transients = ["memberof"];
    
    static dataSources =
    [
        "LinkDBDS":
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
            memberof:['datasourceProperty':"dsname"]
    ];
}
