import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat

def objectName = params.objectName;

def inMaintenanceObj = RsInMaintenance.get(objectName:objectName);
def schedules = RsInMaintenanceSchedule.searchEvery("objectName:${objectName.exactQuery()}")

def sw = new StringWriter();
def builder = new MarkupBuilder(sw);
def formatter1 = new SimpleDateFormat("MMM dd yyyy HH:mm:ss")
def formatter3 = new SimpleDateFormat("MMM dd yyyy")
def formatter2 = new SimpleDateFormat("HH:mm")

builder.MaintenanceData(){
    if(inMaintenanceObj){
        def ending = inMaintenanceObj.ending == null || inMaintenanceObj.ending == new Date(0) ? 'Until manually canceled' : formatter1.format(inMaintenanceObj.ending)
        builder.InMaintenance(objectName:inMaintenanceObj.objectName, source:inMaintenanceObj.source, info:inMaintenanceObj.info, ending:ending, starting:formatter1.format(inMaintenanceObj.starting))
    }
    builder.Schedules(){
        schedules.each{schedule ->
            def maintStart = schedule.schedType == RsInMaintenanceSchedule.RUN_ONCE ? formatter1.format(schedule.maintStarting) : formatter2.format(schedule.maintStarting)
            def maintEnd = schedule.schedType == RsInMaintenanceSchedule.RUN_ONCE ? formatter1.format(schedule.maintEnding) : formatter2.format(schedule.maintEnding)
            def schedStart = schedule.schedType == RsInMaintenanceSchedule.RUN_ONCE ? "" : formatter3.format(schedule.schedStarting)
            def schedEnd = schedule.schedType == RsInMaintenanceSchedule.RUN_ONCE ? "" : formatter3.format(schedule.schedEnding)

            builder.Schedule(id:schedule.id, type:getTypeString(schedule.schedType), maintStarting:maintStart, maintEnding:maintEnd, schedStarting:schedStart, schedEnding:schedEnd, details:getDetails(schedule));
        }
    }
}
def getDetails(schedule){
    if(schedule.schedType == RsInMaintenanceSchedule.MONTHLY_BY_DATE){
        return "Days Of Month: ${schedule.daysOfMonth}";
    }
    else if(schedule.schedType == RsInMaintenanceSchedule.WEEKLY){
        def days = schedule.daysOfWeek.split(",");
        def convertedDays = [];
        days.each{day ->
            day = day.trim();
            switch(day){
                case "1": convertedDays.add("Sun");break;
                case "2": convertedDays.add("Mon");break;
                case "3": convertedDays.add("Tue");break;
                case "4": convertedDays.add("Wed");break;
                case "5": convertedDays.add("Thu");break;
                case "6": convertedDays.add("Fri");break;
                case "7": convertedDays.add("Sat");break;
                default: break;
            }
        }
        return "Days Of Week: ${convertedDays.join(', ')}"
    }
    return "";
}
def getTypeString(type){
    switch(type){
        case RsInMaintenanceSchedule.RUN_ONCE: return "Once";
        case RsInMaintenanceSchedule.DAILY: return "Daily";
        case RsInMaintenanceSchedule.WEEKLY: return "Weekly";
        case RsInMaintenanceSchedule.MONTHLY_BY_DATE: return "Monthly";
        default: return null;
    }
}
return sw.toString();