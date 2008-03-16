class Customer {
    static dataSources =
    [
        "otherDbms":
        [
            master:false,
            keys:
            [
                ssn:["nameInDs":"ssn"]
            ]                              
        ]
    ];
    
    static propertyConfiguration =
    [
            name:['datasource':"otherDbms", 'nameInDs':'name'],
            surname:['datasource':"otherDbms", 'nameInDs':'surname']
    ];

    static transients = ["name","surname"]
    
    Integer ssn;
    String name;
    String surname;

}
  