class Customer {
    String name;
    String accountmanager;
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
        "AccountDBDS":
        [
            master:false,
            keys:
            [
                name:["nameInDs":"code"]
            ]                              
        ]
    ];
    
    static propertyConfiguration =
    [
            name:['datasource':"RCMDB"],
            accountmanager:['datasource':"AccountDBDS", 'nameInDs':'manager']  //lazy false icin ne yapilacak??
    ];

    static transients = ["accountmanager"]
}
  