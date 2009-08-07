import groovy.xml.*;
dsname = params.name;

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);

def dataUtil = RrdVariable.get(name:params.name);
def map = dataUtil.fetchAllData();
def variable = map.keySet().toArray()[0];
map = map[variable];

long now = Date.now();
def mapArray = map.keySet().toArray();
mb.RootTag{
    mb.Variable(Target:"UpperBand"){
        map.keySet().each{
            mb.Data(time:Long.parseLong(it)*1000, value:map[it]);
        }
    }
    mb.Annotations{
        RsEvent.searchEvery("elementName:${dataUtil.resource}").each{ event ->
            def eventPropToDisplay=["severity","count"];
            def eventProps=[time:event.changedAt,label:event.name];
            eventPropToDisplay.each{  propName ->
                eventProps[propName]=event.getProperty(propName);
            }
            mb.Annotation(eventProps);
        }
    }
}
return sw.toString();
	