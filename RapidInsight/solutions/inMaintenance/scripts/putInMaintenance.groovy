import java.text.SimpleDateFormat
import com.ifountain.rcmdb.domain.util.ControllerUtils
import org.quartz.Trigger
import org.quartz.TriggerUtils
import org.quartz.impl.calendar.BaseCalendar
import groovy.xml.MarkupBuilder

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 4:03:26 PM
* To change this template use File | Settings | File Templates.
*/
def objectName = params.objectName;
def type = params.maintenanceType;
def formatter = new SimpleDateFormat("MMM dd yyyy HH:mm:ss")
if (type == "maintenance")
{
    if (objectName)
    {
        def inMaintenance = params.inMaintenance;
        def minutes = params.minutes;
        def source = "User ${web.session.username}";
        def info = params.info;

        if (inMaintenance)
        {
            if (minutes)
            {
                try {
                    minutes = minutes.toInteger();
                }
                catch (e)
                {
                    throw new Exception("Minutes should be an integer");
                }

                def endTime = new Date(new Date().getTime() + (1000 * 60 * minutes))
                def props = ["objectName": objectName, "source": source, "info": info, "ending": endTime];
                RsInMaintenance.putObjectInMaintenance(props, true);
            }
            else
            {
                def props = ["objectName": objectName, "source": source, "info": info];
                RsInMaintenance.putObjectInMaintenance(props, true);
            }
            return ControllerUtils.convertSuccessToXml("Object ${params.objectName} is put into maintenance")
        }
        else
        {
            RsInMaintenance.takeObjectOutOfMaintenance(objectName);
            return ControllerUtils.convertSuccessToXml("Object ${params.objectName} is taken out of maintenance")
        }
    }

}
else if (type == "schedule")
{
    if (params.mode == 'delete') {
        RsInMaintenanceSchedule.removeSchedule(params.scheduleId);
        return ControllerUtils.convertSuccessToXml("Shedule with id ${params.scheduleId} deleted.")
    }
    else {
        def scheduleType = params.scheduleType.toInteger();
        def props = [type: scheduleType, objectName: params.objectName, info: params.info]
        if (scheduleType == RsInMaintenanceSchedule.RUN_ONCE) {
            props.maintStarting = getDate(params.starting, params.starting_hour.toInteger(), params.starting_minute.toInteger());
            props.maintEnding = getDate(params.ending, params.ending_hour.toInteger(), params.ending_minute.toInteger());
        }
        else {
            props.maintStarting = getDate(null, params.maintStarting_hour.toInteger(), params.maintStarting_minute.toInteger())
            props.maintEnding = getDate(null, params.maintEnding_hour.toInteger(), params.maintEnding_minute.toInteger())
            props.schedStarting = getDate(params.schedStarting, 0, 0);
            props.schedEnding = getDate(params.schedEnding, 23, 59);
            if (scheduleType == RsInMaintenanceSchedule.WEEKLY) {
                def daysOfWeek = params.daysOfWeek ? params.daysOfWeek : '';
                if (daysOfWeek instanceof List) {
                    daysOfWeek = daysOfWeek.join(',');
                }
                else if (daysOfWeek instanceof String[]) {
                    daysOfWeek = Arrays.asList(daysOfWeek).join(',')
                }
                if (daysOfWeek.trim().length() == 0) {
                    return web.errorMessagesToXml(['You should select at least one day.'])
                }
                props.daysOfWeek = daysOfWeek;
            }
            else if (scheduleType == RsInMaintenanceSchedule.MONTHLY_BY_DATE) {
                def daysOfMonth = params.daysOfMonth ? params.daysOfMonth : '';
                if (daysOfMonth instanceof List) {
                    daysOfMonth = daysOfMonth.join(',');
                }
                else if (daysOfMonth instanceof String[]) {
                    daysOfMonth = Arrays.asList(daysOfMonth).join(',')
                }
                if (daysOfMonth.trim().length() == 0) {
                    return web.errorMessagesToXml(['You should select at least one day.'])
                }
                props.daysOfMonth = daysOfMonth;
            }
        }
        if (params.mode == "calculateFireTimes") {
            props.name = "dummy"
            Trigger trigger = RsInMaintenanceSchedule.getTrigger(props);
            def fireTimes = TriggerUtils.computeFireTimes(trigger, new BaseCalendar(), 20)
            def sw = new StringWriter();
            def builder = new MarkupBuilder(sw);
            builder.FireTimes() {
                fireTimes.each {fireTime ->
                    builder.FireTime(time: formatter.format(fireTime))
                }
            }
            return sw.toString();
        }
        else {
            def schedule = RsInMaintenanceSchedule.addObjectSchedule(props);
            if (schedule.hasErrors()) {
                return web.errorsToXml(schedule.errors)
            }
            else {
                return ControllerUtils.convertSuccessToXml("shedule for ${params.objectName} created")
            }
        }
    }

}


def getDate(dateString, hour, minute)
{
    Date date;
    if (dateString != null && dateString != '') {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        date = format.parse(dateString)
    }
    else {
        date = new Date(0)
    }

    def calendar = Calendar.getInstance()
    calendar.clear()
    calendar.setLenient(false);
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    return calendar.getTime();
}
