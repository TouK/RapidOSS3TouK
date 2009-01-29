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
        return this.get(itemType)["canBeDeleted"];
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
    this.isChildMultiple = function(itemType, childType){
        return this.get(itemType)['children'][childType]['isMultiple']
    };
    this.getPropertyDescription = function(itemType, propertyName) {
        if (itemType == "Layout" && propertyName == "type") {
            return "Type of the layout"
        }
        return this.get(itemType)["properties"][propertyName]["descr"]
    };
    this.isPropertyRequired = function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["required"]
    };
    this.getPropertyType = function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["type"]
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
    this.methods = {
        "SearchGrid":{
            "poll":{
                descr:"Refreshes component's data with its already saved request url and parameters.",
                args:{}
            },
            "refresh":{
                descr:"Refreshes component's data with the given request parameters",
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
            }
        },
        "Html":{

        },
        "AutoComplete":{

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
                descr:"Fired when the connection is lost with the server",params:{}
            },
            "serverUp":{
                descr:"Fired when the connection is established with the server",params:{}
            }
        },
        "SearchGrid":{
            "propertyClicked":{
                descr:"Fired when a cell of the grid is clicked",
                params:{
                    "params.key":"Name of the property",
                    "params.value":"Value of the property",
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowClicked":{
                descr:"Fired when a row of the grid is clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowDoubleClicked":{
                descr:"Fired when a row of the grid is double clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "selectionChanged":{
                descr:"Fired when the row selection is changed",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked",
                params:{
                    "params.query":"Query written in search input field"
                }
            }
        },
        "SearchList":{
            "propertyClicked":{
                descr:"Fired when a cell of the grid is clicked",
                params:{
                    "params.key":"Name of the property",
                    "params.value":"Value of the property",
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowClicked":{
                descr:"Fired when a row of the grid is clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowDoubleClicked":{
                descr:"Fired when a row of the grid is double clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "selectionChanged":{
                descr:"Fired when the row selection is changed",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked",
                params:{
                    "params.query":"Query written in search input field"
                }
            },
            "rowHeaderClick":{
                descr:"Fired when row header is clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            }
        },
        "TreeGrid":{

        },
        "Timeline":{

        },
        "PieChart":{

        },
        "FlexPieChart":{
            "propertyClicked":{
                descr:"Fired when a cell of the grid is clicked",
                params:{
                    "params.key":"Name of the property",
                    "params.value":"Value of the property",
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowClicked":{
                descr:"Fired when a row of the grid is clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "rowDoubleClicked":{
                descr:"Fired when a row of the grid is double clicked",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "selectionChanged":{
                descr:"Fired when the row selection is changed",
                params:{
                    "params.data":"JavaScript object representing the row data"
                }
            },
            "saveQueryClicked":{
                descr:"Fired when save query button is clicked",
                params:{
                    "params.query":"Query written in search input field"
                }
            }
        },
        "GMap":{

        },
        "Html":{

        },
        "AutoComplete":{

        },
        "ObjectMap":{

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

//    this.config = {
//        'Urls':{
//            display:'Urls', properties:{}, children:['Url'],
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
//        },
//        'Url':{
//            display:{from:'url'},
//            canBeDeleted:true,
//            properties:{
//                'url':{type:'String', descr:'Url of the page set', required:true}
//            },
//            image:{expanded:'images/rapidjs/designer/gsp_logo.png', collapsed:'images/rapidjs/designer/gsp_logo.png'},
//            children:["Tabs"]
//
//        },
//        'Tabs':{display:'Tabs',properties:{},children:["Tab"],
//            image:{expanded:'images/rapidjs/designer/tab.png', collapsed:'images/rapidjs/designer/tab.png'}
//        },
//        'Tab':{
//            display:{from:'name'},
//            canBeDeleted:true,
//            properties:{
//                'name':{type:'String', descr:'Name of the tab', required:true}
//            },
//            image:{expanded:'images/rapidjs/designer/page.png', collapsed:'images/rapidjs/designer/page.png'},
//            children:['Layout', 'Components', 'Dialogs', 'Forms', 'Actions', "JavaScript"]
//        },
//        'Layout':{
//            display:'Layout',
//            properties:{},
//            canBeDeleted:true,
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'},
//            children:['CenterUnit', 'TopUnit', 'BottomUnit', 'LeftUnit', 'RightUnit']
//        },
//        'CenterUnit':{
//            display:'Center',
//            properties:{
//                "htmlContent":"",
//                "component":{type:"String", descr:"RapidInsight component that will be displayed in the unit"},
//                "gutter": {type:"String", descr:"The gutter applied to the unit's wrapper, before the content."},
//                "scroll": {type:"Boolean", descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."}
//            },
//            children:['Layout'],
//            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}
//        },
//        'TopUnit':{
//            display:'Top',
//            canBeDeleted:true,
//            properties:{
//                "height": {type:"Number", descr:"The height (in pixels) that the unit will take up in the layout", required:true},
//                "component":{type:"String", descr:"RapidInsight component that will be displayed in the unit"},
//                "resize": {type:"Boolean", descr:"Boolean indicating whether this unit is resizeable."},
//                "gutter": {type:"String", descr:"The gutter applied to the unit's wrapper, before the content."},
//                "scroll": {type:"Boolean", descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."}
//            },
//            children:['Layout'],
//            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
//        'BottomUnit':{
//            display:'Bottom',
//            canBeDeleted:true,
//            properties:{
//                "height": {type:"Number", descr:"The height (in pixels) that the unit will take up in the layout.", required:true},
//                "component":{type:"String", descr:"RapidInsight component that will be displayed in the unit"},
//                "resize": {type:"Boolean", descr:"Boolean indicating whether this unit is resizeable."},
//                "gutter": {type:"String", descr:"The gutter applied to the unit's wrapper, before the content."},
//                "scroll": {type:"Boolean", descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."}
//            },
//            children:['Layout'],
//            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
//        'LeftUnit':{
//            display:'Left',
//            canBeDeleted:true,
//            properties:{
//                "width": {type:"Number", descr:"The width (in pixels) that the unit will take up in the layout.", required:true},
//                "component":{type:"String", descr:"RapidInsight component that will be displayed in the unit"},
//                "resize": {type:"Boolean", descr:"Boolean indicating whether this unit is resizeable."},
//                "gutter": {type:"String", descr:"The gutter applied to the unit's wrapper, before the content."},
//                "scroll": {type:"Boolean", descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."}
//            },
//            children:['Layout'],
//            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
//        'RightUnit':{
//            display:'Right',
//            canBeDeleted:true,
//            properties:{
//                "width": {type:"Number", descr:"The width  pixels) that the unit will take up in the layout.", required:true},
//                "component":{type:"String", descr:"RapidInsight component that will be displayed in the unit"},
//                "resize": {type:"Boolean", descr:"Boolean indicating whether this unit is resizeable."},
//                "gutter": {type:"String", descr:"The gutter applied to the unit's wrapper, before the content."},
//                "scroll": {type:"Boolean", descr:"Boolean indicating whether the unit's body should have scroll bars if the body content is larger than the display area."}
//            },
//            children:['Layout'],
//            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
//        'Components':{display:'Components', properties:{}, children:["FlexPieChart"],
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
//        },
//        'Dialogs':{display:'Dialogs', properties:{}, children:["Dialog"],
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
//        },
//        'Dialog':{
//            display:{from:'component'},
//            canBeDeleted:true,
//            properties:{
//                'component':{type:'String', descr:'RapidInsight component that will be displayed as pop up dialog', required:true},
//                'width':{type:'Number', descr:'Width of the dialog', required:true},
//                'height':{type:'Number', descr:'Height of the dialog', required:true},
//                'minWidth':{type:'Number', descr:'Minimum width of the dialog'},
//                'minHeight':{type:'Number', descr:'Minimum height of the dialog'},
//                'maxHeight':{type:'Number', descr:'Maximum height of the dialog'},
//                'maxWidth':{type:'Number', descr:'Maximum width of the dialog'},
//                'title':{type:'String', descr:'Title of the dialog'}
//            },
//            image:{expanded:'images/rapidjs/designer/application_double.png', collapsed:'images/rapidjs/designer/application_double.png'},
//            children:[]
//        },
//        'Forms':{display:'Forms', properties:{}, children:["Form"],
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
//        },
//        'Form':{
//            display:{from:'id'},
//            canBeDeleted:true,
//            properties:{
//                'id':{type:'String', descr:'The unique id of the component which is stored in the global JavaScript object YAHOO.rapidjs.Components', required:true},
//                'width':{type:'Number', descr:'Width of the form', required:true},
//                'saveUrl':{type:'String', descr:'Form submit url, when the form is opened in "create" mode', required:true},
//                'updateUrl':{type:'String', descr:'Form submit url, when the form is opened in "edit" mode'},
//                'createUrl':{type:'String', descr:'The server url which brings values to populate form fields when the form is opened in "create" mode'},
//                'editUrl':{type:'String', descr:'The server url which brings values to populate form fields when the form is opened in "edit" mode'},
//                'submitAction':{type:{inList:['GET', 'POST']}, descr:'Action type of the form. Available properties are "GET" and "POST". Default is "GET"'}
//            },
//            image:{expanded:'images/rapidjs/designer/application_form.png', collapsed:'images/rapidjs/designer/application_form.png'},
//            children:[]
//        },
//        'Actions':{display:'Actions', properties:{}, children:["RequestAction", "FunctionAction"],
//            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
//        },
//        'JavaScript':{display:'JavaScript', properties:{}, children:[],
//            image:{expanded:'images/rapidjs/designer/javascript.gif', collapsed:'images/rapidjs/designer/javascript.gif'}
//        },
//
//        'FlexPieChart':{
//            display:{from:'name'},
//            canBeDeleted:true,
//            properties:{
//                'name':{type:'String', descr:'The unique id of the component which is stored in the global JavaScript object YAHOO.rapidjs.Components', required:true},
//                'url':{type:'String', descr:'The default URL to be used for requests to the server to retrieve the data', required:true},
//                'rootTag':{type:'String', descr:'The root node name of AJAX response which FlexPieChart takes as starting point to get its data', required:true},
//                'swfURL':{type:'String', descr:'The url to the .swf flash file', required:true},
//                'title':{type:'String', descr:'FlexPieChart title'},
//                'pollingInterval':{type:'Number', descr:'Time delay between two server requests'}
//            },
//            image:{expanded:'images/rapidjs/designer/component.gif', collapsed:'images/rapidjs/designer/component.gif'},
//            children:[]
//        },
//
//        "RequestAction":{
//            display:{from:'name'},
//            canBeDeleted:true,
//            properties:{
//                'name':{type:'String', descr:'', required:true},
//                'url':{type:'String', descr:'', required:true},
//                'condition':{type:'String', descr:''},
//                'timeout':{type:'Number', descr:''},
//                'event':{type:'String', descr:'', required:true}
//            },
//            image:{expanded:'', collapsed:''},
//            children:["RequestParameter"]
//        },
//        "FunctionAction":{
//            display:{from:'name'},
//            canBeDeleted:true,
//            properties:{
//                'name':{type:'String', descr:'', required:true},
//                'component':{type:'String', descr:'', required:true},
//                'function':{type:'String', descr:'', required:true},
//                'condition':{type:'String', descr:''},
//                'event':{type:'String', descr:'', required:true}
//            },
//            image:{expanded:'', collapsed:''},
//            children:["RequestParameter"]
//        },
//        "RequestParameter":{
//            display:{from:'key'},
//            canBeDeleted:true,
//            properties:{
//                'key':{type:'String', descr:'', required:true},
//                'value':{type:'String', descr:'', required:true}
//            },
//            image:{expanded:'', collapsed:''},
//            children:[]
//        },
//        "FunctionArgument":{
//            display:'FunctionArgument',
//            canBeDeleted:true,
//            properties:{
//                'value':{type:'String', descr:'', required:true}
//            },
//            image:{expanded:'', collapsed:''},
//            children:[]
//        }
//    }
}

var UIConfig = YAHOO.rapidjs.designer.Config;