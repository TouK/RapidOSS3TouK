YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.Config = new function() {
    this.config = {};
    this.loadMetaData = function(response) {
        var getAttributes = function(xmlNode) {
            var atts = {};
            var attributeNodes = xmlNode.attributes;
            if (attributeNodes != null)
            {
                var nOfAtts = attributeNodes.length
                for (var index = 0; index < nOfAtts; index++) {
                    var attNode = attributeNodes.item(index);
                    atts[attNode.nodeName] = attNode.nodeValue;
                }
            }
            return atts;
        }

        var config = {};
        var items = response.responseXML.getElementsByTagName('UiElement');
        for (var i = 0; i < items.length; i++) {
            var itemNode = items[i];
            var itemType = itemNode.getAttribute('designerType');
            var itemConfig = getAttributes(itemNode);
            var children = itemNode.getElementsByTagName('Child');
            var childrenConfig = {};
            for (var j = 0; j < children.length; j++) {
                var childNode = children[j]
                var childType = childNode.getAttribute('designerType');
                var childConfig = getAttributes(childNode);
                childrenConfig[childType] = childConfig;
            }
            itemConfig['children'] = childrenConfig;
            var properties = itemNode.getElementsByTagName('Property');
            var propsConfig = {};
            for (var j = 0; j < properties.length; j++) {
                var propertyNode = properties[j]
                var propertyName = propertyNode.getAttribute('name');
                var propertyConfig = getAttributes(propertyNode);
                propsConfig[propertyName] = propertyConfig;
            }
            itemConfig['properties'] = propsConfig;
            config[itemType] = itemConfig;
        }
        this.config = config;
    };
    this.get = function(itemType) {
        return this.config[itemType];
    };
    this.getDisplayName = function(itemType, xmlNode) {
        var displayFromProperty = this.get(itemType)["displayFromProperty"]
        if (displayFromProperty) {
            if (xmlNode) {
                return xmlNode.getAttribute(displayFromProperty)
            }
            else {
                return itemType;
            }
        }
        else {
            return this.get(itemType)["display"];
        }

    };
    this.canBeDeleted = function(itemType) {
        return this.get(itemType)["canBeDeleted"] == 'true';
    };
    this.getProperties = function(itemType) {
        return this.get(itemType)["properties"];
    };
    this.isDisplayProperty = function(itemType, propertyName) {
        var displayFromProperty = this.get(itemType)["displayFromProperty"]
        return displayFromProperty == propertyName
    };
    this.getChildren = function(itemType) {
        return this.get(itemType)["children"]
    };
    this.isChildMultiple = function(itemType, childType) {
        return this.get(itemType)['children'][childType]['isMultiple'] == 'true'
    };
    this.getPropertyDescription = function(itemType, propertyName) {
        if (itemType == "Layout" && propertyName == "type") {
            return "Type of the layout"
        }
        return this.get(itemType)["properties"][propertyName]["descr"]
    };
    this.getPropertyDefaultValue = function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["defaultValue"]
    };
    this.isPropertyRequired = function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["required"] == 'true'
    };
    this.getPropertyType = function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["type"]
    };
    this.getPropertyInList = function(itemType, propertyName) {
        var inList = this.get(itemType)["properties"][propertyName]['inList']
        if (inList != null) {
           return inList.split(',');
        }
        return [];
    };

    this.getImageConfig = function(itemType) {
        var itemConfig = this.get(itemType);
        return {
            expanded:itemConfig['imageExpanded'],
            collapsed:itemConfig['imageCollapsed']
        }
    };
    this.getConfig = function() {
        return this.config;
    };
    this.getLayoutTypeNames = function() {
        var names = [];
        for (var layoutType in this.layoutTypes) {
            names[names.length] = layoutType
        }
        return names;
    };
    this.getLayoutType = function(layoutType) {
        return this.layoutTypes[layoutType];
    };
    this.getGlobalEvents = function() {
        return this.events["Global"]
    };
    this.getComponentEvents = function(componentType) {
        return this.events[componentType];
    };
    this.getEventDescription = function(componentType, eventName) {
        return this.events[componentType][eventName]['descr'];
    };
    this.getEventParameters = function(componentType, eventName) {
        return this.events[componentType][eventName]['params'];
    };
    this.getComponentMethods = function(componentType) {
        return this.methods[componentType];
    };
    this.getMethodDescription = function(componentType, methodName) {
        return this.methods[componentType][methodName]['descr'];
    };
    this.getMethodArguments = function(componentType, methodName) {
        return this.methods[componentType][methodName]['args'];
    };
    this.getMenuParameters = function(componentType, menuType) {
        return this.menuParameters[componentType][menuType];
    }
    this.methods = {
        "SearchGrid":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setQueryWithView":{
                descr:"Requests to server with the given query and changes its view.",
                args:{
                    "queryString":"New query to get data from server",
                    "view":"The view name that will be displayed",
                    "title":"Grid's new title",
                    "extraParams":"JavaScript object containing extra request parameters if needed"
                }
            },
            "appendToQuery":{
                descr:"Appends the given query to the current query of the grid and retreives the data from server with the combined query.",
                args:{
                    "query":"Query string to append"
                }
            },
            "appendExceptQuery":{
                descr:"Applies negation to the current query of the grid for given property and value.",
                args:{
                    "property":"The property to be negated.",
                    "value":"Value of the property to be negated."
                }
            },
            "sort":{
                descr:"Sorts the grid with the given property and order.",
                args:{
                    "sortAttribute":"Property to sort the grid according to",
                    "sortOrder":"The order of the sort. Possible values are 'asc' and 'desc'"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "SearchList":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setQuery":{
                descr:"Requests to server with the given query, sort property and sort order.",
                args:{
                    "queryString":"New query to get data from server",
                    "sortAttribute":"Property to sort the search list according to",
                    "sortOrder":"The order of the sort. Possible values are 'asc' and 'desc'",
                    "extraParams":"JavaScript object containing extra request parameters if needed"
                }
            },
            "appendToQuery":{
                descr:"Appends the given query to the current query of the search list and retreives the data from server with the combined query.",
                args:{
                    "query":"Query string to append"
                }
            },
            "appendExceptQuery":{
                descr:"Applies negation to the current query of the search list for given property and value.",
                args:{
                    "property":"The property to be negated.",
                    "value":"Value of the property to be negated."
                }
            },
            "sort":{
                descr:"Sorts the search list with the given property and order.",
                args:{
                    "sortAttribute":"Property to sort the search list according to",
                    "sortOrder":"The order of the sort. Possible values are 'asc' and 'desc'"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "TreeGrid":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "Timeline":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "PieChart":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "FlexPieChart":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "GMap":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "Html":{
            "show":{
                descr:"Retrieves the html content from the given url and changes the title.",
                args:{
                    "url":"Url to retrive html content.",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "AutoComplete":{
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "ObjectMap":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        }
    };
    this.events = {
        "Global":{
            "DOMReady":{descr:"Fired when the HTML DOM is initally usable.", params:{}},
            "errorOccurred":{
                descr:"Fired when an error occurred during a server side AJAX call.",
                params:{
                    "params.messages":"A list of error messages"
                }
            },
            "serverDown":{
                descr:"Fired when the connection is lost with the server.",params:{}
            },
            "serverUp":{
                descr:"Fired when the connection is established with the server.",params:{}
            }
        },
        "SearchGrid":{
            "propertyClicked":{
                descr:"Fired when a cell of the grid is clicked.",
                params:{
                    "params.key":"Name of the property",
                    "params.value":"Value of the property",
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowClicked":{
                descr:"Fired when a row of the grid is clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowDoubleClicked":{
                descr:"Fired when a row of the grid is double clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "selectionChanged":{
                descr:"Fired when the row selection is changed.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked.",
                params:{
                    "params.query":"Query written in search input field"
                }
            }
        },
        "SearchList":{
            "propertyClicked":{
                descr:"Fired when a cell of the grid is clicked.",
                params:{
                    "params.key":"Name of the property",
                    "params.value":"Value of the property",
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowClicked":{
                descr:"Fired when a row of the grid is clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowDoubleClicked":{
                descr:"Fired when a row of the grid is double clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "selectionChanged":{
                descr:"Fired when the row selection is changed.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked.",
                params:{
                    "params.query":"Query written in search input field"
                }
            },
            "rowHeaderClick":{
                descr:"Fired when row header is clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            }
        },
        "TreeGrid":{
            "selectionChanged":{
                descr:"Fired when row selection is changed.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "nodeClicked":{
                descr:"Fired when a row of the treegrid is clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            }
        },
        "Timeline":{
            "tooltipClicked":{
                descr:"Fired when an event tooltip is clicked.",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            }
        },
        "PieChart":{

        },
        "FlexPieChart":{
            "itemClicked":{
                descr:"Fired when a chart slice is clicked",
                params:{
                    "params.data":"JavaScript object representing the slice data"
                }
            }
        },
        "GMap":{
            "markerClicked":{
                descr:"Fired when a location marker is clicked",
                params:{
                    "params.data":"JavaScript object representing the location data"
                }
            }
        },
        "Html":{

        },
        "AutoComplete":{
            "submit":{
                descr:"Fired when a suggestion is selected or \"search\" button is clicked",
                params:{
                    "params.query":"Value in search input field"
                }
            }
        },
        "ObjectMap":{
            "nodeClicked":{
                descr:"Fired when a map node is clicked.",
                params:{
                    "params.data":"JavaScript object representing the node data"
                }
            },
            "mapInitialized":{
                descr:"Fired when a map's flash object is initalized and ready to response to external calls.",
                params:{}
            }
        }
    }

    this.layoutTypes = {
        "TwoColumns":{"CenterUnit":'', "LeftUnit":''},
        "TwoColumnsWithHeader":{"CenterUnit":'',"LeftUnit":'',"TopUnit":''},
        "TwoColumnsWithFooter":{"CenterUnit":'',"LeftUnit":'',"BottomUnit":''},
        "TwoColumnsWithHeaderAndFooter":{"CenterUnit":'',"LeftUnit":'',"TopUnit":'',"BottomUnit":''},
        "TwoColumnsLeftDivided":{"CenterUnit":'',
            "LeftUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "TwoColumnsRightDivided":{"LeftUnit":'',
            "CenterUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "TwoColumnsAllDivided":{
            "LeftUnit":{"TopUnit":'',"CenterUnit":''},
            "CenterUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "ThreeColumns":{"CenterUnit":'',"LeftUnit":'',"RightUnit":''},
        "ThreeColumnsWithHeader":{"CenterUnit":'',"LeftUnit":'',"TopUnit":'', "RightUnit":''},
        "ThreeColumnsWithFooter":{"CenterUnit":'',"LeftUnit":'',"BottomUnit":'', "RightUnit":''},
        "ThreeColumnsWithHeaderAndFooter":{"CenterUnit":'',"LeftUnit":'',"BottomUnit":'', "RightUnit":'', "TopUnit":''},
        "ThreeColumnsLeftDivided":{"CenterUnit":'',"RightUnit":'',
            "LeftUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "ThreeColumnsRightDivided":{"CenterUnit":'',"LeftUnit":'',
            "RightUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "ThreeColumnsLeftAndRightDivided":{
            "LeftUnit":{"TopUnit":'',"CenterUnit":''},
            "CenterUnit":'',
            "RightUnit":{"TopUnit":'',"CenterUnit":''}
        },
        "ThreeColumnsAllDivided":{
            "LeftUnit":{"TopUnit":'',"CenterUnit":''},
            "CenterUnit":{"TopUnit":'',"CenterUnit":''},
            "RightUnit":{"TopUnit":'',"CenterUnit":''}
        }
    }

    this.menuParameters = {
        'SearchGrid':{
            'node':{
                "params.data": "JavaScript object representing the row data",
                "params.menuId": "Name of the menu item.",
                "params.parentId": "Parent menu item name, if it is a sub menu."
            }
        },
        'SearchList':{
            'node':{
                "params.data": "JavaScript object representing the row data",
                "params.menuId": "Name of the menu item.",
                "params.parentId": "Parent menu item name, if it is a sub menu."
            },
            'property':{
                "params.data": "JavaScript object representing the row data",
                "params.menuId": "Name of the menu item.",
                "params.key": "Name of the clicked property.",
                "params.value": "Value of the clicked property."
            }
        },
        'TreeGrid':{
            'node':{
                "params.data": "JavaScript object representing the row data",
                "params.menuId": "Name of the menu item.",
                "params.parentId": "Parent menu item name, if it is a sub menu."
            }
        },
        'ObjectMap':{
            'node':{
                "params.data": "JavaScript object representing the row data",
                "params.menuId": "Name of the menu item."
            },
            'toolbar':{
                "params.menuId": "Name of the menu item."
            }

        }
    }
}

var UIConfig = YAHOO.rapidjs.designer.Config;