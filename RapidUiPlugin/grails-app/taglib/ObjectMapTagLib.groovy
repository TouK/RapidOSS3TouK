import com.ifountain.rui.util.TagLibUtils

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Oct 16, 2008
* Time: 10:40:36 AM
* To change this template use File | Settings | File Templates.
*/
class ObjectMapTagLib {
    static namespace = "rui";
    static def fObjectMap(attrs, bodyString) {
        def topoMapId = attrs["id"];
        def configXML = "<ObjectMap>${bodyString}</ObjectMap>";
        def onNodeClick = attrs["onNodeClick"];

        def nodeClickJs;
        if (onNodeClick != null) {
            nodeClickJs = """
               ${topoMapId}tm.events['nodeClicked'].subscribe(function(data, clickedItems){
                   var params = {data:data, clickedItems:clickedItems};
                   YAHOO.rapidjs.Actions['${onNodeClick}'].execute(params);
                }, this, true);
            """
        }
        def menuEvents = [:]
        def toolbarMenuEvents = [:]
        def menuEventsJs;
        def toolbarMenuEventsJs;
        def configStr = getConfig(attrs, configXML, menuEvents, toolbarMenuEvents);
        if (menuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            menuEvents.each {id, action ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(menuId == '${id}'){
                   YAHOO.rapidjs.Actions['${action}'].execute(params);
                }
                """
                index++;
            }
            menuEventsJs = """
               ${topoMapId}tm.events['nodeMenuItemClicked'].subscribe(function(menuId, data){
                   var params = {data:data, menuId:menuId};
                   ${innerJs}
                }, this, true);
            """

        }
        if (toolbarMenuEvents.size() > 0) {
            def innerJs = "";
            def index = 0;
            toolbarMenuEvents.each {id, action ->
                innerJs += index == 0 ? "if" : "else if";
                innerJs += """(menuId == '${id}'){
                   YAHOO.rapidjs.Actions['${action}'].execute(params);
                }
                """
                index++;
            }
            toolbarMenuEventsJs = """
               ${topoMapId}tm.events['toolbarMenuItemClicked'].subscribe(function(menuId){
                   var params = {menuId:menuId};
                   ${innerJs}
                }, this, true);
            """

        }
        return """
           <script type="text/javascript">
               var ${topoMapId}c = ${configStr};
               var ${topoMapId}container = YAHOO.ext.DomHelper.append(document.body, {tag:'div'});
               var ${topoMapId}tm = new YAHOO.rapidjs.component.TopologyMap(${topoMapId}container, ${topoMapId}c);
               ${nodeClickJs ? nodeClickJs : ""}
               ${menuEventsJs ? menuEventsJs : ""}
               ${toolbarMenuEventsJs ? toolbarMenuEventsJs : ""}
           </script>
        """
    }

    def objectMap = {attrs, body ->
        out << fObjectMap(attrs, body())
    }

    static def getConfig(config, configXML, menuEvents, toolbarMenuEvents) {
        def xml = new XmlSlurper().parseText(configXML);
        def cArray = [];
        cArray.add("id: '${config["id"]}'")
        cArray.add("dataTag: '${config["dataTag"]}'")
        cArray.add("expandURL: '${config["expandURL"]}'")
        cArray.add("dataURL: '${config["dataURL"]}'")
        if (config["title"])
            cArray.add("title:'${config['title']}'")
        if (config["nodeSize"])
            cArray.add("nodeSize:${config['nodeSize']}")
        if (config["pollingInterval"])
            cArray.add("pollingInterval:${config['pollingInterval']}")
        def edgeColors = config["edgeColors"];
        def edgeNodesArray = [];
        edgeColors.each {k, v ->
            edgeNodesArray.add("'${k}':${v}")
        }
        cArray.add("edgeColors:{${edgeNodesArray.join(',')}}")
        def nodeContentArray = [];
        def images = xml.NodeContent.Images.Image;
        def imageArray = [];
        images.each {image ->
            def mappingArray = [];
            def mappings = image.mapping.Item;
            mappings.each {
                mappingArray.add("'${it.@key}':'${it.@value}'")
            }
            imageArray.add("""{
               id:'${image.@id}',
               x:${image.@x},
               y:${image.@y},
               width:${image.@width},
               height:${image.@height},
               dataKey:'${image.@dataKey}',
               images:{${mappingArray.join(',')}}
           }""")
        }
        nodeContentArray.add("images:[${imageArray.join(',\n')}]")
        def texts = xml.NodeContent.Texts.Text;
        def textsArray = [];
        texts.each {text ->
            textsArray.add("""
                {
                   id:'${text.@id}',
                   x:${text.@x},
                   y:${text.@y},
                   width:${text.@width},
                   height:${text.@height},
                   dataKey:'${text.@dataKey}'
                }
              """)
        }
        nodeContentArray.add("texts:[${textsArray.join(',\n')}]")
        def gauges = xml.NodeContent.Gauges.Gauge;
        def gaugesArray = [];
        gauges.each {gauge ->
            gaugesArray.add("""
                {
                   id:'${gauge.@id}',
                   x:${gauge.@x},
                   y:${gauge.@y},
                   width:${gauge.@width},
                   height:${gauge.@height},
                   dataKey:'${gauge.@dataKey}'
                }
              """)
        }
        nodeContentArray.add("gauges:[${gaugesArray.join(',\n')}]")
        cArray.add("nodeContent:{${nodeContentArray.join(',\n')}}")

        def menuItems = xml.MenuItems.MenuItem;
        def menuItemsArray = [];
        menuItems.each {menuItem ->
            def visible = menuItem.@visible.toString().trim();
            def action = menuItem.@action.toString().trim();
            def id = menuItem.@id.toString();
            if(action.length() > 0){
                menuEvents.put(id, action);
            }
            menuItemsArray.add("""{
                id:'${id}',
                ${visible.length() > 0? "visible:\"${visible}\",":""}
                text:'${menuItem.@label}'       
            }""")
        }
        cArray.add("nodeMenuItems:[${menuItemsArray.join(',\n')}]")

        def toolbarMenus = xml.ToolbarMenus.ToolbarMenu;
        def tMenuArray = [];
        def toolbarMenuIndex = 0;
        toolbarMenus.each{toolbarMenu ->
            def label = toolbarMenu.@label;
            def tMenuItems = toolbarMenu.MenuItem;
            def tMenuItemsArray = [];
            tMenuItems.each{tMenuItem ->
                def action = tMenuItem.@action.toString().trim();
                def id = tMenuItem.@id.toString();
                if(action.length() > 0){
                    toolbarMenuEvents.put(id, action);
                }
                tMenuItemsArray.add("""{
                    id:'${id}',
                    text:'${tMenuItem.@label}'
                }""")
            }
            tMenuArray.add("""{
                text:'${label}',
                subMenu:{
                    id:'subMenu${toolbarMenuIndex}',
                    itemdata:[${tMenuItemsArray.join(',\n')}]
                }
            }""")
            toolbarMenuIndex ++;
        }
        cArray.add("toolbarMenuItems:[${tMenuArray.join(',\n')}]")
        return "{${cArray.join(',\n')}}"
    }

    static def fOmNodeContent(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("NodeContent", attrs, [], bodyString)
    }
    def omNodeContent = {attrs, body ->
        out << fOmNodeContent(attrs, body());
    }
    static def fOmImages(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Images", attrs, [], bodyString)
    }
    def omImages = {attrs, body ->
        out << fOmImages(attrs, body());
    }

    static def fOmImage(attrs, bodyString) {
        def validAttrs = ["id", "x", "y", "width", "height", "dataKey", "mapping"];
        return TagLibUtils.getConfigAsXml("Image", attrs, validAttrs)
    }
    def omImage = {attrs, body ->
        out << fOmImage(attrs, "");
    }

    static def fOmTexts(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Texts", attrs, [], bodyString)
    }
    def omTexts = {attrs, body ->
        out << fOmTexts(attrs, body());
    }
    static def fOmText(attrs, bodyString) {
        def validAttrs = ["id", "x", "y", "width", "height", "dataKey"];
        return TagLibUtils.getConfigAsXml("Text", attrs, validAttrs)
    }
    def omText = {attrs, body ->
        out << fOmText(attrs, "");
    }

    static def fOmGauges(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("Gauges", attrs, [], bodyString)
    }
    def omGauges = {attrs, body ->
        out << fOmGauges(attrs, body());
    }
    static def fOmGauge(attrs, bodyString) {
        def validAttrs = ["id", "x", "y", "width", "height", "dataKey"];
        return TagLibUtils.getConfigAsXml("Gauge", attrs, validAttrs)
    }
    def omGauge = {attrs, body ->
        out << fOmGauge(attrs, "");
    }

    static def fOmMenuItems(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("MenuItems", attrs, [], bodyString)
    }
    def omMenuItems = {attrs, body ->
        out << fOmMenuItems(attrs, body());
    }
    static def fOmMenuItem(attrs, bodyString) {
        def validAttrs = ["id", "label", "action", "visible"];
        return TagLibUtils.getConfigAsXml("MenuItem", attrs, validAttrs)
    }
    def omMenuItem = {attrs, body ->
        out << fOmMenuItem(attrs, "");
    }
    static def fOmToolbarMenus(attrs, bodyString) {
        return TagLibUtils.getConfigAsXml("ToolbarMenus", attrs, [], bodyString)
    }
    def omToolbarMenus = {attrs, body ->
        out << fOmToolbarMenus(attrs, body());
    }
    static def fOmToolbarMenu(attrs, bodyString) {
        def validAttrs = ["label"];
        return TagLibUtils.getConfigAsXml("ToolbarMenu", attrs, validAttrs, bodyString)
    }
    def omToolbarMenu = {attrs, body ->
        out << fOmToolbarMenu(attrs, body());
    }
}