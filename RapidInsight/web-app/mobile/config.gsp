<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 14, 2009
  Time: 3:44:14 PM
--%>

<%
    CONFIG.SEVERITY_MAPPING = [
            "default": "images/mobile/states/green.png",
            "0": "images/mobile/states/green.png",
            "1": "images/mobile/states/purple.png",
            "2": "images/mobile/states/blue.png",
            "3": "images/mobile/states/yellow.png",
            "4": "images/mobile/states/orange.png",
            "5": "images/mobile/states/red.png"
    ]
    
    CONFIG.EVENT_DATE_PROPERTIES = ["createdAt", "changedAt", "clearedAt", "willExpireAt"];
    CONFIG.HISTORICAL_EVENT_DATE_PROPERTIES = ["createdAt", "changedAt", "clearedAt", "willExpireAt"];
    CONFIG.INVENTORY_DATE_PROPERTIES = ["lastChangedAt", "consideredDownAt"];

    CONFIG.EVENT_COLUMNS = [
        [propertyName:"name", title:"Name"],
        [propertyName:"acknowledged", title:"Ack"],
        [propertyName:"owner", title:"Owner"],
        [propertyName:"source", title:"Source"]
    ]

    CONFIG.HISTORICAL_EVENT_COLUMNS = [
        [propertyName:"name", title:"Name"],
        [propertyName:"acknowledged", title:"Ack"],
        [propertyName:"owner", title:"Owner"],
        [propertyName:"source", title:"Source"]
    ]

    CONFIG.INVENTORY_COLUMNS = [
        [propertyName:"className", title:"Class"],
        [propertyName:"name", title:"Name"],
        [propertyName:"description", title:"Descr"],
        [propertyName:"isManaged", title:"Managed"]
    ]
    
    CONFIG.EVENT_CLASS = RsEvent;
    CONFIG.HISTORICAL_EVENT_CLASS = RsHistoricalEvent;

    CONFIG.EVENT_ACTIONS = [
        [
                scriptName:"acknowledge",
                title:"Acknowledge",
                visible:{event ->
                    return event.acknowledged == false;
                },
                parameters:{event ->
                     return [name:event.name, acknowledged:"true"]
                }
        ],
        [
                scriptName:"acknowledge",
                title:"Unacknowledge",
                visible:{event ->
                    return event.acknowledged == true;
                },
                parameters:{event ->
                     return [name:event.name, acknowledged:"false"]
                }
        ],
        [
                scriptName:"setOwnership",
                title:"Take Ownership",
                parameters:{event ->
                     return [name:event.name, act:"true"]
                }
        ],
        [
                scriptName:"setOwnership",
                title:"Release Ownership",
                parameters:{event ->
                     return [name:event.name, act:"false"]
                }
        ]

    ];
    CONFIG.INVENTORY_ACTIONS = [];
%>