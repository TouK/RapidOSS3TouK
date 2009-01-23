import ui.designer.*;

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Jan 23, 2009
* Time: 8:55:18 AM
*/
UiDialog.removeAll();
UiForm.removeAll();
UiAction.removeAll();
UiJavaScript.removeAll();
UiComponent.removeAll();
UiLayoutUnit.removeAll();
UiLayout.removeAll();
UiTab.removeAll();
UiUrl.removeAll();

def isError = false;
def data = params.data;
def urls = new XmlSlurper().parseText(data).Item.Item;
urls.each{urlNode ->
   def url = UiUrl.add(urlNode.attributes());
   if(!url.hasErrors()){
      def tabs = urlNode.Item.Item;
      tabs.each{tabNode ->
          def tabProps = tabNode.attributes();
          tabProps.put("url", url);
          def tab = UiTab.add(tabProps);
          if(!tab.hasErrors()){
              def layout = UiLayout.add(tab:tab);
              def centerUnit = UiLayoutUnit.add(type:UiLayoutUnit.CENTER, parentLayout:layout);
          }
          else{
              renderErrors(tab)
              isError = true;
              return;
          }
      }
   }
   else{
       renderErrors(url);
       isError = true;
       return;
   }
}
if(!isError){
    web.render(contentType:"text/xml"){
        Successful("Configuration successfully saved.");
    }
}


def renderErrors(bean){
    web.render(contentType:"text/xml"){
        Errors(){
            bean.errors.allErrors.each{
                Error(error:it)
            }
        }
    }
}