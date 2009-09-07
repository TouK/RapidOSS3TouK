import groovy.xml.MarkupBuilder
import org.compass.core.converter.basic.DateMathParser
import org.codehaus.groovy.grails.commons.ApplicationHolder

def buttonName = params.lastSelectedButton;
def field = params.field;
def searchClassName = params.searchClass;
def query = params.query;
if(query == "" || query == null)
{
    query = "alias:*"
}
def incrementAmount = 1;
def duration  = "hours"
def numberOfIntervals = 60;
def searchDoaminClass = ApplicationHolder.application.getDomainClass(searchClassName)
if(searchDoaminClass == null)
{
    throw new Exception("searchClass with name ${searchClassName} could not be found.");
}
def searchClass = ApplicationHolder.application.getDomainClass(searchClassName).clazz;
if(buttonName == null)
{
    duration = "months"
}
else if(buttonName == "Last Hour")
{
    duration = "minutes"
}
else if(buttonName == "Last Day")
{
    duration = "hours"
    numberOfIntervals = 24;
}
else if(buttonName == "Last Month")
{
    duration = "days"
    numberOfIntervals = 30;
}

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
def parser = new DateMathParser(TimeZone.getDefault(), Locale.getDefault());
mb.Datum{
    numberOfIntervals.times{
        def timeToBeParsed = "-${it+1}${duration}";
        def time = parser.parseMath(timeToBeParsed).getTime() 
        def tmpQuery = "(${query}) AND ${field}:[currenttime-${it+1}${duration} TO currenttime-${it}${duration}]"
        def numberOfObjects = searchClass.countHits(tmpQuery);
        mb.Data(time:time, value:numberOfObjects);
    }
}
println sw
return sw.toString();
