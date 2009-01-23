YAHOO.namespace("rapidjs", "rapidjs.designer");

YAHOO.rapidjs.designer.Config = new function(){
    this.get = function(itemType) {
        return this.config[itemType];
    };
    this.getDisplayName = function(itemType, xmlNode) {
        var display = this.get(itemType)["display"]
        if (typeof display == "object") {
            if (xmlNode) {
                return xmlNode.getAttribute(display["from"])
            }
            else {
                return itemType;
            }
        }
        else {
            return display;
        }

    };
    this.canBeDeleted = function(itemType) {
        return this.get(itemType)["canBeDeleted"];
    };
    this.getProperties = function(itemType) {
        return this.get(itemType)["properties"];
    };
    this.isDisplayProperty = function(itemType, propertyName) {
        var display = this.get(itemType)["display"]
        return typeof display == "object" && display["from"] == propertyName
    };
    this.getChildren = function(itemType) {
        return this.get(itemType)["children"]
    };
    this.getPropertyDescription =function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["descr"]
    };
    this.isPropertyRequired =function(itemType, propertyName) {
        return this.get(itemType)["properties"][propertyName]["required"]
    };
    this.getImageConfig = function(itemType) {
        return this.get(itemType)["image"];
    };
    this.getConfig = function(){
       return this.config;
    };
    this.config =  {
        'Root':{
            display:'Urls', properties:{}, children:['Url'],
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
        },
        'Url':{
            display:{from:'url'},
            canBeDeleted:true,
            properties:{
                'url':{type:'String', descr:'Url of the page set', required:true}
            },
            image:{expanded:'images/rapidjs/designer/gsp_logo.png', collapsed:'images/rapidjs/designer/gsp_logo.png'},
            children:["Tabs"]

        },
        'Tabs':{display:'Tabs',properties:{},children:["Tab"],
            image:{expanded:'images/rapidjs/designer/tab.png', collapsed:'images/rapidjs/designer/tab.png'}
        },
        'Tab':{
            display:{from:'name'},
            canBeDeleted:true,
            properties:{
                'name':{type:'String', descr:'Name of the tab', required:true}
            },
            image:{expanded:'images/rapidjs/designer/page.png', collapsed:'images/rapidjs/designer/page.png'},
            children:['Layout', 'Components', 'Dialogs', 'Forms', 'Actions', "JavaScript"]
        },
        'Layout':{
            display:'Layout',
            properties:{},
            canBeDeleted:true,
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'},
            children:['Layout_Center', 'Layout_Top', 'Layout_Bottom', 'Layout_Left', 'Layout_Right']
        },
        'Layout_Center':{display:'Center', properties:{}, children:['Layout'],
            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}
        },
        'Layout_Top':{display:'Top', canBeDeleted:true, properties:{}, children:['Layout'],
            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
        'Layout_Bottom':{display:'Bottom', canBeDeleted:true, properties:{}, children:['Layout'],
            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
        'Layout_Left':{display:'Left', canBeDeleted:true, properties:{}, children:['Layout'],
            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
        'Layout_Right':{display:'Right', canBeDeleted:true, properties:{}, children:['Layout'],
            image:{expanded:'images/rapidjs/designer/layout_content.png', collapsed:'images/rapidjs/designer/layout_content.png'}},
        'Components':{display:'Components', properties:{}, children:["FlexPieChart"],
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
        },
        'Dialogs':{display:'Dialogs', properties:{}, children:["Dialog"],
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
        },
        'Dialog':{
            display:{from:'component'},
            canBeDeleted:true,
            properties:{
                'component':{type:'String', descr:'RapidInsight component that will be displayed as pop up dialog', required:true},
                'width':{type:'Number', descr:'Width of the dialog', required:true},
                'height':{type:'Number', descr:'Height of the dialog', required:true},
                'minWidth':{type:'Number', descr:'Minimum width of the dialog'},
                'minHeight':{type:'Number', descr:'Minimum height of the dialog'},
                'maxHeight':{type:'Number', descr:'Maximum height of the dialog'},
                'maxWidth':{type:'Number', descr:'Maximum width of the dialog'},
                'title':{type:'String', descr:'Title of the dialog'}
            },
            image:{expanded:'images/rapidjs/designer/application_double.png', collapsed:'images/rapidjs/designer/application_double.png'},
            children:{}
        },
        'Forms':{display:'Forms', properties:{}, children:["Form"],
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
        },
        'Form':{
            display:{from:'id'},
            canBeDeleted:true,
            properties:{
                'id':{type:'String', descr:'The unique id of the component which is stored in the global JavaScript object YAHOO.rapidjs.Components', required:true},
                'width':{type:'Number', descr:'Width of the form', required:true},
                'saveUrl':{type:'String', descr:'Form submit url, when the form is opened in "create" mode', required:true},
                'updateUrl':{type:'String', descr:'Form submit url, when the form is opened in "edit" mode'},
                'createUrl':{type:'String', descr:'The server url which brings values to populate form fields when the form is opened in "create" mode'},
                'editUrl':{type:'String', descr:'The server url which brings values to populate form fields when the form is opened in "edit" mode'},
                'submitAction':{type:{inList:['GET', 'POST']}, descr:'Action type of the form. Available properties are "GET" and "POST". Default is "GET"'}
            },
            image:{expanded:'images/rapidjs/designer/application_form.png', collapsed:'images/rapidjs/designer/application_form.png'},
            children:{}
        },
        'Actions':{display:'Actions', properties:{}, children:[],
            image:{expanded:'images/rapidjs/component/tools/folder_open.gif', collapsed:'images/rapidjs/component/tools/folder.gif'}
        },
        'JavaScript':{display:'JavaScript', properties:{}, children:[],
            image:{expanded:'images/rapidjs/designer/javascript.gif', collapsed:'images/rapidjs/designer/javascript.gif'}
        },

        'FlexPieChart':{
            display:{from:'id'},
            canBeDeleted:true,
            properties:{
                'id':{type:'String', descr:'The unique id of the component which is stored in the global JavaScript object YAHOO.rapidjs.Components', required:true},
                'url':{type:'String', descr:'The default URL to be used for requests to the server to retrieve the data', required:true},
                'rootTag':{type:'String', descr:'The root node name of AJAX response which FlexPieChart takes as starting point to get its data', required:true},
                'swfURL':{type:'String', descr:'The url to the .swf flash file', required:true},
                'title':{type:'String', descr:'FlexPieChart title'},
                'pollingInterval':{type:'Number', descr:'Time delay between two server requests'}
            },
            image:{expanded:'images/rapidjs/designer/component.gif', collapsed:'images/rapidjs/designer/component.gif'},
            children:{}
        }
    }
}

var UIConfig = YAHOO.rapidjs.designer.Config;