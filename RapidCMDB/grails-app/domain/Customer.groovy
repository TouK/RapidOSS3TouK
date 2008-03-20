class Customer {
    String name;
    String accountmanager;
    static hasMany = [slas:Sla];
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
        "CustomerDS":     
        [
            master:false,
            keys:
            [
                name:["nameInDs":"name"]
            ]
        ]
    ];
    
    static propertyConfiguration =
    [
            name:['datasource':"RCMDB"],
            accountmanager:['datasource':"CustomerDS", 'nameInDs':'manager']  //lazy false icin ne yapilacak??
    ];

    static transients = ["accountmanager"];
}
  