import ui.designer.*;

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jan 22, 2009
* Time: 4:09:59 PM
*/
DESIGNER_KEY = "designer_key"
DESIGNER_TYPE = "designer_type"
def urls = UiUrl.list();
web.render(contentType: 'text/xml') {
    Items() {
        Item("${DESIGNER_KEY}": "1", "${DESIGNER_TYPE}": "Root") {
            urls.each {UiUrl url ->
                Item("${DESIGNER_KEY}": url.id, "${DESIGNER_TYPE}": "Url", url: url.url) {
                    Item("${DESIGNER_KEY}": "tabs${url.id}", "${DESIGNER_TYPE}": "Tabs", url: url.url) {
                        def tabs = url.tabs;
                        tabs.each {UiTab tab ->
                           Item("${DESIGNER_KEY}": tab.id, "${DESIGNER_TYPE}": "Tab", name: tab.name){
                               Item("${DESIGNER_KEY}": tab.layout.id, "${DESIGNER_TYPE}": "Layout"){
                                   def layoutUnits = tab.layout.units;
                                   layoutUnits.each{UiLayoutUnit layoutUnit->
                                       def type = layoutUnit.type;
                                       def designerType = type.substring(0,1).toUpperCase()+type.substring(1);
                                       Item("${DESIGNER_KEY}": layoutUnit.id, "${DESIGNER_TYPE}": "Layout_${designerType}") 
                                   }
                               }
                               Item("${DESIGNER_KEY}": "components${tab.id}", "${DESIGNER_TYPE}": "Components")
                               Item("${DESIGNER_KEY}": "dialogs${tab.id}", "${DESIGNER_TYPE}": "Dialogs")
                               Item("${DESIGNER_KEY}": "forms${tab.id}", "${DESIGNER_TYPE}": "Forms")
                               Item("${DESIGNER_KEY}": "actions${tab.id}", "${DESIGNER_TYPE}": "Actions")
                               Item("${DESIGNER_KEY}": "javascript${tab.id}", "${DESIGNER_TYPE}": "JavaScript")
                           }
                        }
                    }
                }
            }
        }
    }
}
