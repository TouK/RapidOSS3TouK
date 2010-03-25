package message

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Mar 16, 2010
* Time: 5:13:43 PM
*/
class RsMessageRuleCalendarOperations extends AbstractDomainOperation {
    def afterDelete() {
        RsMessageRule.searchEvery("calendarId:${id}").each {
            it.update(calendarId: 0)
        }
    }
    public static RsMessageRuleCalendar addCalendar(Map props) {
        if (props.isPublic) props.username = RsUser.RSADMIN;
        def user = RsUser.get(username: props.username);
        if (!user) {
            throw new Exception("No user defined with username '${props.username}'")
        }
        def startingHour = props.startingHour ? props.startingHour.toInteger() : 0;
        def endingHour = props.endingHour ? props.endingHour.toInteger() : 0;
        def startingMinute = props.startingMinute ? props.startingMinute.toInteger() : 0;
        def endingMinute = props.endingMinute ? props.endingMinute.toInteger() : 0;
        props.starting = getTimeFromHourAndMinute(startingHour, startingMinute)
        props.ending = getTimeFromHourAndMinute(endingHour, endingMinute)
        props.daysString = getDaysString(props.days);
        return RsMessageRuleCalendar.add(props);
    }
    public static RsMessageRuleCalendar updateCalendar(RsMessageRuleCalendar cal, Map props) {
        if (props.isPublic) props.username = RsUser.RSADMIN;
        if (props.username) {
            def user = RsUser.get(username: props.username);
            if (!user) {
                throw new Exception("No user defined with username '${props.username}'")
            }
        }
        if (props.startingHour != null || props.startingMinute != null) {
            def startingHour = props.startingHour ? props.startingHour.toInteger() : 0;
            def startingMinute = props.startingMinute ? props.startingMinute.toInteger() : 0;
            props.starting = getTimeFromHourAndMinute(startingHour, startingMinute)
        }
        if (props.endingHour != null || props.endingMinute != null) {
            def endingHour = props.endingHour ? props.endingHour.toInteger() : 0;
            def endingMinute = props.endingMinute ? props.endingMinute.toInteger() : 0;
            props.ending = getTimeFromHourAndMinute(endingHour, endingMinute)
        }
        if (props.days) props.daysString = getDaysString(props.days);
        cal.update(props);
        return cal;
    }
    private static String getDaysString(String days) {
        def daysMap = ["1": "Sun", "2": "Mon", "3": "Tue", "4": "Wed", "5": "Thu", "6": "Fri", "7": "Sat"]
        if (days) {
            def daysArray = Arrays.asList(days.split(","));
            def dayDisplayArray = [];
            daysArray.each {
                if (it.trim() != '') {
                    dayDisplayArray.add(daysMap[it.trim()]);
                }
            }
            return dayDisplayArray.join(',');
        }
        return "";
    }
    private static long getTimeFromHourAndMinute(int hour, int minute) {
        Calendar cal = new GregorianCalendar()
        cal.setTime(new Date(0));
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        return cal.getTime().getTime();
    }
}