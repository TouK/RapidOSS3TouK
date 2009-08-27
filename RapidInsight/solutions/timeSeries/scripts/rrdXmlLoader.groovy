import groovy.xml.*;
dsname = params.name;
if(dsname == null)
{
    throw new Exception("Variable name should be specified")
}

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);

def dataUtil = RrdVariable.get(name:params.name);
def map = dataUtil.fetchAllData();

long now = Date.now();
mb.RootTag{
    mb.Variable(Target:"UpperBand"){
        map.each{String time,  value->
            value = value == Double.NaN?0:value;
            mb.Data(time:Long.parseLong(time)*1000, value:value);
        }
    }
    mb.Annotations{
        RsEvent.searchEvery("elementName:${dataUtil.resource.exactQuery()}").each{ event ->
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
	