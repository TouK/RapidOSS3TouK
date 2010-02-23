YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.Config = new function() {
    this.config = {};
    this.helpConfig = {};
    this.componentWizardScenarioMap = null;
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
    this.loadHelp = function(response) {
        var helpNodes = response.responseXML.getElementsByTagName("Help");
        for (var i = 0; i < helpNodes.length; i++) {
            var helpNode = helpNodes[i]
            var helpId = helpNode.getAttribute("id");
            var helpText = helpNode.firstChild.nodeValue;
            this.helpConfig[helpId] = helpText;
        }
    };
    this.get = function(itemType) {
        return this.config[itemType];
    };
    this.getHelp = function(itemType) {
        var helpId = this.get(itemType)["help"];
        if (helpId) {
            return this.helpConfig[helpId] || "";
        }
        return "";
    };
    this.getDisplayName = function(itemType, xmlNode) {
        var displayFromProperty = this.get(itemType)["displayFromProperty"]
        if (displayFromProperty) {
            if (xmlNode) {
                return xmlNode.getAttribute(displayFromProperty)
            }
            else {
                return this.get(itemType)["display"] || itemType;
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
        else if (itemType == "FunctionAction" && propertyName.match(/arg\d+/)) {
            return "";
        }
        return this.get(itemType)["properties"][propertyName]["descr"]
    };
    this.getPropertyDefaultValue = function(itemType, propertyName) {
        if (itemType == "FunctionAction" && propertyName.match(/arg\d+/)) {
            return "";
        }
        return this.get(itemType)["properties"][propertyName]["defaultValue"]
    };
    this.isPropertyRequired = function(itemType, propertyName) {
        if (itemType == "FunctionAction" && propertyName.match(/arg\d+/)) {
            return true;
        }
        return this.get(itemType)["properties"][propertyName]["required"] == 'true'
    };
    this.getPropertyType = function(itemType, propertyName) {
        if (itemType == "FunctionAction" && propertyName.match(/arg\d+/)) {
            return "Expression";
        }
        return this.get(itemType)["properties"][propertyName]["type"]
    };
    this.getPropertyInList = function(itemType, propertyName) {
        if (itemType == "FunctionAction" && propertyName.match(/arg\d+/)) {
            return [];
        }
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
    this.createComponentWizardScenarioMap = function() {
        if (!this.componentWizardScenarioMap) {
            var cMap = {};
            for (var scenario in this.wizardScenarios) {
                var components = this.wizardScenarios[scenario]['components'];
                for (var i = 0; i < components.length; i++) {
                    var component = components[i];
                    if (!cMap[component]) {
                        cMap[component] = [];
                    }
                    cMap[component].push(scenario);
                }
            }
            this.componentWizardScenarioMap = cMap;
        }
    };
    this.isWizardAvailable = function(itemType) {
        this.createComponentWizardScenarioMap();
        return this.componentWizardScenarioMap[itemType] != null
    };
    this.getWizardScenariosForComponent = function(itemType) {
        this.createComponentWizardScenarioMap();
        return this.componentWizardScenarioMap[itemType]
    };
    this.getWizardScenarios = function() {
        return this.wizardScenarios;
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
    this.getItemEvents = function(itemType) {
        return this.events[itemType];
    };
    this.getEventDescription = function(itemType, eventName) {
        return this.events[itemType][eventName]['descr'];
    };
    this.getEventParameters = function(itemType, eventName) {
        return this.events[itemType][eventName]['params'];
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
                    "searchIn":"The class that the search will be applied",
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
                    "searchIn":"The class that the search will be applied",
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
            "addSort":{
                descr:"Adds the given property and order to the current sorting parameters for multiple field sorting",
                args:{
                    "sortAttribute":"Property to add to the sorting parameters",
                    "sortOrder":"The order of the sort. Possible values are 'asc' and 'desc'"
                }
            },
            "removeSort":{
                descr:"Removes the given property and order from the current sorting parameters for multiple field sorting",
                args:{
                    "sortAttribute":"Property to remove from the sorting parameters"
                }
            },
            "clearSorting":{
                descr:"Clears all sorting parameters",
                args:{}
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
        "FusionChart":{
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
        "FlexLineChart":{
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
            },
            "showAnnotationDetails":{
                descr:"Shows details of selected annotation.",
                args:{
                    "time":"Annotation's timestamp"
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
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters and already saved url.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
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
        "Autocomplete":{
            "setTitle":{
                descr:"Changes component's title.",
                args:{
                    "title":"Component's new title"
                }
            }
        },
        "AudioPlayer":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters and already saved url.",
                args:{
                    "params":"JavaScript object containing request parameter key-value pairs",
                    "title":"Component's new title"
                }
            },
            "play":{
                descr:"Plays the specified sound file.",
                args:{}
            },
            "stop":{
                descr:"Stops the playing audio.",
                args:{}
            },
            "resume":{
                descr:"Resumes the paused audio.",
                args:{}
            },
            "pause":{
                descr:"Pauses the playing audio.",
                args:{}
            },
            "mute":{
                descr:"Mutes the audio.",
                args:{}
            },
            "unmute":{
                descr:"Unmutes the audio.",
                args:{}
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
            },
            "loadMapForNode":{
                descr:"Loads the map for the given object name.",
                args:{
                    "nodeParams":"Map of properties that will identify the node",
                    "mapParams":"Map of properties that will identify the map"
                }
            },
            "loadMap":{
                descr:"Loads the map from AJAX response.",
                args:{
                    "response":"AJAX response object which includes map structure"
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
                    "params.datas":"List of JavaScript objects representing the selected rows data"
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
                    "params.datas":"List of JavaScript objects representing the selected rows data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked.",
                params:{
                    "params.query":"Query written in search input field"
                }
            },
            "rowHeaderClicked":{
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
                    "params.datas":"List of JavaScript objects representing the selected rows data"
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
        "AudioPlayer":{

        },
        "FlexPieChart":{
            "itemClicked":{
                descr:"Fired when a chart slice is clicked",
                params:{
                    "params.data":"JavaScript object representing the slice data"
                }
            }
        },
        "FusionChart":{
            "itemClicked":{
                descr:"Fired when a chart set is clicked",
                params:{
                    "params.data":"JavaScript object representing the set data"
                }
            }
        },
        "FlexLineChart":{
            "itemClicked":{
                descr:"Fired when a chart annotation is clicked",
                params:{
                    "params.data":"JavaScript object representing the clicked annotation"
                }
            },
            "rangeChanged":{
                descr:"Fired when main chart range is changed",
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
            },
            "lineClicked":{
                descr:"Fired when a line is clicked",
                params:{
                    "params.data":"JavaScript object representing the line data"
                }
            },
            "iconClicked":{
                descr:"Fired when an icon is clicked",
                params:{
                    "params.data":"JavaScript object representing the icon data"
                }
            }
        },
        "Html":{

        },
        "Autocomplete":{
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
        },
        "RequestAction":{
            "success": {
                descr:"Fired when server side AJAX call successfully executed.",
                params:{
                    "params.response": "JavaScript AJAX response object returned from server."
                }
            },
            "error":{
                descr:"Fired when server side AJAX response contains error messages",
                params:{
                    "params.messages":"List of error messages."
                }
            },
            "timeout":{
                descr:"Fired when server cannot response to the client in timeout interval",
                params:{}
            },
            "unknownUrl":{
                descr:"Fired when action's url is not available.",
                params:{}
            },
            "internalServerError":{
                descr:"Fired when an internal server error occurred.",
                params:{}
            },
            "serverDown":{
                descr:"Fired when server does not response to the AJAX call.",
                params:{}
            }
        },
        "MergeAction":{
            "success": {
                descr:"Fired when server side AJAX call successfully executed.",
                params:{
                    "params.response": "JavaScript AJAX response object returned from server."
                }
            },
            "error":{
                descr:"Fired when server side AJAX response contains error messages",
                params:{
                    "params.messages":"List of error messages."
                }
            },
            "timeout":{
                descr:"Fired when server cannot response to the client in timeout interval",
                params:{}
            },
            "unknownUrl":{
                descr:"Fired when action's url is not available.",
                params:{}
            },
            "internalServerError":{
                descr:"Fired when an internal server error occurred.",
                params:{}
            },
            "serverDown":{
                descr:"Fired when server does not response to the AJAX call.",
                params:{}
            }
        },
        "FunctionAction":{
            "success": {
                descr:"Fired when the method call successfully executed.",
                params:{}
            },
            "error":{
                descr:"Fired when the method call cannot be executed successfully.",
                params:{
                    "params.messages":"List of error messages."
                }
            }
        },
        "ExecuteJavascriptAction":{
            "success": {
                descr:"Fired when the js code successfully executed.",
                params:{}
            },
            "error":{
                descr:"Fired when the js code cannot be executed successfully.",
                params:{
                    "params.messages":"List of error messages."
                }
            }
        },
        "LinkAction":{
            "error":{
                descr:"Fired when the action cannot be executed successfully.",
                params:{
                    "params.messages":"List of error messages."
                }
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
            },
            'multiple':{
                "params.datas": "List of JavaScripts object representing the selected rows data",
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
            'multiple':{
                "params.datas": "List of JavaScripts object representing the selected rows data",
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
    this.wizardScenarios = {
        'Add a menu item to execute a shell script':{
            constructor: YAHOO.rapidjs.designer.ShellScriptScenario,
            components: ['SearchGrid', 'SearchList', 'TreeGrid', 'ObjectMap']
        },
        'Add a menu item to pop up a form':{
            constructor: YAHOO.rapidjs.designer.MenuToFormScenario,
            components: ['SearchGrid', 'SearchList', 'TreeGrid', 'ObjectMap']
        },
        'Add a menu item to launch an external web page':{
            constructor: YAHOO.rapidjs.designer.MenuToLinkScenario,
            components: ['SearchGrid', 'SearchList', 'TreeGrid', 'ObjectMap']
        },
        'Make grid column hyperlink':{
            constructor: YAHOO.rapidjs.designer.ColumnLinkScenario,
            components: ['SearchGrid']
        }
    }
}

var UIConfig = YAHOO.rapidjs.designer.Config;