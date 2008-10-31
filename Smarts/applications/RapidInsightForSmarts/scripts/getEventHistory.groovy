import java.sql.Timestamp;
import java.text.SimpleDateFormat;

def timeStampFormat = "MMM dd yyyy HH:mm:ss";
def formatter = new SimpleDateFormat(timeStampFormat);
def nodeType = params.nodeType;
def name = params.name;

def historicalEvents = null;
if(nodeType == "Container"){
    historicalEvents = RsSmartsHistoricalNotification.search("className:\"${name}\"", [max:10000000]).results;
}
else{
   historicalEvents = RsSmartsHistoricalNotification.search("instanceName:\"${name}\"", [max:10000000]).results;
}

web.render(contentType: 'text/xml'){
   data(){
       historicalEvents.each{RsSmartsHistoricalNotification historicalEvent ->
           def start = formatter.format(new Timestamp(historicalEvent.lastNotifiedAt)) + " GMT";
           def end = formatter.format(new Timestamp(historicalEvent.lastClearedAt)) + " GMT";
           def title = historicalEvent.instanceName + " " + historicalEvent.eventName;
           event(title:title, start:start, end:end, isDuration:"true", historicalEvent.eventText)
       }
   }
}