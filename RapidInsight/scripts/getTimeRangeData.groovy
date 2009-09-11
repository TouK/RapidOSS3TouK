import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ApplicationHolder

public static long MINUTE = 60*1000
public static long HOUR = 60*MINUTE
public static long DAY = 24*HOUR
public static long MONTH = 30*DAY
public static long YEAR = 12*MONTH
public static SimpleDateFormat FULL_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
public static SimpleDateFormat TOOLTIP_DF = new SimpleDateFormat("HH:mm EE, MMM dd, yyyy");
public static SimpleDateFormat YEAR_DF = new SimpleDateFormat("yyyy");
public static SimpleDateFormat MONTH_DF = new SimpleDateFormat("yyyy-MM");
public static SimpleDateFormat DAY_DF = new SimpleDateFormat("dd");
public static SimpleDateFormat HOUR_DF = new SimpleDateFormat("HH:mm");
public static SimpleDateFormat MINUTE_DF = new SimpleDateFormat("mm");
public static SimpleDateFormat YEAR_START_DF = new SimpleDateFormat("yyyy-MM-01 00:00:00");
public static SimpleDateFormat MONTH_START_DF = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
public static SimpleDateFormat DAY_START_DF = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
public static SimpleDateFormat HOUR_START_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
public static SimpleDateFormat MINUTE_START_DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


def buttonName = params.lastSelectedButton;
def field = params.field;
def searchClassName = params.searchClass;
def query = params.query;
if(query == "" || query == null)
{
    query = "alias:*"
}
def cTime = new Date();
def searchDoaminClass = ApplicationHolder.application.getDomainClass(searchClassName)
if(searchDoaminClass == null)
{
    throw new Exception("searchClass with name ${searchClassName} could not be found.");
}
def searchClass = ApplicationHolder.application.getDomainClass(searchClassName).clazz;

if(buttonName == "Last Hour")
{
    timeInterval = MINUTE
    df = MINUTE_DF;
    numberOfintervals = 60;
    startDate = FULL_DF.parse(HOUR_START_DF.format(new Date(cTime.getTime()-numberOfintervals*timeInterval)));
}
else if(buttonName == "Last Day")
{
    timeInterval = HOUR
    df = HOUR_DF;
    numberOfintervals = 24;
    startDate = FULL_DF.parse(DAY_START_DF.format(new Date(cTime.getTime()-numberOfintervals*timeInterval)));
}
else if(buttonName == "Last Month")
{
    timeInterval = DAY
    df = DAY_DF;
    numberOfintervals = 30;
    startDate = FULL_DF.parse(MONTH_START_DF.format(new Date(cTime.getTime()-numberOfintervals*timeInterval)));
}
else
{
    timeInterval = MONTH
    df = MONTH_DF;
    numberOfintervals = 30;
    startDate = FULL_DF.parse(YEAR_START_DF.format(new Date(cTime.getTime()-numberOfintervals*timeInterval)));
}

def sw = new StringWriter();
def mb = new MarkupBuilder(sw);
long lowestTime = startDate.getTime();
def time = lowestTime;
def start = 0;
def results = [];
def queryThreads = Collections.synchronizedList([]);
while(time <= cTime.getTime() || start < numberOfintervals){

    long lowerTime = time
    long upperTime = time+timeInterval;
    def willShowLabel = start%3==0;
    Thread t = Thread.start{
        def tmpQuery = "(${query}) AND ${field}:[${lowerTime} TO ${upperTime}]"
        def numberOfObjects = searchClass.countHits(tmpQuery);
        def data = [fromTime:lowerTime, toTime:upperTime, value:numberOfObjects, timeAxisLabel:willShowLabel?df.format(new Date(lowerTime)):"",
                stringFromTime:FULL_DF.format(new Date(lowerTime)), stringToTime:FULL_DF.format(new Date(upperTime)),
                tooltip:"${numberOfObjects} events between ${TOOLTIP_DF.format(new Date(lowerTime))}  and ${TOOLTIP_DF.format(new Date(upperTime))}"]
        results.add(data);
    }
    queryThreads.add(t);
    start++;
    time = upperTime;
}
queryThreads.each {
    it.join();
}
mb.Datum(interval:timeInterval){
    results = results.sort {it.fromTime}
    results.each{
        mb.Data(it);
    }
}
return sw.toString();
